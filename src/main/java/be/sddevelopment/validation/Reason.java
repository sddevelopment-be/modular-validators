package be.sddevelopment.validation;

import static be.sddevelopment.validation.Evaluation.FAIL;
import static be.sddevelopment.validation.Evaluation.PASS;

/**
 * <p>Reason class.</p>
 * Represents the evaluation of a single {@link ValidationRule}, and will be included in a {@link EvaluationRationale}.
 * Each reason will tell the user whether a particular rule passed or failed, and will include a description (aka "rationale") to explain why a certain result was achieved.
 *
 * @version 1.0.0-SNAPSHOT
 * @apiNote This class is immutable, and provides static factory methods to simplify usage, and increase readability..
 * @since 1.0.0-SNAPSHOT
 */
public record Reason(String rationale, Evaluation result) {

    public static Reason passed(String rationale) {
        return new Reason(rationale, PASS);
    }

    public static Reason failed(String rationale) {
        return new Reason(rationale, FAIL);
    }

}
