package be.sddevelopment.validation;

import net.serenitybdd.junit5.SerenityJUnit5Extension;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDate;
import java.util.Objects;

import static be.sddevelopment.validation.ModularRuleset.aValid;
import static be.sddevelopment.validation.Validations.haveNonNullField;
import static java.time.Month.MARCH;
import static java.util.Optional.ofNullable;

/**
 * Tests illustrating using the validators through close-to-real examples.
 * These tests are intended to allow library developers to experience working with their own code in a practical setting,
 * getting first-hand impressions on how their API design feels to their target audience.
 */
@ExtendWith(SerenityJUnit5Extension.class)
class ValidationDogfoodTest implements WithAssertions {

    @Nested
    class ValidatorUsage {
        @Test
        void modularValidatorsMustCoverBasicUsage_givenSimpleDateBasedValidationLogic() {
            var toValidate = new DateBasedDummyObject(LocalDate.of(2023, MARCH, 9));
            assertThat(toValidate).isNotNull()
                    .extracting(DateBasedDummyObject::localDate)
                    .isNotNull();

            var notBeNull = new Constraint<DateBasedDummyObject>(Objects::nonNull, "not be null");
            var validator = aValid(DateBasedDummyObject.class)
                    .must(notBeNull)
                    .must(haveNonNullField(DateBasedDummyObject::localDate), "have a non-null local date")
                    .iHaveSpoken();

            assertThat(validator.constrain(toValidate)).is(CheckedTestUtils.valid());
        }

        private record DateBasedDummyObject(LocalDate localDate) {
        }
    }

    @Nested
    class ConstrainableUsages {

        @Test
        void checkedShouldAllowForFluentUsage_whenUsingItAsAGuard_givenInvalidObject() {
            var validator = aValid(DateBasedDummyObject.class)
                    .must(Objects::nonNull, "not be null")
                    .must(haveNonNullField(DateBasedDummyObject::localDate), "have a non-null local date")
                    .must(this::haveAName, "have a name")
                    .iHaveSpoken();
            var toValidate = new DateBasedDummyObject("", LocalDate.of(2023, MARCH, 9));
            assertThat(validator.constrain(toValidate)).matches(dateBasedDummyObjectConstrainable -> !dateBasedDummyObjectConstrainable.isValid());

            var result = validator.constrain(toValidate);

            assertThatExceptionOfType(InvalidObjectException.class)
                    .isThrownBy(() -> result.feedback("Object should be valid"))
                    .withMessage("Object should be valid")
                    .extracting(InvalidObjectException::errors).asList()
                    .contains("FAIL: [have a name]");
        }

        private boolean haveAName(DateBasedDummyObject toCheck) {
            return ofNullable(toCheck)
                    .map(DateBasedDummyObject::name)
                    .map(String::isBlank)
                    .map(empty -> !empty)
                    .orElse(false);
        }

        private record DateBasedDummyObject(String name, LocalDate localDate) {
        }
    }

}
