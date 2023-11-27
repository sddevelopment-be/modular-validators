package be.sddevelopment.validation;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import java.util.function.Predicate;

import static be.sddevelopment.validation.CheckedTestUtils.valid;
import static be.sddevelopment.validation.Reason.pass;

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
                .contains(pass("mustn't be blank"));
    }
}