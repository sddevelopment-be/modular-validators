# Quick Start: Using Dynamic Descriptions in COREP Validation

## Before vs. After Examples

### Example 1: Amount Validation

#### Before (Static Description)
```java
ModularRuleset.aValid(CorrelatedRecord.class)
    .must(
        record -> record.osxAmount().equals(record.abacusAmount()),
        "Amounts must match"
    )
    .done();
```

**Failure Output:**
```
❌ Amounts must match
```

#### After (Dynamic Description)
```java
ModularRuleset.aValid(CorrelatedRecord.class)
    .must(
        record -> record.osxAmount().equals(record.abacusAmount()),
        record -> {
            if (record.osxAmount().equals(record.abacusAmount())) {
                return "✅ Amounts match: " + record.osxAmount();
            }
            return "❌ Amount mismatch - OSX: " + record.osxAmount() 
                   + ", ABACUS: " + record.abacusAmount() 
                   + " (diff: " + record.osxAmount().subtract(record.abacusAmount()) + ")";
        }
    )
    .done();
```

**Failure Output:**
```
❌ Amount mismatch - OSX: 1500000.00, ABACUS: 1450000.00 (diff: 50000.00)
```

---

### Example 2: Exposure Class Validation

#### Before (Static Description)
```java
public static Constraint<CorrelatedRecord<ExposureRecord>> exposureClassesMatch() {
    return new Constraint<>(
        record -> record.osx().exposureClass().equals(record.abacus().exposureClass()),
        "Exposure classes must match"
    );
}
```

**Failure Output:**
```
❌ Exposure classes must match
```

#### After (Dynamic Description)
```java
public static Constraint<CorrelatedRecord<ExposureRecord>> exposureClassesMatch() {
    return new Constraint<>(
        record -> record.osx().exposureClass().equals(record.abacus().exposureClass()),
        record -> {
            String osxClass = record.osx().exposureClass();
            String abacusClass = record.abacus().exposureClass();
            if (osxClass.equals(abacusClass)) {
                return "✅ Exposure class matches: " + osxClass;
            }
            return "❌ Exposure class mismatch - OSX: '" + osxClass 
                   + "', ABACUS: '" + abacusClass + "'";
        }
    );
}
```

**Failure Output:**
```
❌ Exposure class mismatch - OSX: 'CRE_RESIDENTIAL', ABACUS: 'CRE_COMMERCIAL'
```

---

### Example 3: Row/Column Name Validation

#### Before (Static Description)
```java
public static Constraint<CorrelatedRecord<ExposureRecord>> rowNamesMatch() {
    return new Constraint<>(
        record -> record.osx().rowName().equals(record.abacus().rowName()),
        "Row names must match"
    );
}
```

**Failure Output:**
```
❌ Row names must match
```

#### After (Dynamic Description)
```java
public static Constraint<CorrelatedRecord<ExposureRecord>> rowNamesMatch() {
    return new Constraint<>(
        record -> record.osx().rowName().equals(record.abacus().rowName()),
        record -> String.format(
            record.osx().rowName().equals(record.abacus().rowName())
                ? "✅ Row names match: %s"
                : "❌ Row name mismatch - OSX: '%s', ABACUS: '%s'",
            record.osx().rowName(),
            record.osx().rowName(),
            record.abacus().rowName()
        )
    );
}
```

**Failure Output:**
```
❌ Row name mismatch - OSX: '0020', ABACUS: '0030'
```

---

## Migration Strategy

### Step 1: Update Library Dependency
```xml
<dependency>
    <groupId>be.sddevelopment.validation</groupId>
    <artifactId>core</artifactId>
    <version>1.2.0-SNAPSHOT</version>
</dependency>
```

### Step 2: Convert Constraints Incrementally

**Priority 1: High-Value Constraints**
- Amount validations (show actual numbers)
- Exposure class validations (show actual values)
- Row/Column name validations (show actual codes)

**Priority 2: Medium-Value Constraints**
- Deduplication validations (show how many duplicates)
- Aggregation validations (show expected vs actual sums)

**Priority 3: Low-Value Constraints**
- Simple existence checks (static description is fine)
- Boolean flags (static description is fine)

### Step 3: Simplify Reporting Layer

