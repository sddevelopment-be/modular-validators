package be.sddevelopment.validation;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

public class InvalidObjectException extends Exception {

    private final List<String> errors = new ArrayList<>();

    public InvalidObjectException(String errorMessage, Rationale rationale) {
        super(errorMessage);
        rationale.details()
                .stream().map(this::errorFromReason)
                .forEach(this.errors::add);
    }

    private String errorFromReason(Reason reason) {
        return format("%s: [%s]", reason.result().name(), reason.rationale());
    }

    public List<String> errors() {
        return new ArrayList<>(this.errors);
    }
}
