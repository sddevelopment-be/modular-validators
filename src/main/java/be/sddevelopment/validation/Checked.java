package be.sddevelopment.validation;

import java.util.Optional;

import static be.sddevelopment.validation.Evaluation.FAIL;
import static be.sddevelopment.validation.Evaluation.PASS;
import static java.util.Optional.ofNullable;

/**
 * <p>Checked class.</p>
 *
 * @author stijnd
 * @version 1.0.0-SNAPSHOT
 */
public final class Checked<T> {

    private final T data;
    private final EvaluationRationale rationale = new EvaluationRationale();

    private Checked(T toValidate) {
        this.data = toValidate;
    }

    /**
     * <p>isValid.</p>
     *
     * @return a boolean
     */
    public boolean isValid() {
        return this.rationale.details()
                .stream()
                .map(Reason::result)
                .noneMatch(FAIL::equals);
    }

    /**
     * <p>applyRule.</p>
     *
     * @param tValidationRule a {@link be.sddevelopment.validation.ValidationRule} object
     * @return a {@link be.sddevelopment.validation.Checked} object
     */
    public Checked<T> applyRule(ValidationRule<T> tValidationRule) {
        var result = tValidationRule.rule().test(this.data);
        this.rationale.add(new Reason(tValidationRule.description(), result ? PASS : FAIL));
        return this;
    }

    /**
     * <p>of.</p>
     *
     * @param toValidate a T object
     * @param <T> a T class
     * @return a {@link be.sddevelopment.validation.Checked} object
     */
    public static <T> Checked<T> of(T toValidate) {
        return new Checked<>(toValidate);
    }

    /**
     * <p>rationale.</p>
     *
     * @return a {@link java.util.Optional} object
     */
    public Optional<EvaluationRationale> rationale() {
        return ofNullable(this.rationale);
    }
}
