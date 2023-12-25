package be.sddevelopment.validation;

import net.serenitybdd.junit5.SerenityJUnit5Extension;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDate;
import java.util.Objects;

import static be.sddevelopment.validation.ModularValidator.aValid;
import static be.sddevelopment.validation.Validations.haveNonNullField;
import static java.time.Month.MARCH;
import static java.util.Optional.ofNullable;

/**
 * Tests illustrating using the validators through close-to-real examples.
 * These tests are intended to allow library developers to experience working with their own code in a practical setting,
 * getting first-hand impressions on how their API design feels to their target audience.
 */
@ExtendWith(SerenityJUnit5Extension.class)
class ValidatorDogfoodTest implements WithAssertions {

    @Nested
    class ValidatorUsage {
        @Test
        void modularValidatorsMustCoverBasicUsage_givenSimpleDateBasedValidationLogic() {
            var toValidate = new DateBasedDummyObject(LocalDate.of(2023, MARCH, 9));
            assertThat(toValidate).isNotNull()
                    .extracting(DateBasedDummyObject::localDate)
                    .isNotNull();

            var notBeNull = new ValidationRule<DateBasedDummyObject>(Objects::nonNull, "not be null");
            var validator = aValid(DateBasedDummyObject.class)
                    .must(notBeNull)
                    .must(haveNonNullField(DateBasedDummyObject::localDate), "have a non-null local date")
                    .iHaveSpoken();

            assertThat(validator.evaluate(toValidate)).is(CheckedTestUtils.valid());
        }

        private record DateBasedDummyObject(LocalDate localDate) {
        }
    }

    @Nested
    class CheckedUsages {

        @Test
        @Disabled("Acceptance test for Checked Usage")
        void checkedShouldAllowForFluentUsage_whenUsingItAsAGuard() {
            var validator = aValid(DateBasedDummyObject.class)
                    .must(Objects::nonNull, "not be null")
                    .must(haveNonNullField(DateBasedDummyObject::localDate), "have a non-null local date")
                    .must(this::haveAName, "have a name")
                    .iHaveSpoken();
            var toValidate = new DateBasedDummyObject("", LocalDate.of(2023, MARCH, 9));
            assertThat(validator.evaluate(toValidate)).matches(Checked::isInvalid);

            var result = validator.evaluate(toValidate);

            assertThatExceptionOfType(InvalidObjectException.class)
                    .isThrownBy(() -> result.guard("Object should be valid"))
                    .withMessage("Object should be valid")
                    .extracting(InvalidObjectException::errors).asList()
                    .containsExactly("rule [have a name] FAILED");
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
