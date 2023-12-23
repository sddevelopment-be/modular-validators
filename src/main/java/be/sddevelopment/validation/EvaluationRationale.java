package be.sddevelopment.validation;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>EvaluationRationale class.</p>
 *
 * @author stijnd
 * @version 1.0.0-SNAPSHOT
 */
public class EvaluationRationale {
    private final List<Reason> details = new ArrayList<>();

    /**
     * <p>details.</p>
     *
     * @return a {@link java.util.List} object
     */
    public List<Reason> details() {
        return details;
    }

    /**
     * <p>add.</p>
     *
     * @param reason a {@link be.sddevelopment.validation.Reason} object
     */
    public void add(Reason reason) {
        this.details.add(reason);
    }
}
