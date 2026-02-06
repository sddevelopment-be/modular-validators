# Evidence-Based Validation Pattern - Design Analysis

**Date:** 2026-02-06  
**Context:** Proposal for enhancing ModularValidator library with structured evidence capture  
**Status:** Design exploration

## Current State (v1.2.0)

### Dynamic Descriptions
```java
new Constraint<>(
    record -> record.osxAmount().equals(record.abacusAmount()),
    record -> record.osxAmount().equals(record.abacusAmount())
        ? "✅ Amounts match: " + record.osxAmount()
        : "❌ Amount mismatch - OSX: " + record.osxAmount() 
          + ", ABACUS: " + record.abacusAmount()
)
```

**Pros:**
- ✅ Simple, direct
- ✅ Full flexibility in message formatting
- ✅ Works immediately

**Cons:**
- ❌ String concatenation couples data extraction to message formatting
- ❌ Evidence extraction logic repeated in predicate and description
- ❌ Difficult to extract structured data for alternative outputs (JSON, metrics, etc.)
- ❌ Hard to reuse evidence across multiple constraints

## Proposed Enhancement: Evidence-Based Pattern

### Core Concept

Separate **evidence extraction** (what data is relevant) from **evidence presentation** (how to describe it).

```java
new Constraint<>(
    record -> record.osxAmount().equals(record.abacusAmount()),
    record -> Map.of(
        "osxAmount", record.osxAmount(),
        "abacusAmount", record.abacusAmount(),
        "difference", record.osxAmount().subtract(record.abacusAmount()).abs(),
        "tolerance", policy.defaultTolerance()
    ),
    evidence -> {
        BigDecimal diff = (BigDecimal) evidence.get("difference");
        BigDecimal tol = (BigDecimal) evidence.get("tolerance");
        return diff.compareTo(tol) <= 0
            ? "✅ Amounts match: " + evidence.get("osxAmount")
            : "❌ Amount mismatch - OSX: " + evidence.get("osxAmount") 
              + ", ABACUS: " + evidence.get("abacusAmount")
              + " (diff: " + diff + " > tol: " + tol + ")";
    }
)
```

### API Design Option 1: Separate Methods

```java
public record Constraint<T>(
    Predicate<T> rule, 
    Function<T, Map<String, ?>> evidenceExtractor,
    Function<Map<String, ?>, String> evidenceDescriptor
) {
    // Backward-compatible constructors...
    
    public Reason applyTo(T toTest) {
        var result = this.rule().test(toTest);
        var evidence = this.evidenceExtractor().apply(toTest);
        String description = this.evidenceDescriptor().apply(evidence);
        
        return new Reason(
            description, 
            result ? Evaluation.PASS : Evaluation.FAIL,
            evidence  // ← New field in Reason
        );
    }
}
```

### API Design Option 2: Builder Pattern

```java
Constraint.ruleFor(CorrelatedRecord.class)
    .requires(record -> record.osxAmount().equals(record.abacusAmount()))
    .evidence(record -> Map.of(
        "osxAmount", record.osxAmount(),
        "abacusAmount", record.abacusAmount()
    ))
    .describedBy(evidence -> 
        "Amounts: OSX=" + evidence.get("osxAmount") 
        + ", ABACUS=" + evidence.get("abacusAmount")
    )
    .done();
```

### API Design Option 3: Context Object (Most Type-Safe)

```java
public record ValidationContext<T, E> {
    T subject;
    E evidence;
    Evaluation result;
}

public record Constraint<T, E>(
    Predicate<T> rule,
    Function<T, E> evidenceExtractor,
    Function<ValidationContext<T, E>, String> descriptor
) {
    public Reason applyTo(T toTest) {
        var result = this.rule().test(toTest);
        var evidence = this.evidenceExtractor().apply(toTest);
        var context = new ValidationContext<>(toTest, evidence, 
            result ? Evaluation.PASS : Evaluation.FAIL);
        
        String description = this.descriptor().apply(context);
        return new Reason(description, context.result(), evidence);
    }
}
```

