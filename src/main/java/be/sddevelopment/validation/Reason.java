package be.sddevelopment.validation;

import static be.sddevelopment.validation.Evaluation.FAIL;
import static be.sddevelopment.validation.Evaluation.PASS;

public record Reason(String rationale, Evaluation result) {

    public static Reason passed(String rationale) {
        return new Reason(rationale, PASS);
    }
    public static Reason failed(String rationale) {
        return new Reason(rationale, FAIL);
    }

}
