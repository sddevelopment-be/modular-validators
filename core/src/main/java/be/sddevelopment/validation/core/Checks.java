package be.sddevelopment.validation.core;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.function.Predicate.not;

public final class Checks {

    private Checks() {
        throw new UnsupportedOperationException("Utility classes (containing shared methods or constants) should not be instantiated.");
    }

    public static <S> Predicate<S> haveNonNullField(Function<S, ?> extractor) {
        return not(haveNullField(extractor));
    }

    public static <S> Predicate<S> haveNonEmpty(Function<S, String> extractor) {
        return extractor.andThen(StringUtils::isNotBlank)::apply;
    }

    public static <S> Predicate<S> haveNullField(Function<S, ?> extractor) {
        return extractor.andThen(Objects::isNull)::apply;
    }
}
