package be.sddevelopment.validation;

import static be.sddevelopment.validation.ModularRuleset.emptyRules;

/**
 * <p>Evaluated class.</p>
 * <p>
 * Represents the result of evaluating an object of any type.
 * The result of said evaluation is represented by a {@link Rationale}.
 * The original object is maintained as well, to allow for further processing, and flexibility in dealing with various validation states.
 * </p>
 *
 * <i>Intended as a <a href="https://en.wikipedia.org/wiki/Monad_(functional_programming)">monadic structure</a> to allow for fluent programming with the result of a validation.</i>
 *
 * @author stijnd
 * @version 1.0.0-SNAPSHOT
 */
public final class Constrainable<T> {

    private final T data;
    private final ModularRuleset<T> toAdhereTo;

    private Constrainable(T toValidate, ModularRuleset<T> toAdhereTo) {
        this.data = toValidate;
        this.toAdhereTo = toAdhereTo;
    }

    public boolean isValid() {
        return this.toAdhereTo.quickCheck(this.data).isPassing();
    }

    Constrainable<T> adheresTo(Constraint<T> constraint) {
        return new Constrainable<>(this.data, this.toAdhereTo.toBuilder().must(constraint).done());
    }

    public static <T> Constrainable<T> constrain(T toConstrain) {
        return constrain(toConstrain, emptyRules());
    }

    public static <T> Constrainable<T> constrain(T toConstrain, ModularRuleset<T> ruleset) {
        return of(toConstrain, ruleset);
    }

    public static <T> Constrainable<T> of(T toConstrain, ModularRuleset<T> ruleset) {
        return new Constrainable<>(toConstrain, ruleset);
    }

    public Rationale rationale() {
        return this.toAdhereTo.assess(this.data);
    }

    public void feedback(String errorMessage) throws InvalidObjectException {
        var rationale = this.toAdhereTo.check(this.data);
        if (rationale.isFailing()) {
            throw new InvalidObjectException(errorMessage, this.rationale());
        }
    }

    public void guard() throws InvalidObjectException {
        var rationale = this.toAdhereTo.quickCheck(this.data);
        if (rationale.isFailing()) {
            throw new InvalidObjectException("Object is invalid", rationale);
        }
    }

}
