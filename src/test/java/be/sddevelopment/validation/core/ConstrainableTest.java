package be.sddevelopment.validation.core;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.util.StringUtils;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static be.sddevelopment.validation.CheckedTestUtils.invalid;
import static be.sddevelopment.validation.CheckedTestUtils.valid;
import static be.sddevelopment.validation.core.Constrainable.constrain;
import static be.sddevelopment.validation.core.ModularRuleset.aValid;
import static be.sddevelopment.validation.core.Reason.failed;
import static be.sddevelopment.validation.core.Reason.passed;
import static be.sddevelopment.validation.core.Checks.haveNonEmpty;

@DisplayName("Constrainables")
@ExtendWith({MockitoExtension.class})
class ConstrainableTest implements WithAssertions {

    @Test
    void givenValidData_whenApplyingRule_itsEvaluationIsRemembered() {
        var contentToCheck = "This is a non empty String";
        var rule = new Constraint<>(Predicate.not(String::isBlank), "mustn't be blank");
        assertThat(rule.rule().test(contentToCheck)).isTrue();

        var result = constrain(contentToCheck).adheresTo(rule);

        assertThat(result).is(valid());
        assertThat(result.rationale())
                .extracting(Rationale::details).asList()
                .contains(passed("mustn't be blank"));
    }

    @Test
    void givenInvalidData_whenApplyingRule_itIsEvaluatedAsInvalid() {
        var empty = "";
        var rule = new Constraint<>(Predicate.not(String::isBlank), "mustn't be blank");
        assertThat(rule.rule().test(empty)).isFalse();

        var result = constrain(empty).adheresTo(rule);

        assertThat(result).is(invalid());
        assertThat(result.rationale())
                .extracting(Rationale::details).asList()
                .contains(failed("mustn't be blank"));
    }

    @Test
    void feedback_throwsAValidationException_givenInvalidSourceData() {
        var checked = constrain("")
                .adheresTo(new Constraint<>(StringUtils::isNotBlank, "mustn't be blank"));
        assertThat(checked).is(invalid());

        assertThatExceptionOfType(InvalidObjectException.class)
                .isThrownBy(() -> checked.feedback("This is an invalid object"))
                .withMessage("This is an invalid object");
    }

    @Test
    void feedback_hasNoEffect_givenValidSourceData() {
        var checked = constrain("I am a real String")
                .adheresTo(new Constraint<>(StringUtils::isNotBlank, "mustn't be blank"));
        assertThat(checked).is(valid());

        assertThatCode(() -> checked.feedback("This is an invalid object"))
                .doesNotThrowAnyException();
    }

    @Test
    void ifValid_givenTheDateIsValid_CallsTheProvidedConsumer() {
        var checked = constrain("I am a real String").adheresTo(Constraints.isNotEmpty());
        var consumer = new MessageStore();
        assertThat(checked).is(valid());
        assertThat(consumer.messages()).isEmpty();

        checked.ifValid(consumer::store);


    }

    @Test
    void guard_failsFastWhenInvalid() {
        var rules = aValid(ToBeConstrained.class)
                .must(haveNonEmpty(ToBeConstrained::firstName), "have a first name")
                .must(haveNonEmpty(ToBeConstrained::lastName), "have a last name")
                .must(haveNonEmpty(ToBeConstrained::dateOfBirth), "have a year of birth")
                .done();

        var constrainedRecord = rules.constrain(new ToBeConstrained("", "", ""));

        assertThatExceptionOfType(InvalidObjectException.class)
                .isThrownBy(constrainedRecord::guard)
                .extracting(InvalidObjectException::errors).asList()
                .containsExactly("FAIL: [have a first name]");
    }

    private record ToBeConstrained(String firstName, String lastName, String dateOfBirth) {
    }

    private static class MessageStore {
        private final List<String> messages = new ArrayList<>();

        void store(String message) {
            messages.add(message);
        }
         List<String> messages() {
             return messages;
         }
    }
}