package be.sddevelopment.validation;

import net.serenitybdd.junit5.SerenityJUnit5Extension;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.function.Predicate;

import static be.sddevelopment.validation.CheckedTestUtils.invalid;
import static be.sddevelopment.validation.CheckedTestUtils.valid;
import static be.sddevelopment.validation.Reason.failed;
import static be.sddevelopment.validation.Reason.passed;

@ExtendWith(SerenityJUnit5Extension.class)
class CheckedTest implements WithAssertions {

    @Test
    void givenCheckedWithData_whenApplyingRule_itsEvaluationIsRemembered() {
        var contentToCheck = "This is a non empty String";
        var rule = new ValidationRule<>(Predicate.not(String::isBlank), "mustn't be blank");
        assertThat(rule.rule().test(contentToCheck)).isTrue();

        var result = Checked.of(contentToCheck).applyRule(rule);

        assertThat(result).is(valid());
        assertThat(result.rationale())
                .isPresent().get()
                .extracting(EvaluationRationale::details).asList()
                .contains(passed("mustn't be blank"));
    }

    @Test
    void givenCheckedWithData_andInvalidData_whenApplyingRule_itIsEvaluatedAsInvalid() {
        var empty = "";
        var rule = new ValidationRule<>(Predicate.not(String::isBlank), "mustn't be blank");
        assertThat(rule.rule().test(empty)).isFalse();

        var result = Checked.of(empty).applyRule(rule);

        assertThat(result).is(invalid());
        assertThat(result.rationale())
                .isPresent().get()
                .extracting(EvaluationRationale::details).asList()
                .contains(failed("mustn't be blank"));
    }
}