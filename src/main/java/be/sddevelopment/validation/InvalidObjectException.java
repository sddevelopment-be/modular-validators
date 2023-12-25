package be.sddevelopment.validation;

import java.util.List;

import static java.util.Collections.emptyList;

public class InvalidObjectException extends Exception {

    public InvalidObjectException(String errorMessage, EvaluationRationale rationale) {
        super(errorMessage);
    }

    public List<String> errors() {
        return emptyList();
    }
}