**Usage:**
```java
new Constraint<>(
    record -> record.osxAmount().equals(record.abacusAmount()),
    record -> new AmountEvidence(
        record.osxAmount(), 
        record.abacusAmount(),
        record.osxAmount().subtract(record.abacusAmount()).abs()
    ),
    ctx -> ctx.result() == PASS
        ? "✅ Amounts match: " + ctx.evidence().osxAmount()
        : "❌ Amount mismatch: " + ctx.evidence()
)

record AmountEvidence(BigDecimal osxAmount, BigDecimal abacusAmount, BigDecimal difference) {
    @Override
    public String toString() {
        return "OSX: " + osxAmount + ", ABACUS: " + abacusAmount + " (diff: " + difference + ")";
    }
}
```

## Benefits Analysis

### 1. Separation of Concerns ✅

**Current (v1.2.0):**
```java
record -> {
    BigDecimal osxAmount = record.osxAmount();           // ← Extraction
    BigDecimal abacusAmount = record.abacusAmount();     // ← Extraction
    BigDecimal diff = osxAmount.subtract(abacusAmount);  // ← Extraction
    
    return "OSX: " + osxAmount + ", ABACUS: " + abacusAmount;  // ← Formatting
}
```

**Evidence-Based:**
```java
// Extraction (reusable)
record -> new AmountEvidence(record.osxAmount(), record.abacusAmount())

// Formatting (can vary by output format)
evidence -> "OSX: " + evidence.osxAmount() + ", ABACUS: " + evidence.abacusAmount()
```

### 2. Structured Output ✅

**Excel Report:**
```java
for (var reason : failedReasons) {
    var evidence = reason.evidence();
    
    // Direct column mapping
    row.createCell(0).setCellValue(evidence.get("osxAmount"));
    row.createCell(1).setCellValue(evidence.get("abacusAmount"));
    row.createCell(2).setCellValue(evidence.get("difference"));
}
```

**JSON Report:**
```java
{
  "failures": [
    {
      "rule": "Amounts must match",
      "description": "❌ Amount mismatch...",
      "evidence": {
        "osxAmount": 1500000.00,
        "abacusAmount": 1450000.00,
        "difference": 50000.00
      }
    }
  ]
}
```

**Metrics/Alerting:**
```java
// Find all failures with difference > 10000
var largeDiscrepancies = reasons.stream()
    .filter(r -> r.isFailing())
    .filter(r -> ((BigDecimal) r.evidence().get("difference")).compareTo(new BigDecimal("10000")) > 0)
    .toList();
```

### 3. Description Reuse ✅

```java
// Define evidence extractor once
Function<CorrelatedRecord, Map<String, ?>> amountEvidence = 
    record -> Map.of(
        "osxAmount", record.osxAmount(),
        "abacusAmount", record.abacusAmount(),
        "difference", record.osxAmount().subtract(record.abacusAmount()).abs()
    );

// Use different descriptions for different audiences
Function<Map<String, ?>, String> technicalDescription = 
    evidence -> String.format("Amount mismatch detected: OSX=%.2f, ABACUS=%.2f, Δ=%.2f",
        evidence.get("osxAmount"), evidence.get("abacusAmount"), evidence.get("difference"));

Function<Map<String, ?>, String> businessDescription = 
    evidence -> "The reported amount differs from the system of record by " 
        + evidence.get("difference");

// Use in different contexts
new Constraint<>(predicate, amountEvidence, technicalDescription);  // For developers
new Constraint<>(predicate, amountEvidence, businessDescription);   // For business users
```

### 4. Testing & Debugging ✅

```java
@Test
void evidenceExtraction_capturesRelevantFields() {
    var constraint = Corep07ValidationRules.haveAggregatedAmountsWithinTolerance(policy);
    var record = createRecordWithAmounts(...);
    
    var reason = constraint.applyTo(record);
    var evidence = reason.evidence();
    
    // Verify evidence structure (independent of description)
    assertThat(evidence).containsKeys("osxAmount", "abacusAmount", "difference", "tolerance");
    assertThat(evidence.get("osxAmount")).isEqualTo(new BigDecimal("1500000.00"));
}

@Test
void descriptionFormatting_usesEvidence() {
    var evidence = Map.of(
        "osxAmount", new BigDecimal("1500000.00"),
        "abacusAmount", new BigDecimal("1450000.00")
    );
    
    String description = evidenceDescriptor.apply(evidence);
    
    assertThat(description).contains("1500000.00").contains("1450000.00");
}
```

