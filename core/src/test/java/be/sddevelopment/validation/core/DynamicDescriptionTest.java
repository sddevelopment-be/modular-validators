package be.sddevelopment.validation.core;

import be.sddevelopment.commons.testing.naming.ReplaceUnderscoredCamelCasing;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;

import java.util.function.Function;
import java.util.function.Predicate;

import static be.sddevelopment.validation.core.Constraint.ruleFor;

/**
 * Demonstrates and tests the new dynamic description feature added in v1.2.0.
 * 
 * <p>Dynamic descriptions enable context-aware failure messages that include
 * actual field values from the validated object.</p>
 */
@DisplayName("Dynamic Descriptions (v1.2.0+)")
@DisplayNameGeneration(ReplaceUnderscoredCamelCasing.class)
class DynamicDescriptionTest implements WithAssertions {

    record Person(String name, int age) {}

    @Test
    void staticDescriptions_stillWork_backwardCompatibility() {
        var rule = new Constraint<Person>(
                person -> person.age() >= 18,
                "must be adult"
        );

        var minor = new Person("Alice", 16);
        var adult = new Person("Bob", 25);

        Reason failureReason = rule.applyTo(minor);
        Reason successReason = rule.applyTo(adult);

        assertThat(failureReason.rationale()).isEqualTo("must be adult");
        assertThat(failureReason.result()).isEqualTo(Evaluation.FAIL);

        assertThat(successReason.rationale()).isEqualTo("must be adult");
        assertThat(successReason.result()).isEqualTo(Evaluation.PASS);
    }

    @Test
    void dynamicDescriptions_includeActualValues_contextAwareFailureMessages() {
        var rule = new Constraint<Person>(
                person -> person.age() >= 18,
                person -> person.age() >= 18
                        ? "Is adult (age: " + person.age() + ")"
                        : "Too young (age: " + person.age() + ", minimum: 18)"
        );

        var minor = new Person("Alice", 16);
        var adult = new Person("Bob", 25);

        Reason failureReason = rule.applyTo(minor);
        Reason successReason = rule.applyTo(adult);

        assertThat(failureReason.rationale()).isEqualTo("Too young (age: 16, minimum: 18)");
        assertThat(failureReason.result()).isEqualTo(Evaluation.FAIL);

        assertThat(successReason.rationale()).isEqualTo("Is adult (age: 25)");
        assertThat(successReason.result()).isEqualTo(Evaluation.PASS);
    }

    @Test
    void builderPattern_supportsStaticDescriptions_backwardCompatible() {
        var rule = ruleFor(Person.class)
                .describedAs("must be adult")
                .requires(person -> person.age() >= 18)
                .done();

        var minor = new Person("Alice", 16);
        Reason reason = rule.applyTo(minor);

        assertThat(reason.rationale()).isEqualTo("must be adult");
        assertThat(reason.result()).isEqualTo(Evaluation.FAIL);
    }

    @Test
    void builderPattern_supportsDynamicDescriptions_newDescribedByMethod() {
        var rule = ruleFor(Person.class)
                .describedBy(person -> person.age() >= 18
                        ? "Is adult (age: " + person.age() + ")"
                        : "Too young (age: " + person.age() + ", minimum: 18)"
                )
                .requires(person -> person.age() >= 18)
                .done();

        var minor = new Person("Alice", 16);
        var adult = new Person("Bob", 25);

        assertThat(rule.applyTo(minor).rationale())
                .isEqualTo("Too young (age: 16, minimum: 18)");
        assertThat(rule.applyTo(adult).rationale())
                .isEqualTo("Is adult (age: 25)");
    }

    @Test
    void dynamicDescriptions_canAccessMultipleFields() {
        var rule = new Constraint<Person>(
                person -> person.age() >= 18 && person.name() != null,
                person -> {
                    if (person.name() == null) {
                        return "Name is missing";
                    }
                    if (person.age() < 18) {
                        return person.name() + " is too young (age: " + person.age() + ", minimum: 18)";
                    }
                    return person.name() + " is valid (age: " + person.age() + ")";
                }
        );

        var minor = new Person("Alice", 16);
        var adult = new Person("Bob", 25);
        var unnamed = new Person(null, 30);

        assertThat(rule.applyTo(minor).rationale())
                .isEqualTo("Alice is too young (age: 16, minimum: 18)");
        assertThat(rule.applyTo(adult).rationale())
                .isEqualTo("Bob is valid (age: 25)");
        assertThat(rule.applyTo(unnamed).rationale())
                .isEqualTo("Name is missing");
    }

    @Test
    void dynamicDescriptions_canIncludeComputedValues() {
        var rule = new Constraint<Person>(
                person -> person.age() >= 18,
                person -> {
                    int yearsUntilAdult = 18 - person.age();
                    return person.age() >= 18
                            ? "Adult for " + (person.age() - 18) + " years"
                            : "Needs to wait " + yearsUntilAdult + " more year" + (yearsUntilAdult == 1 ? "" : "s");
                }
        );

        assertThat(rule.applyTo(new Person("Child", 10)).rationale())
                .isEqualTo("Needs to wait 8 more years");
        assertThat(rule.applyTo(new Person("Teen", 17)).rationale())
                .isEqualTo("Needs to wait 1 more year");
        assertThat(rule.applyTo(new Person("Adult", 30)).rationale())
                .isEqualTo("Adult for 12 years");
    }

    @Test
    void modularRuleset_supportsDynamicDescriptions() {
        ModularRuleset<Person> ruleset = ModularRuleset.aValid(Person.class)
                .must(
                        person -> person.name() != null && !person.name().isBlank(),
                        person -> person.name() == null || person.name().isBlank()
                                ? "Name is required (got: '" + person.name() + "')"
                                : "Name is valid ('" + person.name() + "')"
                )
                .must(
                        person -> person.age() >= 18,
                        person -> person.age() >= 18
                                ? "Age is valid (" + person.age() + ")"
                                : "Too young (age: " + person.age() + ", minimum: 18)"
                )
                .done();

        var invalidPerson = new Person("", 16);
        Rationale rationale = ruleset.check(invalidPerson);

        assertThat(rationale.details())
                .extracting(Reason::rationale)
                .containsExactly(
                        "Name is required (got: '')",
                        "Too young (age: 16, minimum: 18)"
                );
    }
}
