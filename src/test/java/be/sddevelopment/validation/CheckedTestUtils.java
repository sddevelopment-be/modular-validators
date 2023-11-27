package be.sddevelopment.validation;

import org.assertj.core.api.Condition;

public final class CheckedTestUtils {

    private CheckedTestUtils() throws IllegalAccessException {
        throw new IllegalAccessException("Utility classes (containing shared methods or constants) should not be instantiated.");
    }

    public static Condition<Checked<?>> valid() {
        return new Condition<>(Checked::isValid, "valid");
    }
}