## Drawbacks & Challenges

### 1. Increased Complexity ⚠️

**Simple case becomes verbose:**
```java
// Current (v1.2.0) - Simple
new Constraint<>(
    person -> person.age() >= 18,
    person -> person.age() >= 18 ? "Adult" : "Minor (age: " + person.age() + ")"
)

// Evidence-based - More ceremony
new Constraint<>(
    person -> person.age() >= 18,
    person -> Map.of("age", person.age()),
    evidence -> (int)evidence.get("age") >= 18 ? "Adult" : "Minor (age: " + evidence.get("age") + ")"
)
```

**Mitigation:** Keep both patterns. Evidence is optional for simple cases.

### 2. Type Safety Lost ⚠️

`Map<String, ?>` loses compile-time type checking:
```java
BigDecimal amount = (BigDecimal) evidence.get("osxAmount");  // ← Cast required, can fail at runtime
```

**Mitigation:** Use typed evidence records (Option 3 above):
```java
record AmountEvidence(BigDecimal osxAmount, BigDecimal abacusAmount) {}
```

### 3. Backward Compatibility 🔴

Adding a third parameter breaks existing code.

**Mitigation:** Overloaded constructors + builder pattern:
```java
// Existing (v1.2.0) - Still works
new Constraint<>(predicate, dynamicDescription)

// New (v1.3.0) - Opt-in
new Constraint<>(predicate, evidenceExtractor, evidenceDescriptor)

// Builder
Constraint.ruleFor(T.class)
    .requires(predicate)
    .evidence(extractor)      // ← Optional
    .describedBy(descriptor)
    .done();
```

### 4. Performance Overhead ⚠️

Evidence extraction happens even for passing validations.

**Current:**
```java
// Description only evaluated on failure
person -> person.age() >= 18 ? "Pass" : "Fail (age: " + person.age() + ")"
```

**Evidence-based:**
```java
// Evidence extracted always, even on pass
person -> Map.of("age", person.age())  // ← Happens for every validation
```

**Mitigation:** Lazy evidence extraction:
```java
public Reason applyTo(T toTest) {
    var result = this.rule().test(toTest);
    
    // Only extract evidence if needed (failure or verbose mode)
    Supplier<E> lazyEvidence = () -> evidenceExtractor.apply(toTest);
    
    return new Reason(description, result, lazyEvidence);
}
```

## Recommended Approach

### Phase 1: Add Optional Evidence Support (v1.3.0)

```java
public record Constraint<T>(
    Predicate<T> rule,
    Function<T, String> descriptionBuilder,
    Function<T, Map<String, ?>> evidenceExtractor  // ← New, optional
) {
    // Backward-compatible constructor (v1.2.0 code still works)
    public Constraint(Predicate<T> rule, Function<T, String> descriptionBuilder) {
        this(rule, descriptionBuilder, null);  // ← No evidence by default
    }
    
    // New constructor with evidence
    public Constraint(
        Predicate<T> rule, 
        Function<T, String> descriptionBuilder,
        Function<T, Map<String, ?>> evidenceExtractor
    ) {
        this(rule, descriptionBuilder, evidenceExtractor);
    }
    
    public Reason applyTo(T toTest) {
        var result = this.rule().test(toTest);
        String description = this.descriptionBuilder().apply(toTest);
        
        // Extract evidence if extractor provided
        Map<String, ?> evidence = (evidenceExtractor != null) 
            ? evidenceExtractor.apply(toTest) 
            : Map.of();
        
        return new Reason(description, result, evidence);
    }
}
```

```java
public record Reason(String rationale, Evaluation result, Map<String, ?> evidence) {
    // Backward-compatible constructor
    public Reason(String rationale, Evaluation result) {
        this(rationale, result, Map.of());
    }
}
```

### Phase 2: Evidence-Aware Description (v1.4.0)

After evidence is stable, add evidence-aware description:

