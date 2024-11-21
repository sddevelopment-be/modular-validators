package be.sddevelopment.validation.dsl;

import be.sddevelopment.commons.testing.naming.ReplaceUnderscoredCamelCasing;
import be.sddevelopment.validation.core.Rationale;
import be.sddevelopment.validation.dsl.rules.CsvValidationRules;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.nio.file.Paths;

import static java.nio.file.Files.readAllLines;

@DisplayName("Parsing of validation rules")
@DisplayNameGeneration(ReplaceUnderscoredCamelCasing.class)
class CsvFileSpecificationParserTest implements WithAssertions {

    @Test
    void createsAValidatorBasedOnSpecifications() throws SpecificationParserException, IOException {
        var validationSpec = Paths.get("src/test/resources/parsing/star_wars/STARWARS_VALIDATOR.puml");
        var dataFile = Paths.get("src/test/resources/parsing/star_wars/STARWARS_INPUT_DATA.csv");
        assertThat(validationSpec).exists().isRegularFile();

        var validator = CsvFileSpecificationParser.fromSpecification(validationSpec);
        assertThat(validator).isNotNull();

        var result = validator.check(CsvFile.fromFile(dataFile));

        assertThat(result).isNotNull()
                .matches(Rationale::isPassing);
    }

    @Test
    void failsWhenProvidedWithMalformedRuleSpecification() throws Exception {
        var validationSpec = Paths.get("src/test/resources/parsing/star_wars/STARWARS_VALIDATOR_INVALID.puml");
        assertThat(validationSpec).exists().isRegularFile();
        assertThat(readAllLines(validationSpec).stream().map(String::trim).toList())
                .describedAs("contains a malformed rule specification: missing parameter")
                .contains("FieldExists");

        assertThatException()
                .isThrownBy(() -> CsvFileSpecificationParser.fromSpecification(validationSpec))
                .isInstanceOf(SpecificationParserException.class);
    }

    @Nested
    class parsesSimplesRulesTest {

        @Disabled("This test is disabled because the implementation is not yet complete")
        @ParameterizedTest
        @ValueSource(strings = {
                "RecordIdentifier('NAME')",
                "FieldPopulated('SPECIES')",
                "FieldExists('HOMEWORLD')",
                "RecordExists('C-3PO')"
        })
        void recognizesSimpleRule(String ruleToParse) {
            assertThat(ruleToParse).matches(
                    CsvValidationRules::isKnownSpecification,
                    "is recognized as a rule specification"
            );
        }
    }
}
