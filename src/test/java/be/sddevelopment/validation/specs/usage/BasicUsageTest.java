package be.sddevelopment.validation.specs.usage;

import be.sddevelopment.validation.core.Constraint;
import be.sddevelopment.validation.core.InvalidObjectException;
import be.sddevelopment.validation.core.ModularRuleset;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static be.sddevelopment.validation.CheckedTestUtils.invalid;
import static be.sddevelopment.validation.CheckedTestUtils.valid;
import static be.sddevelopment.validation.core.Checks.haveNonNullField;
import static be.sddevelopment.validation.core.ModularRuleset.aValid;
import static java.time.Month.MARCH;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static java.util.Optional.ofNullable;

@DisplayName("Basic Usage")
class BasicUsageTest implements WithAssertions {

    @Nested
    @DisplayName("Can be used as Validators")
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

            assertThat(validator.constrain(toValidate)).is(valid());
        }

        private record DateBasedDummyObject(LocalDate localDate) {
        }
    }

    @Nested
    @DisplayName("Constrained objects should allow for fluent usage")
    class ConstrainedUsage {
        private static final ModularRuleset<DateBasedDummyObject> DATE_REQUIREMENTS = aValid(ConstrainedUsage.DateBasedDummyObject.class)
                .must(Objects::nonNull, "not be null")
                .must(haveNonNullField(DateBasedDummyObject::localDate), "have a non-null local date")
                .must(ConstrainedUsage::haveAName, "have a name")
                .iHaveSpoken();

        private static ModularRuleset<DateBasedDummyObject> requirements() {
            return DATE_REQUIREMENTS;
        }

        @Test
        @DisplayName("when used as a guard clause")
        void checkedShouldAllowForFluentUsage_whenUsingItAsAGuard_givenInvalidObject() {
            var toValidate = new DateBasedDummyObject("", LocalDate.of(2023, MARCH, 9));
            assertThat(requirements().constrain(toValidate)).is(invalid());

            var result = requirements().constrain(toValidate);

            assertThatExceptionOfType(InvalidObjectException.class)
                    .isThrownBy(() -> result.feedback("Object should be valid"))
                    .withMessage("Object should be valid")
                    .extracting(InvalidObjectException::errors).asList()
                    .contains("FAIL: [have a name]");
        }

        @Test
        void allowsForFurtherProcessing() {
            var toBeUsed = new DateBasedDummyObject("I have a name", LocalDate.of(2023, MARCH, 9));
            var dateLoggingService = new DateLoggingService();
            assertThat(requirements().constrain(toBeUsed)).is(valid());
            assertThat(dateLoggingService.logLines()).isEmpty();

            requirements().constrain(toBeUsed)
                    .extract(DateBasedDummyObject::localDate)
                    .ifValid(dateLoggingService::log);

            assertThat(dateLoggingService.logLines()).containsExactly("2023-03-09");
        }

        private static boolean haveAName(DateBasedDummyObject toCheck) {
            return ofNullable(toCheck)
                    .map(DateBasedDummyObject::name)
                    .map(String::isBlank)
                    .map(empty -> !empty)
                    .orElse(false);
        }

        private record DateBasedDummyObject(String name, LocalDate localDate) {
        }

        private static class DateLoggingService {
            private final List<String> logLines = new ArrayList<>();

            public void log(LocalDate date) {
                this.logLines.add(ISO_LOCAL_DATE.format(date));
            }

            public List<String> logLines() {
                return new ArrayList<>(logLines);
            }
        }
    }

}