#### Remove:
```java
// Delete this class entirely
public record ValidationReportContext(
    ValidationResult result,
    List<CorrelatedRecord<ExposureRecord>> correlatedRecords
) {}
```

#### Simplify:
```java
// Before:
public void generateReport(ValidationReportContext context) {
    var result = context.result();
    var records = context.correlatedRecords();  // ← No longer needed
    
    for (var entry : result.failures().entrySet()) {
        var id = entry.getKey();
        var rationale = entry.getValue();
        
        // Find corresponding record to get actual values
        var record = records.stream()
            .filter(r -> r.id().equals(id))
            .findFirst()
            .orElseThrow();
            
        // Manually extract values
        writeCell(record.osx().amount());
        writeCell(record.abacus().amount());
    }
}

// After:
public void generateReport(ValidationResult result) {
    for (var entry : result.failures().entrySet()) {
        var id = entry.getKey();
        var rationale = entry.getValue();
        
        // Rationale already contains all context
        for (var reason : rationale.details()) {
            writeCell(reason.rationale());  // ← Already includes actual values
        }
    }
}
```

---

## Quick Win: One-Line Improvement

For immediate value with minimal change:

```java
// Find all constraints in Corep07ValidationRules.java
// Replace this pattern:

.must(somePredicate, "Static description")

// With this pattern:

.must(
    somePredicate,
    record -> somePredicate.test(record) 
        ? "✅ Static description (validated)" 
        : "❌ Static description - " + record.toString()
)
```

This provides basic context (full record toString) with minimal effort.

---

## Testing Your Changes

### Unit Test Pattern:
```java
@Test
void exposureClassMismatch_includesActualValues() {
    var osxRecord = new ExposureRecord("CRE_RESIDENTIAL", ...);
    var abacusRecord = new ExposureRecord("CRE_COMMERCIAL", ...);
    var correlated = new CorrelatedRecord<>(osxRecord, abacusRecord);
    
    var constraint = Corep07ValidationRules.exposureClassesMatch();
    var reason = constraint.applyTo(correlated);
    
    assertThat(reason.rationale())
        .contains("CRE_RESIDENTIAL")
        .contains("CRE_COMMERCIAL")
        .contains("mismatch");
}
```

### Integration Test Pattern:
```java
@Test
void excelReport_includesContextualFailureMessages() {
    var result = validator.execute(productionRecords, testSuite);
    var reportFile = excelFormatter.format(result);
    
    var sheet = readExcelSheet(reportFile, "Failures");
    
    // Verify actual amounts appear in failure messages
    assertThat(sheet.getCell("B2").getStringCellValue())
        .containsPattern("OSX: \\d+\\.\\d+, ABACUS: \\d+\\.\\d+");
}
```

---

## Expected Benefits

| Metric | Before | After | Improvement |
|--------|--------|-------|-------------|
| Lines of code (reporting) | ~330 | ~100 | -70% |
| Time to diagnose failure | 5-10 min | <1 min | 90% faster |
| False positive investigation | Manual CSV lookup | Immediate context | 100% faster |
| Code coupling | High (leaky abstraction) | Low (clean separation) | ✅ |

---

## Troubleshooting

### Issue: Lambda too complex
**Symptom:** Lambda function is 20+ lines  
**Solution:** Extract to private method:

```java
.must(
    this::amountsMatch,
    this::describeAmountComparison
)

private String describeAmountComparison(CorrelatedRecord<ExposureRecord> record) {
    // Complex logic here
}
```

### Issue: Null pointer in description
**Symptom:** NullPointerException when generating description  
**Solution:** Add null checks:

```java
record -> {
    String osxAmount = record.osx() != null ? record.osx().amount() : "N/A";
    String abacusAmount = record.abacus() != null ? record.abacus().amount() : "N/A";
    return "Amounts - OSX: " + osxAmount + ", ABACUS: " + abacusAmount;
}
```

### Issue: Performance concern with complex descriptions
**Symptom:** Fear that description computation is expensive  
**Reality:** Description only computed for failing validations (typically <1% of records)  
**Solution:** No optimization needed; focus on clarity

---

## Questions?

See `/home/stijn/Documents/_code/_projects/modular-validators/ENHANCEMENT_DYNAMIC_DESCRIPTIONS.md` for complete technical details.
