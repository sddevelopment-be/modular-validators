package be.sddevelopment.validation;

import java.util.Optional;

import static java.util.Optional.ofNullable;

public final class Checked<T> {

    private final T data;
    private EvaluationRationale rationale;

    private Checked(T toValidate) {
        this.data = toValidate;
    }

    public boolean isValid() {
        return false;
    }

    public Checked<T> applyRule(ValidationRule<T> tValidationRule) {
        return this;
    }

    public static <T> Checked<T> of(T toValidate) {
        return new Checked<>(toValidate);
    }

    public Optional<EvaluationRationale> rationale() {
        return ofNullable(this.rationale);
    }
}
