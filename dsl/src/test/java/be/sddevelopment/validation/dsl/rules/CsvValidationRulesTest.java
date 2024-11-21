package be.sddevelopment.validation.dsl.rules;

import be.sddevelopment.commons.exceptions.WrappedException;
import be.sddevelopment.commons.testing.naming.ReplaceUnderscoredCamelCasing;
import be.sddevelopment.validation.core.Rationale;
import be.sddevelopment.validation.dsl.CsvFile;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static be.sddevelopment.validation.core.ModularRuleset.aValid;
import static be.sddevelopment.validation.dsl.rules.CsvValidationRules.addToRuleset;

@DisplayName("Csv Validation Rules")
@DisplayNameGeneration(ReplaceUnderscoredCamelCasing.class)
class CsvValidationRulesTest implements WithAssertions {

    @Nested
    class FieldExistenceRule {
        private static final CsvFile DATA_FILE = baseDataFile();

        private static CsvFile baseDataFile() {
            try {
                return CsvFile.fromLines(
                        """
                                NAME,HEIGHT,SPECIES
                                Luke Skywalker,172,Human
                                C-3PO,167,Droid
                                R2-D2,96,Droid
                                Boba Fett,183, Human
                                """.lines().toList()
                );
            } catch (IOException e) {
                throw new WrappedException(e);
            }
        }

        @Test
        void isKnownSpecification() {
            var ruleSpec = "FieldExists('SPECIES')";

            assertThat(ruleSpec).matches(CsvValidationRules::isKnownSpecification);
        }

        @Test
        void failsIfHeaderFieldIsNotPresent() throws Exception {
            var baseValidator = aValid(CsvFile.class);
            var rule = "FieldExists('HOMEWORLD')";

            var fieldExistenceValidator = addToRuleset(baseValidator, rule).iHaveSpoken();

            var result = fieldExistenceValidator.check(DATA_FILE);

            assertThat(result).isNotNull().matches(Rationale::isFailing);
            assertThat(result.details().getFirst().rationale())
                    .contains("Field 'HOMEWORLD' must exist in the data file");
        }

        @Test
        void passesIfHeaderFieldIsPresent() throws Exception {
            var baseValidator = aValid(CsvFile.class);
            var rule = "FieldExists('SPECIES')";

            var fieldExistenceValidator = addToRuleset(baseValidator, rule).iHaveSpoken();

            var result = fieldExistenceValidator.check(DATA_FILE);

            assertThat(result).isNotNull().matches(Rationale::isPassing);
            assertThat(result.details().getFirst().rationale())
                    .contains("Field 'SPECIES' must exist in the data file");
        }
    }
}