package be.sddevelopment.validation;

import net.serenitybdd.junit5.SerenityJUnit5Extension;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.util.StringUtils;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.function.Predicate;

import static be.sddevelopment.validation.CheckedTestUtils.invalid;
import static be.sddevelopment.validation.CheckedTestUtils.valid;
import static be.sddevelopment.validation.Reason.failed;
import static be.sddevelopment.validation.Reason.passed;
import static be.sddevelopment.validation.Validations.haveNonEmpty;

@ExtendWith({SerenityJUnit5Extension.class, MockitoExtension.class})
class ConstrainableTest implements WithAssertions {

    @Test
    void givenCheckedWithData_whenApplyingRule_itsEvaluationIsRemembered() {
        var contentToCheck = "This is a non empty String";
        var rule = new Constraint<>(Predicate.not(String::isBlank), "mustn't be blank");
        assertThat(rule.rule().test(contentToCheck)).isTrue();

        var result = Constrainable.constrain(contentToCheck).adheresTo(rule);

        assertThat(result).is(valid());
        assertThat(result.rationale())
                .extracting(Rationale::details).asList()
                .contains(passed("mustn't be blank"));
    }

    @Test
    void givenCheckedWithData_andInvalidData_whenApplyingRule_itIsEvaluatedAsInvalid() {
        var empty = "";
        var rule = new Constraint<>(Predicate.not(String::isBlank), "mustn't be blank");
        assertThat(rule.rule().test(empty)).isFalse();

        var result = Constrainable.constrain(empty).adheresTo(rule);

        assertThat(result).is(invalid());
        assertThat(result.rationale())
                .extracting(Rationale::details).asList()
                .contains(failed("mustn't be blank"));
    }

    @Test
    void feedback_throwsAValidationException_givenInvalidSourceData() {
        var checked = Constrainable.constrain("")
                .adheresTo(new Constraint<>(StringUtils::isNotBlank, "mustn't be blank"));
        assertThat(checked).is(invalid());

        assertThatExceptionOfType(InvalidObjectException.class)
                .isThrownBy(() -> checked.feedback("This is an invalid object"))
                .withMessage("This is an invalid object");
    }

    @Test
    void feedback_hasNoEffect_givenValidSourceData() {
        var checked = Constrainable.constrain("I am a real String")
                .adheresTo(new Constraint<>(StringUtils::isNotBlank, "mustn't be blank"));
        assertThat(checked).is(valid());

        assertThatCode(() -> checked.feedback("This is an invalid object"))
                .doesNotThrowAnyException();
    }

    @Test
    void guard_failsFastWhenInvalid() {

        var rules = ModularRuleset.aValid(ToBeConstained.class)
                .must(haveNonEmpty(ToBeConstained::firstName), "have a first name")
                .must(haveNonEmpty(ToBeConstained::lastName), "have a last name")
                .must(haveNonEmpty(ToBeConstained::dateOfBirth), "have a year of birth")
                .done();

        var constrainedRecord = rules.constrain(new ToBeConstained("", "", ""));

        assertThatExceptionOfType(InvalidObjectException.class)
                .isThrownBy(constrainedRecord::guard)
                .extracting(InvalidObjectException::errors).asList()
                .containsExactly("FAIL: [have a first name]");
    }

    private record ToBeConstained(String firstName, String lastName, String dateOfBirth) {
    }

}