package be.sddevelopment.validation.dsl;

import be.sddevelopment.commons.annotations.Utility;
import be.sddevelopment.commons.exceptions.WrappedException;
import be.sddevelopment.validation.core.ModularRuleset;
import be.sddevelopment.validation.dsl.rules.CsvValidationRules;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.Path;

import static be.sddevelopment.commons.access.AccessProtectionUtils.utilityClassConstructor;
import static be.sddevelopment.commons.exceptions.ExceptionSuppressor.uncheck;
import static java.nio.file.Files.readAllLines;

@Utility
public final class CsvFileSpecificationParser {

    private CsvFileSpecificationParser() {
        utilityClassConstructor();
    }

    /**
     * Parses a validation specification file and creates a {@link ModularRuleset} based on the rules specified in the file.
     *
     * @param validationSpec the path to the validation specification file.
     * @throws SpecificationParserException if the validation specification file cannot be read or processed,
     *                                      due to an I/O error or an unknown rule specification.
     */
    public static ModularRuleset<CsvFile> fromSpecification(Path validationSpec) throws SpecificationParserException {
        try {
            var ruleSet = ModularRuleset.aValid(CsvFile.class);
            readAllLines(validationSpec).stream()
                    .filter(StringUtils::isNotBlank)
                    .filter(CsvValidationRules::isKnownSpecification)
                    .map(uncheck(CsvValidationRules::fromLine))
                    .forEach(ruleAdder -> ruleAdder.apply(ruleSet));
            return ruleSet.iHaveSpoken();
        } catch (WrappedException | IOException e) {
            throw new SpecificationParserException("Error processing validation specification file", e);
        }
    }

}
