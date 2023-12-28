package be.sddevelopment.validation.core;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.function.Predicate;

/**
 * <p>ValidationRule class.</p>
 * Represents a single validation rule, which can be applied to an object of any type.
 *
 * @param rule        a method that accepts an object of type T and returns a boolean that indicates if it matches the validation criterium.
 * @param description textual description of the validation rule
 * @param <T>         type of the object onto which the rule can be applied
 */
public record Constraint<T>(Predicate<T> rule, String description) {

    public static <S> ValidationRuleBuilder<S> ruleFor(Class<S> ignoredClass) {
        return new ValidationRuleBuilder<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Constraint<?> that = (Constraint<?>) o;

        return new EqualsBuilder()
                .append(description, that.description)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(description)
                .toHashCode();
    }

    public Reason applyTo(T toTest) {
        var result = this.rule().test(toTest);
        return new Reason(this.description(), result ? Evaluation.PASS : Evaluation.FAIL);
    }

    public static class ValidationRuleBuilder<T> {
        private String description;
        private Predicate<T> toCheck;

        private ValidationRuleBuilder() {
        }

        public Constraint<T> done() {
            return new Constraint<>(toCheck, this.description);
        }

        public ValidationRuleBuilder<T> requires(Predicate<T> predicate) {
            this.toCheck = predicate;
            return this;
        }

        public ValidationRuleBuilder<T> describedAs(String description) {
            this.description = description;
            return this;
        }
    }
}
