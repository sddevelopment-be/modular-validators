package be.sddevelopment.validation.core;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static java.util.Collections.emptyList;

public final class ModularRuleset<T> {

    private final List<Constraint<T>> rules = new ArrayList<>();

    private ModularRuleset(List<Constraint<T>> rules) {
        this.rules.addAll(rules);
    }

    public Constrainable<T> constrain(T toValidate) {
        return Constrainable.of(toValidate, this);
    }

    public Rationale assess(T toValidate) {
        return check(toValidate);
    }

    public Rationale check(T data) {
        return Rationale.rationaleWithReasons(rules.stream()
                .map(rule -> rule.applyTo(data))
                .toList());
    }

    public Rationale quickCheck(T data) {
        var result = Rationale.emptyRationale();
        for (var rule : this.rules) {
            var reason = rule.applyTo(data);
            result.add(reason);
            if (reason.isFailing()) {
                return result;
            }
        }
        return result;
    }

    public static <S> ModularValidatorBuilder<S> aValid(Class<S> ignoredClazz) {
        return new ModularValidatorBuilder<>();
    }

    public ModularValidatorBuilder<T> toBuilder() {
        return new ModularValidatorBuilder<T>().withRules(this.rules);
    }

    public static <T> ModularRuleset<T> emptyRules() {
        return new ModularRuleset<>(emptyList());
    }

    public static class ModularValidatorBuilder<S> {

        private final List<Constraint<S>> rules = new ArrayList<>();

        private ModularValidatorBuilder() {
        }

        public ModularValidatorBuilder<S> must(Predicate<S> requirement, String description) {
            return must(new Constraint<>(requirement, description));
        }

        public ModularValidatorBuilder<S> must(Constraint<S> rule) {
            this.rules.add(rule);
            return this;
        }

        public ModularValidatorBuilder<S> withRules(List<Constraint<S>> rules) {
            this.rules.addAll(rules);
            return this;
        }

        public ModularRuleset<S> iHaveSpoken() {
            return done();
        }

        public ModularRuleset<S> done() {
            return new ModularRuleset<>(this.rules);
        }
    }
}
