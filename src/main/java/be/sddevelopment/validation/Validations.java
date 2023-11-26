package be.sddevelopment.validation;

import java.util.Objects;
import java.util.function.Function;

public final class Validations {

    private Validations() throws IllegalAccessException {
        throw new IllegalAccessException("Utility methods (containing shared methods or constants) should not be instantiated.");
    }


    public static <S> Function<S, Boolean> haveNonEmptyField(Function<S, ?> extractor) {
        return extractor.andThen(Objects::isNull).andThen(result -> !result);
    }
}
