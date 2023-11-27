package be.sddevelopment.validation;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Objects;

import static be.sddevelopment.validation.ModularValidator.aValid;
import static be.sddevelopment.validation.Validations.haveNonNullField;
import static java.time.Month.MARCH;

/**
 * Tests illustrating using the validators through close-to-real examples.
 * These tests are intended to allow library developers to experience working with their own code in a practical setting,
 * getting first-hand impressions on how their API design feels to their target audience.
 */
class ValidatorDogfoodTest implements WithAssertions {

    @Test
    @Disabled("Work in progress: top-level acceptance test")
    void modularValidatorsMustCoverBasicUsage_givenSimpleDateBasedValidationLogic() {
        var toValidate = new DateBasedDummyObject(LocalDate.of(2023, MARCH, 9));
        assertThat(toValidate).isNotNull()
                .extracting(DateBasedDummyObject::localDate)
                .isNotNull();

        var validator = aValid(DateBasedDummyObject.class)
                .must(Objects::nonNull)
                .must(haveNonNullField(DateBasedDummyObject::localDate));

        assertThat(validator.evaluate(toValidate)).is(CheckedTestUtils.valid());
    }

    private record DateBasedDummyObject(LocalDate localDate) {
    }
}
