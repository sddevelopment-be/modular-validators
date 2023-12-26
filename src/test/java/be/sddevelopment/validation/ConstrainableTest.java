package be.sddevelopment.validation;

import net.serenitybdd.junit5.SerenityJUnit5Extension;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.util.StringUtils;

import java.util.function.Predicate;

import static be.sddevelopment.validation.CheckedTestUtils.invalid;
import static be.sddevelopment.validation.CheckedTestUtils.valid;
import static be.sddevelopment.validation.Constrainable.constrain;
import static be.sddevelopment.validation.Reason.failed;
import static be.sddevelopment.validation.Reason.passed;

@ExtendWith(SerenityJUnit5Extension.class)
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
    void guard_throwsAValidationException_givenInvalidSourceData() {
        var checked = Constrainable.constrain("")
                .adheresTo(new Constraint<>(StringUtils::isNotBlank, "mustn't be blank"));
        assertThat(checked).is(invalid());

        assertThatExceptionOfType(InvalidObjectException.class)
                .isThrownBy(() -> checked.feedback("This is an invalid object"))
                .withMessage("This is an invalid object");
    }

    @Test
    void guard_hasNoEffect_givenValidSourceData() {
        var checked = Constrainable.constrain("I am a real String")
                .adheresTo(new Constraint<>(StringUtils::isNotBlank, "mustn't be blank"));
        assertThat(checked).is(valid());

        assertThatCode(() -> checked.feedback("This is an invalid object"))
                .doesNotThrowAnyException();
    }
}