package be.sddevelopment.validation.dsl.rules;

import be.sddevelopment.commons.exceptions.WrappedException;
import be.sddevelopment.commons.testing.naming.ReplaceUnderscoredCamelCasing;
import be.sddevelopment.validation.core.Rationale;
import be.sddevelopment.validation.core.Reason;
import be.sddevelopment.validation.dsl.CsvFile;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static be.sddevelopment.validation.core.ModularRuleset.aValid;
import static be.sddevelopment.validation.core.Reason.failed;
import static be.sddevelopment.validation.dsl.rules.CsvValidationRules.addToRuleset;

@DisplayName("Csv Validation Rules")
@DisplayNameGeneration(ReplaceUnderscoredCamelCasing.class)
class CsvValidationRulesTest implements WithAssertions {

    @Nested
    class FieldExistenceRule {
        private static final String RULE_SPEC = "Field('SPECIES')";
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
            assertThat(RULE_SPEC).matches(CsvValidationRules::isKnownSpecification);
        }

        @Test
        void failsIfHeaderFieldIsNotPresent() throws Exception {
            var baseValidator = aValid(CsvFile.class);
            var dataWithoutSpeciesField = CsvFile.fromLines(
                    """
                    NAME,HEIGHT
                    Luke Skywalker,172
                    C-3PO,167
                    """.lines().toList()
            );
            var fieldExistenceValidator = addToRuleset(baseValidator, RULE_SPEC).iHaveSpoken();

            var result = fieldExistenceValidator.check(dataWithoutSpeciesField);

            assertThat(result).isNotNull().matches(Rationale::isFailing);
            assertThat(result.details())
                    .contains(failed("Field 'SPECIES' must exist in the data file"));
        }

        @Test
        void passesIfHeaderFieldIsPresent() throws Exception {
            var baseValidator = aValid(CsvFile.class);

            var fieldExistenceValidator = addToRuleset(baseValidator, RULE_SPEC).iHaveSpoken();

            var result = fieldExistenceValidator.check(DATA_FILE);

            assertThat(result).isNotNull().matches(Rationale::isPassing);
            assertThat(result.details().getFirst().rationale())
                    .contains("Field 'SPECIES' must exist in the data file");
        }
    }

    @Nested
    class UniqueFieldRule {
        private static final String UNIQUE_FIELD_SPEC = "UniqueField('NAME')";

        @Test
        void isKnownSpecification() {
            assertThat(UNIQUE_FIELD_SPEC).matches(CsvValidationRules::isKnownSpecification);
        }

        @Test
        void failsIfHeaderFieldIsNotPresent() throws Exception {
            var fileToCheck = CsvFile.fromLines(
                """
                HEIGHT,SPECIES
                172,Human
                167,Droid
                96,Droid
                183, Human
                """.lines().toList()
            );
            var baseValidator = aValid(CsvFile.class);

            var fieldExistenceValidator = addToRuleset(baseValidator, UNIQUE_FIELD_SPEC).iHaveSpoken();

            var result = fieldExistenceValidator.check(fileToCheck);

            assertThat(result).isNotNull().matches(Rationale::isFailing);
            assertThat(result.details().getFirst().rationale())
                    .contains("Field 'NAME' must exist in the data file");
        }

        @Test
        void failsIfFieldHasNotUniqueValues() throws Exception {
            var fileToCheck = CsvFile.fromLines(
                    """
                    NAME,HEIGHT,SPECIES
                    Luke Skywalker,172,Human
                    C-3PO,167,Droid
                    C-3PO,167,Droid
                    R2-D2,96,Droid
                    Boba Fett,183, Human
                    """.lines().toList()
            );
            var baseValidator = aValid(CsvFile.class);

            var fieldExistenceValidator = addToRuleset(baseValidator, UNIQUE_FIELD_SPEC).iHaveSpoken();

            var result = fieldExistenceValidator.check(fileToCheck);

            assertThat(result).isNotNull().matches(Rationale::isFailing);
            assertThat(result.details())
                    .contains(failed("Field 'NAME' must have distinct values in the data file"));
        }


    }
}