package be.sddevelopment.validation.dsl.rules;

import be.sddevelopment.validation.core.Constraint;
import be.sddevelopment.validation.core.ModularRuleset.ModularValidatorBuilder;
import be.sddevelopment.validation.dsl.CsvFile;
import be.sddevelopment.validation.dsl.FieldValue;
import be.sddevelopment.validation.dsl.SpecificationParserException;

import java.util.List;
import java.util.Optional;
import java.util.Vector;
import java.util.function.Function;

import static be.sddevelopment.commons.exceptions.ExceptionSuppressor.ignore;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;

/**
 * <p>
 * A `CsvValidationRules` instance is created with a list of known rule specifications {@link CsvRuleSpec}.
 * These specifications are used to create new rules based on the input line.
 * The rules are then added to a {@link ModularValidatorBuilder} instance, which is returned to the caller.
 * <br />
 * A {@link CsvValidationRules} singleton object with basic rule specifications is provided by the {@link #defaultRules()} method.
 * </p>
 * <p>
 * Extensibility of the code is ensured due to the `CsvValidationRules` following the [Open-Closed principle](https://en.wikipedia.org/wiki/Open%E2%80%93closed_principle)
 * by disallowing the class itself to be modfied (`final`) but allowing new RuleSpecifications to be added dynamically. As such, the class is mutable.
 * </p>
 *
 * @since 1.1.0-SNAPSHOT
 */
public final class CsvValidationRules {

    private static final List<CsvRuleSpec> BASE_RULE_SPECIFICATIONS = List.of(
            new CsvRuleSpec("Field", CsvValidationRules::createFieldExistsRule),
            new CsvRuleSpec("Distinct", CsvValidationRules::createFieldDistinctnessRule),
            new CsvRuleSpec("Unique", CsvValidationRules::createFieldDistinctnessRule),
            new CsvRuleSpec("UniqueField", CsvValidationRules::createFieldDistinctnessRule),
            new CsvRuleSpec("RecordExists", ignore(CsvValidationRules::containsRecord))
    );
    private static final CsvValidationRules DEFAULT_RULES = new CsvValidationRules(BASE_RULE_SPECIFICATIONS);

    /**
     * To ensure that the class is thread-safe, the `knownRuleSpecifications` field is specified as a synchronized {@link Vector} collection
     * instead of a simple {@link List}.
     */
    private Vector<CsvRuleSpec> knownRuleSpecifications;

    public CsvValidationRules(List<CsvRuleSpec> knownRuleSpecifications) {
        this.knownRuleSpecifications = new Vector<>(knownRuleSpecifications);
    }

    public Function<ModularValidatorBuilder<CsvFile>, ModularValidatorBuilder<CsvFile>> fromLine(String line)
            throws SpecificationParserException {
        return knownRuleSpecifications()
                .stream().parallel()
                .map(spec -> spec.toRule(line.trim()))
                .flatMap(Optional::stream)
                .findFirst()
                .orElseThrow(() -> new SpecificationParserException("Unknown rule specification: %s".formatted(line)));
    }

    public List<CsvRuleSpec> knownRuleSpecifications() {
        return knownRuleSpecifications;
    }

    public boolean isKnownSpecification(String specificationLine) {
        return knownRuleSpecifications().stream().anyMatch(spec -> spec.accepts(specificationLine.trim()));
    }

    public ModularValidatorBuilder<CsvFile> addToRuleset(ModularValidatorBuilder<CsvFile> ruleSet, String ruleToAdd) throws SpecificationParserException {
        return fromLine(ruleToAdd).apply(ruleSet);
    }

    public static RuleSpecificationAppender<CsvFile> createFieldExistsRule(String line) {
        var field = CsvRuleSpec.parametersFrom(line).getFirst();
        return ruleset -> ruleset.must(haveField(field));
    }

    public static RuleSpecificationAppender<CsvFile> createFieldDistinctnessRule(String line) {
        var field = CsvRuleSpec.parametersFrom(line).getFirst();
        return ruleset -> ruleset
                .must(haveField(field))
                .must(haveDistinctValuesFor(field));
    }

    public static RuleSpecificationAppender<CsvFile> containsRecord(String line) throws SpecificationParserException {
        var parameters = CsvRuleSpec.parametersFrom(line);
        if (parameters.size() < 2) {
            throw new SpecificationParserException("RecordExists rule requires at least two parameters");
        }

        var identifier = FieldValue.withValue(parameters.getFirst(), parameters.get(1));
        return ruleSet -> ruleSet.must(containRecord(identifier));
    }

    public static Constraint<CsvFile> haveField(String field) {
        return new Constraint<>(
                file -> file.headerFields().contains(field),
                "Field '%s' must exist in the data file".formatted(field)
        );
    }

    public static Constraint<CsvFile> haveDistinctValuesFor(String field) {
        return new Constraint<>(
                file -> file.lines().size() == file.distinctValuesFor(field).size(),
                "Field '%s' must have distinct values in the data file".formatted(field)
        );
    }

    public static Constraint<CsvFile> containRecord(FieldValue identifier, FieldValue... fieldValues) {
        return new Constraint<>(
                file -> identifiedBy(identifier).apply(file)
                        .map(line -> stream(fieldValues).parallel()
                                .allMatch(expected -> expected.value().equals(line.get(file.fieldIndex(expected.field()))))
                        )
                        .orElse(false),
                fieldValues.length == 0
                        ? "Record identified by %s must exist in the data file".formatted(identifier.toString())
                        : "Record identified by %s with values [%s] must exist in the data file".formatted(identifier.toString(), stream(fieldValues).map(FieldValue::toString).collect(joining(" and ")))
        );
    }

    public static Function<CsvFile, Optional<Vector<String>>> identifiedBy(FieldValue identifier) {
        return file -> file.findLineByFieldValue(identifier.field(), identifier.value());
    }

    public static CsvValidationRules defaultRules() {
        return DEFAULT_RULES;
    }

    public void addRuleSpecification(CsvRuleSpec csvRuleSpec) {
        if (isKnownSpecification(csvRuleSpec.ruleName())) {
            throw new IllegalStateException("Rule specification '%s' is already registered".formatted(csvRuleSpec.ruleName()));
        }
        this.knownRuleSpecifications.add(csvRuleSpec);
    }
}
