package be.sddevelopment.validation;

import java.util.function.Predicate;

/**
 * <p>ValidationRule class.</p>
 * Represents a single validation rule, which can be applied to an object of any type.
 *
 * @param rule        a method that accepts an object of type T and returns a boolean that indicates if it matches the validation criterium.
 * @param description textual description of the validation rule
 * @param <T>         type of the object onto which the rule can be applied
 */
public record ValidationRule<T>(Predicate<T> rule, String description) {
}
