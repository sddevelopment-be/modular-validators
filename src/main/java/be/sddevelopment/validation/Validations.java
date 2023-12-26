package be.sddevelopment.validation;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.function.Predicate.not;

/**
 * <p>Validations class.</p>
 *
 * @author stijnd
 * @version 1.0.0-SNAPSHOT
 */
public final class Validations {

    private Validations() {
        throw new UnsupportedOperationException("Utility classes (containing shared methods or constants) should not be instantiated.");
    }

    /**
     * <p>haveNonNullField.</p>
     *
     * @param extractor a {@link java.util.function.Function} object
     * @param <S>       a S class
     * @return a {@link java.util.function.Predicate} object
     */
    public static <S> Predicate<S> haveNonNullField(Function<S, ?> extractor) {
        return not(haveNullField(extractor));
    }

    /**
     * <p>haveNullField.</p>
     *
     * @param extractor a {@link java.util.function.Function} object
     * @param <S>       a S class
     * @return a {@link java.util.function.Predicate} object
     */
    public static <S> Predicate<S> haveNullField(Function<S, ?> extractor) {
        return extractor.andThen(Objects::isNull)::apply;
    }
}
