package be.sddevelopment.validation.core;

import java.util.function.Consumer;
import java.util.function.Function;

import static be.sddevelopment.validation.core.ModularRuleset.emptyRules;

/**
 * <p>
 * Represents the result of evaluating an object of any type.
 * The result of said evaluation is represented by a {@link Rationale}.
 * The original object is maintained as well, to allow for further processing, and flexibility in dealing with various validation states.
 * </p>
 *
 * <i>Intended as a <a href="https://en.wikipedia.org/wiki/Monad_(functional_programming)">monadic structure</a> to allow for fluent programming with the result of a validation.</i>
 *
 * @author Stijn Dejongh
 * @version 1.0.0-SNAPSHOT
 */
public final class Constrained<T> {

    private final T data;
    private final ModularRuleset<T> toAdhereTo;

    private Constrained(T toValidate, ModularRuleset<T> toAdhereTo) {
        this.data = toValidate;
        this.toAdhereTo = toAdhereTo;
    }

    public boolean isValid() {
        return this.toAdhereTo.quickCheck(this.data).isPassing();
    }

    public Constrained<T> adheresTo(Constraint<T> constraint) {
        return new Constrained<>(this.data, this.toAdhereTo.toBuilder().must(constraint).done());
    }

    public static <T> Constrained<T> constrain(T toConstrain) {
        return constrain(toConstrain, emptyRules());
    }

    public static <T> Constrained<T> constrain(T toConstrain, ModularRuleset<T> ruleset) {
        return of(toConstrain, ruleset);
    }

    public static <T> Constrained<T> of(T toConstrain, ModularRuleset<T> ruleset) {
        return new Constrained<>(toConstrain, ruleset);
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

    public <R> Constrained<R> extract(Function<T, R> extractor) {
        return constrain(extractor.apply(this.data), emptyRules());
    }

    public void ifValid(Consumer<T> consumer) {
        if (this.isValid()) {
            consumer.accept(this.data);
        }
    }
}
