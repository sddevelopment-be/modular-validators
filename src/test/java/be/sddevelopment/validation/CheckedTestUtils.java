package be.sddevelopment.validation;

import be.sddevelopment.validation.core.Constrainable;
import be.sddevelopment.validation.core.Reason;
import org.assertj.core.api.Condition;

import java.util.function.Predicate;

public final class CheckedTestUtils {

    private CheckedTestUtils() throws IllegalAccessException {
        throw new IllegalAccessException("Utility classes (containing shared methods or constants) should not be instantiated.");
    }

    public static Condition<Constrainable<?>> valid() {
        return new Condition<>(Constrainable::isValid, "valid");
    }

    public static Condition<Constrainable<?>> invalid() {
        return new Condition<>(Predicate.not(Constrainable::isValid), "invalid");
    }

    public static Condition<Reason> passing() {
        return new Condition<>(Reason::isPassing, "is passing");
    }

    public static Condition<Reason> failing() {
        return new Condition<>(Predicate.not(Reason::isPassing), "is failing");
    }

}
