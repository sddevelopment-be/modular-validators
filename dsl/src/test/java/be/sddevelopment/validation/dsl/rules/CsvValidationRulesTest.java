package be.sddevelopment.validation.dsl.rules;

import be.sddevelopment.commons.exceptions.WrappedException;
import be.sddevelopment.commons.testing.naming.ReplaceUnderscoredCamelCasing;
import be.sddevelopment.validation.core.Rationale;
import be.sddevelopment.validation.dsl.CsvFile;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static be.sddevelopment.validation.core.ModularRuleset.aValid;
import static be.sddevelopment.validation.core.Reason.failed;
import static be.sddevelopment.validation.core.Reason.passed;
import static be.sddevelopment.validation.dsl.rules.CsvValidationRules.defaultRules;
import static java.lang.Boolean.FALSE;
import static java.lang.Thread.sleep;
import static java.util.Collections.emptyList;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.stream.IntStream.range;

@DisplayName("Csv Validation Rules")
@DisplayNameGeneration(ReplaceUnderscoredCamelCasing.class)
class CsvValidationRulesTest implements WithAssertions {

    @Test
    void newRulesCanBeRegistered() {
        var baseRules = new CsvValidationRules(emptyList());
        var notEmpty = new CsvRuleSpec(
                "NotBeEmpty",
                line -> rules -> rules.must(CsvFile::isNotEmpty, "The data file must have at least one record")
        );
        assertThat(notEmpty.ruleName()).matches(Predicate.not(baseRules::isKnownSpecification), "Rule is not known yet");

        baseRules.addRuleSpecification(notEmpty);

        assertThat(notEmpty.ruleName()).matches(baseRules::isKnownSpecification, "Rule is known");
        assertThatNoException().isThrownBy(() -> baseRules.fromLine("NotBeEmpty"));
    }

