package be.sddevelopment.validation.specs.usage;

import static be.sddevelopment.validation.CheckedTestUtils.hasEvidenceWithValue;
import static be.sddevelopment.validation.CheckedTestUtils.invalid;
import static be.sddevelopment.validation.CheckedTestUtils.valid;
import static be.sddevelopment.validation.core.Checks.haveNonNullField;
import static be.sddevelopment.validation.core.ModularRuleset.aValid;
import static java.time.Month.MARCH;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static java.util.Optional.ofNullable;
import static java.util.UUID.randomUUID;
import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import be.sddevelopment.commons.testing.naming.ReplaceUnderscoredCamelCasing;
import be.sddevelopment.validation.core.Constrained;
import be.sddevelopment.validation.core.Constraint;
import be.sddevelopment.validation.core.Evidence;
import be.sddevelopment.validation.core.InvalidObjectException;
import be.sddevelopment.validation.core.ModularRuleset;
import be.sddevelopment.validation.core.Rationale;
import be.sddevelopment.validation.core.Reason;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Basic Usage")
@DisplayNameGeneration(ReplaceUnderscoredCamelCasing.class)
class BasicUsageTest implements WithAssertions {

	@Nested
	@DisplayName("Can be used as Validators")
	class ValidatorUsage {
		private static final List<String> PROHIBITED_ATOMS = List.of("invalid", "forbidden", "banned");

		@Test
		void canHandleBasicDateLogic() {
			var toValidate = new DateBasedDummyObject(LocalDate.of(2023, MARCH, 9));
			assertThat(toValidate).isNotNull().extracting(DateBasedDummyObject::localDate).isNotNull();

			var notBeNull = new Constraint<DateBasedDummyObject>(Objects::nonNull, "not be null");
			var validator = aValid(DateBasedDummyObject.class).must(notBeNull)
					.must(haveNonNullField(DateBasedDummyObject::localDate), "have a non-null local date")
					.iHaveSpoken();
			Constrained<DateBasedDummyObject> constrained = validator.constrain(toValidate);

			assertThat(constrained).is(valid());
			assertThat(constrained).extracting(Constrained::rationale).matches(Rationale::isPassing);
		}

		private record DateBasedDummyObject(LocalDate localDate) {
		}

		@Test
		void canHandleEmailValidation() {
			var invalidEmail = new EmailContact(randomUUID(), "invalid email", "Bob", "The Builder");
			assertThat(invalidEmail).matches(ValidatorUsage::containsProhibitedAtoms, "contains prohibited atoms");

			var toCheck = aValid(EmailContact.class).must(Objects::nonNull, "not be null")
					.must(haveNonNullField(EmailContact::email), "have a non-null email")
					.mayNot(ValidatorUsage::containsProhibitedAtoms, "not contain prohibited atoms")
					.iHaveSpoken()
					.constrain(invalidEmail);

			assertThat(toCheck).is(invalid());
			assertThatThrownBy(() -> toCheck.feedback("Email should be valid")).isInstanceOf(InvalidObjectException.class)
					.hasMessageContaining("Email should be valid");
		}

		private static boolean containsProhibitedAtoms(EmailContact emailContact) {
			return PROHIBITED_ATOMS.parallelStream().anyMatch(atom -> containsIgnoreCase(emailContact.email(), atom));
		}

		private record EmailContact(UUID userIdentifier, String email, String name, String lastName) {
		}

	}

	@Nested
	@DisplayName("Constrained objects should allow for fluent usage")
	class ConstrainedUsage {
		private static final ModularRuleset<DateBasedDummyObject> DATE_REQUIREMENTS =
				aValid(ConstrainedUsage.DateBasedDummyObject.class).must(Objects::nonNull, "not be null")
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

			assertThatExceptionOfType(InvalidObjectException.class).isThrownBy(() -> result.feedback("Object should be valid"))
					.withMessage("Object should be valid")
					.extracting(InvalidObjectException::errors)
					.asInstanceOf(LIST)
					.contains("FAIL: [have a name]");
		}

		@Test
		void allowsForFurtherProcessing() {
			var dateLoggingService = new DateLoggingService();
			assertThat(dateLoggingService.logLines()).isEmpty();
			var toBeUsed = new DateBasedDummyObject("I have a name", LocalDate.of(2023, MARCH, 9));
			assertThat(requirements().constrain(toBeUsed)).is(valid());

			requirements().constrain(toBeUsed).extract(DateBasedDummyObject::localDate).ifValid(dateLoggingService::log);

			assertThat(dateLoggingService.logLines()).containsExactly("2023-03-09");
		}

		private static boolean haveAName(DateBasedDummyObject toCheck) {
			return ofNullable(toCheck).map(DateBasedDummyObject::name).map(String::isBlank).map(empty -> !empty).orElse(false);
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

	private record DummyObject(String name, LocalDate localDate) {
	}

	void dynamicDescriptionsCanBeCreated() {
		var toBeChecked = new DummyObject("I have a name", LocalDate.of(2023, MARCH, 9));

		var ruleSet = aValid(DummyObject.class).must(Objects::nonNull, "not be null")
				.must(haveNonNullField(DummyObject::localDate), "have a non-null local date")
				.must(o -> o.localDate().isBefore(LocalDate.of(2025, 12, 31)), "have a local date before the end of 2025",
						o -> "Local date %s is not before end of 2025 ".formatted(o.localDate()),
						o -> {return Evidence.empty().put("objectId", o.name()).put("date", o.localDate().toString());})
				.iHaveSpoken();

		var result = ruleSet.constrain(toBeChecked);

		assertThat(result).is(invalid());
		assertThat(result.rationale().details()).hasSize(1);
		assertThat(result.rationale().details()).extracting(Reason::rationale).contains("Local date 2023-03-09 is not before end of 2025 ");

		assertThat(result.rationale().evidence()).satisfies(hasEvidenceWithValue("objectId", "I have a name"))
				.satisfies(hasEvidenceWithValue("date", "2023-03-09"));
	}

}
