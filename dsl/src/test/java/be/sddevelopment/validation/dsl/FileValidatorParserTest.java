package be.sddevelopment.validation.dsl;

import be.sddevelopment.commons.testing.naming.ReplaceUnderscoredCamelCasing;
import be.sddevelopment.validation.core.ModularRuleset;
import be.sddevelopment.validation.core.Rationale;
import com.opencsv.CSVParser;
import com.opencsv.bean.util.OpencsvUtils;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.nio.file.Paths;

@DisplayName("Parsing of validation rules")
@DisplayNameGeneration(ReplaceUnderscoredCamelCasing.class)
class FileValidatorParserTest implements WithAssertions {

    @Test
    void createsAValidatorBasedOnSpecifications() throws IOException, SpecificationParserException {
        var validationSpec = Paths.get("src/test/resources/parsing/star_wars/STARWARS_VALIDATOR.puml");
        var dataFile = Paths.get("src/test/resources/parsing/star_wars/STARWARS_INPUT_DATA.csv");
        assertThat(validationSpec).exists();
        assertThat(validationSpec).isRegularFile();

        var validator = FileValidatorParser.fromSpecification(validationSpec);
        assertThat(validator).isNotNull();

        var result = validator.check(CsvFile.fromFile(dataFile));

        assertThat(result).isNotNull()
                .matches(Rationale::isPassing);
    }

    @Nested
    class parsesSimplesRulesTest {

        @ParameterizedTest
        @ValueSource(strings = {
                "RecordIdentifier('NAME')",
                "FieldPopulated('SPECIES')",
                "FieldExists('HOMEWORLD')",
                "RecordExists('C-3PO')"
        })
        void recognizesSimpleRule(String ruleToParse) {
            assertThat(ruleToParse).matches(
                    FileValidatorParser::isRuleSpecification,
                    "is recognized as a rule specification"
            );
        }

        @Test
        void canCheckFieldExistence() {
            var dataFile = CsvFile.fromLines(
                    """
                    NAME,HEIGHT,SPECIES
                    Luke Skywalker,172,Human
                    C-3PO,167,Droid
                    R2-D2,96,Droid
                    Boba Fett,183, Human
                    """.lines().toList()
            );
            var rule = "FieldExists('HOMEWORLD')";
            var ruleAdder = FileValidatorParser.<CsvFile>toRuleAdder(rule);

            var ruleset = ModularRuleset.aValid(CsvFile.class);
            ruleAdder.apply(ruleset);

            var result = ruleset.iHaveSpoken().check(dataFile);

            assertThat(result).isNotNull().matches(Rationale::isFailing, "fails because the field does not exist");
        }
    }
}
