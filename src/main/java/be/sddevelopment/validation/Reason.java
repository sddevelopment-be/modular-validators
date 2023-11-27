package be.sddevelopment.validation;

import static be.sddevelopment.validation.Evaluation.PASS;

public record Reason(String rationale, Evaluation result) {

    public static Reason pass(String rationale) {
        return new Reason(rationale, PASS);
    }
}
