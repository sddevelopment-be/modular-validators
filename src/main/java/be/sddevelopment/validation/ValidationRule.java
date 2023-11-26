package be.sddevelopment.validation;

import java.util.function.Function;
import java.util.function.Predicate;

public record ValidationRule<T>(Predicate<T> rule) {
}
