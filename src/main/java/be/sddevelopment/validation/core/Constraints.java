package be.sddevelopment.validation.core;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.function.Predicate.not;

/**
 * <p>Utility class, used allow for a more straight-forward configuration of {@link ModularRuleset} constructs</p>
 */
public final class Constraints {
    private Constraints() {
        throw new UnsupportedOperationException("Utility classes (containing shared methods or constants) should not be instantiated.");
    }
    public static final Constraint<String> IS_NOT_EMPTY = new Constraint<>(StringUtils::isNotBlank, "mustn't be blank");
    public static Constraint<String> isNotEmpty() {
        return IS_NOT_EMPTY;
    }

}
