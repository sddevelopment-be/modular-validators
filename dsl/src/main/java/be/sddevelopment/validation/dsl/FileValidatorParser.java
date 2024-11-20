package be.sddevelopment.validation.dsl;

import be.sddevelopment.commons.annotations.Utility;
import be.sddevelopment.validation.core.ModularRuleset;
import be.sddevelopment.validation.core.ModularRuleset.ModularValidatorBuilder;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;

import static be.sddevelopment.commons.access.AccessProtectionUtils.utilityClassConstructor;

@Utility
public final class FileValidatorParser {

    private FileValidatorParser() {
        utilityClassConstructor();
    }

    public static ModularRuleset<CsvFile> fromSpecification(Path validationSpec) throws SpecificationParserException {
        try {
            var lines = Files.readAllLines(validationSpec);
            var ruleSet = ModularRuleset.aValid(CsvFile.class);
            lines.stream()
                    .filter(StringUtils::isNotBlank)
                    .filter(FileValidatorParser::isRuleSpecification)
                    .map(FileValidatorParser::<CsvFile>toRuleAdder)
                    .forEach(ruleAdder -> ruleAdder.apply(ruleSet));
            return ruleSet.iHaveSpoken();
        } catch (IOException e) {
            throw new SpecificationParserException("Error processing validation specification file", e);
        }
    }

    private static final List<String> KNOWN_RULESPECS = List.of(
            "RecordIdentifier",
            "FieldExists",
            "FieldPopulated",
            "RecordExists"
    );

    static boolean isRuleSpecification(String specificationLine) {
        return KNOWN_RULESPECS.stream().anyMatch(specificationLine::contains);
    }

    static <T> Function<ModularValidatorBuilder<T>, ModularValidatorBuilder<T>> toRuleAdder(String line) {
        return ruleset -> ruleset;
    }
}
