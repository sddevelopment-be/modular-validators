package be.sddevelopment.validation.core;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.annotation.processing.Generated;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Represents a single constraint / validation rule, which can be applied to an object of type T.
 *
 * <p><strong>Dynamic Descriptions (v1.2.0+):</strong> Constraint descriptions can now be computed
 * dynamically based on the validated object, enabling context-aware failure messages that include
 * actual field values.</p>
 *
 * <p><strong>Example with static description (backward-compatible):</strong></p>
 * <pre>{@code
 * new Constraint<>(person -> person.age() >= 18, "must be adult")
 * }</pre>
 *
 * <p><strong>Example with dynamic description (new in v1.2.0):</strong></p>
 * <pre>{@code
 * new Constraint<>(
 *     person -> person.age() >= 18,
 *     person -> person.age() >= 18
 *         ? "Is adult (age: " + person.age() + ")"
 *         : "Too young (age: " + person.age() + ", minimum: 18)"
 * )
 * }</pre>
 *
 * @param rule        a method that accepts an object of type T and returns a boolean that indicates if it matches the validation criterium.
 * @param descriptionBuilder function that generates a description based on the validated object (dynamic), or static string (backward-compatible)
 * @param <T>         type of the object onto which the rule can be applied
 * @author Stijn Dejongh
 * @version 1.2.0-SNAPSHOT
 */
public record Constraint<T>(Predicate<T> rule, Function<T, String> descriptionBuilder) {
    
    /**
     * Backward-compatible constructor for static descriptions.
     * 
     * <p>This constructor enables existing code using {@code String} descriptions
     * to continue working without modification.</p>
     *
     * @param rule the validation predicate
     * @param staticDescription the static description string
     */
    public Constraint(Predicate<T> rule, String staticDescription) {
        this(rule, new StaticDescriptionFunction<>(staticDescription));
    }
    
    /**
     * Gets the description string for display purposes.
     * 
     * <p>For static descriptions, returns the fixed string.
     * For dynamic descriptions, returns a generic placeholder.</p>
     * 
     * @return description string
     */
    public String descriptionString() {
        if (descriptionBuilder instanceof StaticDescriptionFunction<T> staticFunc) {
            return staticFunc.description();
        }
        return descriptionBuilder.toString();
    }

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

    /**
     * Applies this constraint to the given object and returns a detailed reason.
     * 
     * <p>The description is computed dynamically by evaluating the description builder
     * with the actual object being validated. This enables context-aware failure messages
     * that include actual field values.</p>
     *
     * @param toTest the object to validate
     * @return reason containing evaluation result and context-aware description
     */
    public Reason applyTo(T toTest) {
        var result = this.rule().test(toTest);
        String dynamicDescription = this.descriptionBuilder().apply(toTest);
        return new Reason(dynamicDescription, result ? Evaluation.PASS : Evaluation.FAIL);
    }

    /**
     * Note: Equality is based on description string (for static descriptions)
     * or description builder identity (for dynamic descriptions).
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Constraint<?> that = (Constraint<?>) o;

        // For static descriptions, compare by description string
        if (this.descriptionBuilder instanceof StaticDescriptionFunction<?> thisStatic
                && that.descriptionBuilder instanceof StaticDescriptionFunction<?> thatStatic) {
            return new EqualsBuilder()
                    .append(thisStatic.description(), thatStatic.description())
                    .isEquals();
        }

        // For dynamic descriptions, compare by instance identity
        return new EqualsBuilder()
                .append(descriptionBuilder, that.descriptionBuilder)
                .isEquals();
    }

    @Override
    public int hashCode() {
        // For static descriptions, hash by description string
        if (descriptionBuilder instanceof StaticDescriptionFunction<?> staticFunc) {
            return new HashCodeBuilder(17, 37)
                    .append(staticFunc.description())
                    .toHashCode();
        }
        
        // For dynamic descriptions, hash by instance identity
        return new HashCodeBuilder(17, 37)
                .append(descriptionBuilder)
                .toHashCode();
    }
    
    /**
     * Wrapper for static description strings that enables value-based equality.
     * 
     * @param <T> type parameter (unused, but required for Function signature)
     */
    private record StaticDescriptionFunction<T>(String description) implements Function<T, String> {
        @Override
        public String apply(T ignored) {
            return description;
        }
    }

    /**
     * Builder for the {@link Constraint} class, enabling a more readable code style.
     *
     * @param <T> type of the object onto which the rule can be applied
     */
    public static class ConstraintBuilder<T> {
        private Function<T, String> descriptionBuilder;
        private Predicate<T> toCheck;

        private ConstraintBuilder() {
        }

        public Constraint<T> done() {
            return new Constraint<>(toCheck, this.descriptionBuilder);
        }

        public ConstraintBuilder<T> requires(Predicate<T> predicate) {
            this.toCheck = predicate;
            return this;
        }

        /**
         * Sets a static description for this constraint.
         * 
         * @param description static description string
         * @return this builder
         */
        public ConstraintBuilder<T> describedAs(String description) {
            this.descriptionBuilder = new StaticDescriptionFunction<>(description);
            return this;
        }
        
        /**
         * Sets a dynamic description function for this constraint.
         * 
         * <p>The function will be evaluated with the actual object being validated,
         * enabling context-aware failure messages.</p>
         *
         * @param descriptionBuilder function that generates description based on validated object
         * @return this builder
         * @since 1.2.0
         */
        public ConstraintBuilder<T> describedBy(Function<T, String> descriptionBuilder) {
            this.descriptionBuilder = descriptionBuilder;
            return this;
        }
    }
}
