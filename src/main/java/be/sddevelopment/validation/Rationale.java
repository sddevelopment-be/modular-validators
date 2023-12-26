package be.sddevelopment.validation;

import java.util.ArrayList;
import java.util.List;

public class Rationale {
    private final List<Reason> details = new ArrayList<>();

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

    public List<Reason> details() {
        return details;
    }

    public Rationale add(Reason reason) {
        this.details.add(reason);
        return this;
    }
}
