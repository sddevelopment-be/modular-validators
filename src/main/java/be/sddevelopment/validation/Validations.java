package be.sddevelopment.validation;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.function.Predicate.not;

public final class Validations {

    private Validations() throws IllegalAccessException {
        throw new IllegalAccessException("Utility classes (containing shared methods or constants) should not be instantiated.");
    }

    public static <S> Predicate<S> haveNonNullField(Function<S, ?> extractor) {
        return not(haveNullField(extractor));
    }

    public static <S> Predicate<S> haveNullField(Function<S, ?> extractor) {
        return extractor.andThen(Objects::isNull)::apply;
    }
}
