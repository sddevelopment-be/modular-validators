package be.sddevelopment.validation;

import java.util.Optional;

import static be.sddevelopment.validation.Evaluation.FAIL;
import static be.sddevelopment.validation.Evaluation.PASS;
import static java.util.Optional.ofNullable;

/**
 * <p>Checked class.</p>
 * <p>
 * Represents the result of evaluating an object of any type.
 * The result of said evaluation is represented by a {@link EvaluationRationale}.
 * The original object is maintained as well, to allow for further processing, and flexibility in dealing with various validation states.
 *
 * Intended as a <a href="https://en.wikipedia.org/wiki/Monad_(functional_programming)">monadic structure</a> to allow for fluent programming with the result of a validation.
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

    public boolean isValid() {
        return this.rationale.details()
                .stream()
                .map(Reason::result)
                .noneMatch(FAIL::equals);
    }

    public boolean isInvalid() {
        return !isValid();
    }

    Checked<T> applyRule(ValidationRule<T> tValidationRule) {
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

    public void guard(String errorMessage) throws InvalidObjectException {
        if (isInvalid()) {
            throw new InvalidObjectException(errorMessage, this.rationale);
        }
    }
}
