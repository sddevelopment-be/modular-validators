package be.sddevelopment.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public final class ModularValidator<T> {

    private final List<ValidationRule<T>> rules = new ArrayList<>();

    private ModularValidator(List<ValidationRule<T>> rules) {
        this.rules.addAll(rules);
    }

    public Checked<T> evaluate(T toValidate) {
        Checked<T> accumulator = Checked.of(toValidate);
        rules.forEach(accumulator::applyRule);
        return accumulator;
    }

    public static <S> ModularValidatorBuilder<S> aValid(Class<S> ignoredClazz) {
        return new ModularValidatorBuilder<>();
    }

    public static class ModularValidatorBuilder<S> {
        private final List<ValidationRule<S>> rules = new ArrayList<>();

        private ModularValidatorBuilder() {
        }

        public ModularValidatorBuilder<S> must(Predicate<S> requirement, String description) {
            return must(new ValidationRule<>(requirement, description));
        }

        public ModularValidatorBuilder<S> must(ValidationRule<S> rule) {
            this.rules.add(rule);
            return this;
        }

        public ModularValidator<S> iHaveSpoken() {
            return done();
        }

        public ModularValidator<S> done() {
            return new ModularValidator<>(this.rules);
        }
    }
}
