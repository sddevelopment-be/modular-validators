package be.sddevelopment.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class ModularValidator<T> {

    private final List<ValidationRule<T>> rules = new ArrayList<>();

    private ModularValidator() {
    }

    public <S extends Predicate<T>> ModularValidator<T> must(S requirement) {
        this.rules.add(new ValidationRule<>(requirement, "musn't be blank"));
        return this;
    }

    public static <S> ModularValidator<S> aValid(Class<S> clazz) {
        return new ModularValidator<>();
    }

    public Checked<T> evaluate(T toValidate) {
        Checked<T> accumulator = Checked.of(toValidate);
        rules.forEach(accumulator::applyRule);
        return accumulator;
    }
}
