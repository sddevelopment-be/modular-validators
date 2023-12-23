package be.sddevelopment.validation;

import java.util.Optional;

import static be.sddevelopment.validation.Evaluation.FAIL;
import static be.sddevelopment.validation.Evaluation.PASS;
import static java.util.Optional.ofNullable;

public final class Checked<T> {

    private final T data;
    private final EvaluationRationale rationale = new EvaluationRationale();

    private Checked(T toValidate) {
        this.data = toValidate;
    }

    public boolean isValid() {
        return this.rationale.details()
                .stream()
                .map(Reason::result)
                .noneMatch(FAIL::equals);
    }

    public Checked<T> applyRule(ValidationRule<T> tValidationRule) {
        var result = tValidationRule.rule().test(this.data);
        this.rationale.add(new Reason(tValidationRule.description(), result ? PASS : FAIL));
        return this;
    }

    public static <T> Checked<T> of(T toValidate) {
        return new Checked<>(toValidate);
    }

    public Optional<EvaluationRationale> rationale() {
        return ofNullable(this.rationale);
    }
}
