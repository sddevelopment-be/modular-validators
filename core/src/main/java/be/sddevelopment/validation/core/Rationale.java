package be.sddevelopment.validation.core;

import java.util.ArrayList;
import java.util.List;

public class Rationale {
    private final List<Reason> details = new ArrayList<>();
    private Evidence evidence = Evidence.empty();

    private Rationale() {
    }

    private Rationale(List<Reason> reasons) {
        this();
        this.details.addAll(reasons);
    }

    public static Rationale emptyRationale() {
        return new Rationale();
    }

    public static Rationale rationaleWithReasons(List<Reason> reasons) {
        return new Rationale(reasons);
    }

    public static Rationale rationaleWithReasonsAndProof(List<Reason> reasons, Evidence evidence) {
        return new Rationale(reasons).asShownBy(evidence);
    }

    public Rationale asShownBy(Evidence evidence) {
        this.evidence = evidence;
        return this;
    }

    public Evidence evidence() {
        return evidence;
    }

    public List<Reason> details() {
        return details;
    }

    public Rationale add(Reason reason) {
        this.details.add(reason);
        return this;
    }

    public boolean isPassing() {
        return this.details.stream()
                .allMatch(Reason::isPassing);
    }

    public boolean isFailing() {
        return !this.isPassing();
    }

}