```java
public record Constraint<T>(
    Predicate<T> rule,
    Function<T, String> descriptionBuilder,           // ← Kept for backward compat
    Function<T, Map<String, ?>> evidenceExtractor,
    Function<Map<String, ?>, String> evidenceDescriptor  // ← New
) {
    public Reason applyTo(T toTest) {
        var result = this.rule().test(toTest);
        Map<String, ?> evidence = evidenceExtractor.apply(toTest);
        
        // Use evidence-aware descriptor if provided, otherwise fall back
        String description = (evidenceDescriptor != null)
            ? evidenceDescriptor.apply(evidence)
            : descriptionBuilder.apply(toTest);
        
        return new Reason(description, result, evidence);
    }
}
```

## Migration Path for COREP Project

### Current (v1.2.0):
```java
public static Constraint<CorrelatedRecord<ExposureRecord>> haveAggregatedAmountsWithinTolerance(
    TolerancePolicy policy
) {
    return new Constraint<>(
        record -> { /* predicate */ },
        record -> {
            BigDecimal osxTotal = record.aggregateOsxAmount();
            BigDecimal abacusTotal = record.aggregateAbacusAmount();
            BigDecimal difference = osxTotal.subtract(abacusTotal).abs();
            
            return String.format("OSX: %.2f, ABACUS: %.2f, diff: %.4f", 
                osxTotal, abacusTotal, difference);
        }
    );
}
```

### Future (v1.3.0 with Evidence):
```java
public static Constraint<CorrelatedRecord<ExposureRecord>> haveAggregatedAmountsWithinTolerance(
    TolerancePolicy policy
) {
    return new Constraint<>(
        record -> { /* predicate */ },
        record -> {
            var evidence = extractAmountEvidence(record, policy);
            return formatAmountDescription(evidence);
        },
        record -> extractAmountEvidence(record, policy)  // ← Evidence extractor
    );
}

private static Map<String, ?> extractAmountEvidence(CorrelatedRecord<ExposureRecord> record, TolerancePolicy policy) {
    return Map.of(
        "osxAmount", record.aggregateOsxAmount(),
        "abacusAmount", record.aggregateAbacusAmount(),
        "osxCount", record.osxRecords().size(),
        "abacusCount", record.abacusRecords().size(),
        "difference", record.aggregateOsxAmount().subtract(record.aggregateAbacusAmount()).abs(),
        "tolerance", policy.defaultTolerance()
    );
}

private static String formatAmountDescription(Map<String, ?> evidence) {
    // Same formatting logic, but operates on evidence
    return String.format("OSX: %.2f (%d records), ABACUS: %.2f (%d records), diff: %.4f",
        evidence.get("osxAmount"), evidence.get("osxCount"),
        evidence.get("abacusAmount"), evidence.get("abacusCount"),
        evidence.get("difference"));
}
```

### Benefit for Excel Reporting:
```java
// Instead of this (current workaround):
var recordMap = reportContext.correlatedRecords().stream()
    .collect(Collectors.toMap(CorrelatedRecord::canonicalId, r -> r));

for (var id : failedIds) {
    var record = recordMap.get(id);  // ← Lookup needed
    var rationale = result.recordRationales().get(id);
    
    // Manually extract fields
    writeCell(record.osx().amount());
    writeCell(record.abacus().amount());
}

// We could do this:
for (var id : failedIds) {
    var rationale = result.recordRationales().get(id);
    
    for (var reason : rationale.details()) {
        var evidence = reason.evidence();
        
        // Direct evidence access - no record lookup needed!
        writeCell(evidence.get("osxAmount"));
        writeCell(evidence.get("abacusAmount"));
        writeCell(evidence.get("difference"));
    }
}
```

## Recommendation

**Yes, this would be valuable!** But implement incrementally:

### v1.3.0 (Next Release)
- ✅ Add optional `evidenceExtractor` parameter to `Constraint`
- ✅ Add `evidence` field to `Reason`
- ✅ Maintain 100% backward compatibility
- ✅ Let users experiment with evidence extraction

### v1.4.0 (Future)
- Consider `evidenceDescriptor` if v1.3.0 adoption shows value
- Evaluate typed evidence records vs `Map<String, ?>`
- Consider lazy evidence extraction for performance

### For Now
- Continue with dynamic descriptions (v1.2.0)
- They solve 90% of the problem
- Evidence can be added later without breaking changes

---

**Conclusion:** Excellent design insight! Evidence-based validation is a natural evolution of the dynamic description pattern. It provides clean separation of concerns and structured data access. Recommend adding as optional enhancement in v1.3.0.
