package be.sddevelopment.validation.core;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.annotation.processing.Generated;
import java.util.function.Predicate;

/**
 * Represents a single constraint / validation rule, which can be applied to an object of type T.
 *
 * @param rule        a method that accepts an object of type T and returns a boolean that indicates if it matches the validation criterium.
 * @param description textual description of the validation rule
 * @param <T>         type of the object onto which the rule can be applied
 * @author Stijn Dejongh
 * @version 1.0.0-SNAPSHOT
 */
public record Constraint<T>(Predicate<T> rule, String description) {

    /**
     * Factory method to create a new {@link ConstraintBuilder} instance.
     *
     * @param <S>          type of the object onto which the rule can be applied
     * @param ignoredClass class to be ignored, as this method is only used as a type parameter
     *                     to create a new instance of the builder
     * @return a new instance of the {@link ConstraintBuilder} class, able to evaluate a rule on an object of type S
     */
    public static <S> ConstraintBuilder<S> ruleFor(Class<S> ignoredClass) {
        return new ConstraintBuilder<>();
    }

    public Reason applyTo(T toTest) {
        var result = this.rule().test(toTest);
        return new Reason(this.description(), result ? Evaluation.PASS : Evaluation.FAIL);
    }

    @Override
    @Generated("generated by IDE, using Apache Commons Lang3")
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Constraint<?> that = (Constraint<?>) o;

        return new EqualsBuilder()
                .append(description, that.description)
                .isEquals();
    }

    @Override
    @Generated("generated by IDE, using Apache Commons Lang3")
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(description)
                .toHashCode();
    }

    /**
     * Builder for the {@link Constraint} class, enabling a more readable code style.
     *
     * @param <T> type of the object onto which the rule can be applied
     */
    public static class ConstraintBuilder<T> {
        private String description;
        private Predicate<T> toCheck;

        private ConstraintBuilder() {
        }

        public Constraint<T> done() {
            return new Constraint<>(toCheck, this.description);
        }

        public ConstraintBuilder<T> requires(Predicate<T> predicate) {
            this.toCheck = predicate;
            return this;
        }

        public ConstraintBuilder<T> describedAs(String description) {
            this.description = description;
            return this;
        }
    }
}