    @Test
    void ruleIdentifiersMustBeUnique() {
        var baseRules = new CsvValidationRules(emptyList());
        var notEmpty = new CsvRuleSpec(
                "NotBeEmpty",
                line -> rules -> rules.must(CsvFile::isNotEmpty, "The data file must have at least one record")
        );
        assertThat(notEmpty.ruleName()).matches(Predicate.not(baseRules::isKnownSpecification), "Rule is not known yet");

        baseRules.addRuleSpecification(notEmpty);
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> baseRules.addRuleSpecification(notEmpty))
                .withMessage("Rule specification 'NotBeEmpty' is already registered");
    }

    @Test
    void addingRuleSpecificationsIsThreadSafe() {
        var baseRules = new CsvValidationRules(emptyList());
        var notEmpty = new CsvRuleSpec(
                "NotBeEmpty",
                line -> rules -> rules.must(CsvFile::isNotEmpty, "The data file must have at least one record")
        );

        assertThat(notEmpty.ruleName()).matches(Predicate.not(baseRules::isKnownSpecification), "Rule is not known yet");

        SoftAssertions softly = new SoftAssertions();
        try (var threadPool = Executors.newCachedThreadPool()) {
            range(0, 15).forEach(_ignored -> {
                Future<Boolean> result = threadPool.submit(() -> {
                    try {
                        sleep(new Random().nextInt(50, 500));
                        return baseRules.isKnownSpecification("NotBeEmpty");
                    } catch (InterruptedException e) {
                        fail("Error in multithreading test", e);
                        throw new WrappedException(e);
                    }
                });
                softly.assertThat(result).succeedsWithin(1, SECONDS).isNotNull()
                        .matches(FALSE::equals, "Rule is not known during access");
            });
            softly.assertThat(threadPool.submit(() -> {
                try {
                    sleep(new Random().nextInt(150, 500));
                    baseRules.addRuleSpecification(notEmpty);
                } catch (InterruptedException e) {
                    fail("Error in multithreading test", e);
                    throw new WrappedException(e);
                }
            })).succeedsWithin(1, SECONDS);
        } catch (Exception e) {
            fail("Error in multithreading test", e);
            throw e;
        }

        softly.assertAll();
        assertThat(notEmpty.ruleName()).matches(baseRules::isKnownSpecification, "Rule is known after all access completed");
    }

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
            assertThat(RULE_SPEC).matches(defaultRules()::isKnownSpecification);
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
            var fieldExistenceValidator = defaultRules().addToRuleset(baseValidator, RULE_SPEC).iHaveSpoken();

            var result = fieldExistenceValidator.check(dataWithoutSpeciesField);

            assertThat(result).isNotNull().matches(Rationale::isFailing);
            assertThat(result.details())
                    .contains(failed("Field 'SPECIES' must exist in the data file"));
        }

        @Test
        void passesIfHeaderFieldIsPresent() throws Exception {
            var baseValidator = aValid(CsvFile.class);

            var fieldExistenceValidator = defaultRules().addToRuleset(baseValidator, RULE_SPEC).iHaveSpoken();

            var result = fieldExistenceValidator.check(DATA_FILE);

            assertThat(result).isNotNull().matches(Rationale::isPassing);
            assertThat(result.details().getFirst().rationale())
                    .contains("Field 'SPECIES' must exist in the data file");
        }
    }

    @Nested
    class UniqueFieldRule {

        static Stream<Arguments> aliasesForUniqueFieldRule() {
            return Stream.of(
                    Arguments.of("Distinct('NAME')"),
                    Arguments.of("Unique('NAME')"),
                    Arguments.of("UniqueField('NAME')")
            );
        }

        @ParameterizedTest
        @MethodSource("aliasesForUniqueFieldRule")
        void isKnownSpecification(String ruleToParse) {
            assertThat(ruleToParse).matches(defaultRules()::isKnownSpecification);
            assertThatNoException().isThrownBy(() -> defaultRules().fromLine(ruleToParse));
        }

        @ParameterizedTest
        @MethodSource("aliasesForUniqueFieldRule")
        void failsIfHeaderFieldIsNotPresent(String ruleToParse) throws Exception {
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

            var fieldExistenceValidator = defaultRules().addToRuleset(baseValidator, ruleToParse).iHaveSpoken();

            var result = fieldExistenceValidator.check(fileToCheck);

            assertThat(result).isNotNull().matches(Rationale::isFailing);
            assertThat(result.details().getFirst().rationale())
                    .contains("Field 'NAME' must exist in the data file");
        }

        @ParameterizedTest
        @MethodSource("aliasesForUniqueFieldRule")
        void failsIfFieldHasNotUniqueValues(String ruleToParse) throws Exception {
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

            var fieldExistenceValidator = defaultRules().addToRuleset(baseValidator, ruleToParse).iHaveSpoken();

            var result = fieldExistenceValidator.check(fileToCheck);

            assertThat(result).isNotNull().matches(Rationale::isFailing);
            assertThat(result.details())
                    .contains(failed("Field 'NAME' must have distinct values in the data file"));
        }


    }

    @Nested
    class RecordExistenceRule {
        private static final String RULE_SPEC = "RecordExists('NAME', 'C-3PO')";

        @Test
        void isKnownSpecification() {
            assertThat(RULE_SPEC).matches(defaultRules()::isKnownSpecification);
        }

        @Test
        void failsIfRecordIsNotPresent() throws Exception {
            var fileToCheck = CsvFile.fromLines(
                    """
                            NAME, SPECIES
                            Luke Skywalker, Human
                            R2-D2, Droid
                            Obi-Wan Kenobi, Human
                            """.lines().toList()
            );
            var baseValidator = aValid(CsvFile.class);

            var fieldExistenceValidator = defaultRules().addToRuleset(baseValidator, RULE_SPEC).iHaveSpoken();

            var result = fieldExistenceValidator.check(fileToCheck);

            assertThat(result).isNotNull().matches(Rationale::isFailing);
            assertThat(result.details())
                    .contains(
                            failed("Record identified by NAME::'C-3PO' must exist in the data file")
                    );
        }

        @Test
        void passesIfRecordIsPresent() throws Exception {
            var fileToCheck = CsvFile.fromLines(
                    """
                            NAME, SPECIES
                            Luke Skywalker, Human
                            R2-D2, Droid
                            C-3PO, Droid
                            """.lines().toList()
            );
            var baseValidator = aValid(CsvFile.class);

            var fieldExistenceValidator = defaultRules().addToRuleset(baseValidator, RULE_SPEC).iHaveSpoken();

            var result = fieldExistenceValidator.check(fileToCheck);

            assertThat(result).isNotNull().matches(Rationale::isPassing);
            assertThat(result.details())
                    .contains(
                            passed("Record identified by NAME::'C-3PO' must exist in the data file")
                    );
        }

    }
}