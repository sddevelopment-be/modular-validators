package be.sddevelopment.validation;

import org.assertj.core.api.Condition;

import java.util.function.Predicate;

public final class CheckedTestUtils {

    private CheckedTestUtils() throws IllegalAccessException {
        throw new IllegalAccessException("Utility classes (containing shared methods or constants) should not be instantiated.");
    }

    public static Condition<Checked<?>> valid() {
        return new Condition<>(Checked::isValid, "valid");
    }

    public static Condition<Checked<?>> invalid() {
        return new Condition<>(Predicate.not(Checked::isValid), "invalid");
    }
}
