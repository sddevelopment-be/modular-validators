package be.sddevelopment.validation;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;

import java.util.List;
import java.util.function.Predicate;

import static be.sddevelopment.validation.CheckedTestUtils.failing;
import static be.sddevelopment.validation.CheckedTestUtils.invalid;
import static be.sddevelopment.validation.Constraint.ruleFor;
import static be.sddevelopment.validation.Reason.failed;
import static be.sddevelopment.validation.Reason.passed;

class ConstraintTest implements WithAssertions {

    @Test
    void aValidationRule_canBeBuildAndUsed_inAFluentWay() {
        var rule = ruleFor(String.class)
                .describedAs("We rule")
                .requires(StringUtils::isNotBlank)
                .done();

        assertThat(rule.applyTo("")).is(failing());
    }

    @Test
    void rules_areChainable() {
        var rule = ruleFor(String.class)
                .describedAs("Must be filled in")
                .requires(StringUtils::isNotBlank)
                .done();
        var otherRule = ruleFor(String.class)
                .describedAs("Be polite")
                .requires(Predicate.not(List.of("Idiot", "Fracking", "Noob")::contains))
                .done();

        var result = Constrainable.constrain("Idiot")
                .adheresTo(rule)
                .adheresTo(otherRule);

        assertThat(result).is(invalid())
                .extracting(Constrainable::rationale)
                .extracting(Rationale::details).asList()
                .contains(passed("Must be filled in"), failed("Be polite"));
    }

    @Test
    void givenTwoIdenticalRules_theyAreConsideredEqual() {
        var rule = ruleFor(String.class)
                .describedAs("We rule")
                .requires(StringUtils::isNotBlank)
                .done();
        var other = ruleFor(String.class)
                .describedAs("We rule")
                .requires(StringUtils::isNotBlank)
                .done();

        assertThat(rule).isEqualTo(other);
        assertThat(other).isEqualTo(rule);
    }

    @Test
    void givenTwoIdenticalRules_theirHashesAreIdentical() {
        var rule = ruleFor(String.class)
                .describedAs("We rule")
                .requires(StringUtils::isNotBlank)
                .done();
        var other = ruleFor(String.class)
                .describedAs("We rule")
                .requires(StringUtils::isNotBlank)
                .done();

        assertThat(rule.hashCode()).isEqualTo(other.hashCode());
    }
}