package be.sddevelopment.validation.dsl.rules;

import be.sddevelopment.commons.annotations.Utility;
import be.sddevelopment.validation.core.ModularRuleset.ModularValidatorBuilder;
import be.sddevelopment.validation.dsl.CsvFile;
import be.sddevelopment.validation.dsl.SpecificationParserException;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static be.sddevelopment.commons.access.AccessProtectionUtils.utilityClassConstructor;

@Utility
public final class CsvValidationRules {

    public static final String PARAMETER_SEPARATOR = ",";
    private static final List<CsvRuleSpec> KNOWN_RULESPECS = List.of(
            new CsvRuleSpec("FieldExists", CsvValidationRules::createFieldExistsRule)
    );

    private CsvValidationRules() {
        utilityClassConstructor();
    }

    public static Function<ModularValidatorBuilder<CsvFile>, ModularValidatorBuilder<CsvFile>> fromLine(String line)
            throws SpecificationParserException {
        return knownRuleSpecifications().stream()
                .map(spec -> spec.toRule(line))
                .flatMap(Optional::stream)
                .findFirst()
                .orElseThrow(() -> new SpecificationParserException("Unknown rule specification: %s".formatted(line)));
    }

    public static RuleSpecificationAppender<CsvFile> createFieldExistsRule(String line) {
        var field = CsvRuleSpec.parametersFrom(line).getFirst();
        return ruleset -> ruleset.must(
                file -> file.headerFields().contains(field),
                "Field '%s' must exist in the data file".formatted(field)
        );
    }

    public static List<CsvRuleSpec> knownRuleSpecifications() {
        return KNOWN_RULESPECS;
    }

    public static boolean isKnownSpecification(String specificationLine) {
        return knownRuleSpecifications().stream().anyMatch(spec -> spec.accepts(specificationLine.trim()));
    }

    public static ModularValidatorBuilder<CsvFile> addToRuleset(ModularValidatorBuilder<CsvFile> ruleSet, String ruleToAdd) throws SpecificationParserException {
        return fromLine(ruleToAdd).apply(ruleSet);
    }
}
