package be.sddevelopment.validation;

import java.util.ArrayList;
import java.util.List;

public class EvaluationRationale {
    private final List<Reason> details = new ArrayList<>();

    public List<Reason> details() {
        return details;
    }

    public void add(Reason reason) {
        this.details.add(reason);
    }
}
