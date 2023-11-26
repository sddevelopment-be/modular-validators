package be.sddevelopment.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class ModularValidator<T> {

    private final List<ValidationRule<T>> rules = new ArrayList<>();

    private ModularValidator() {
    }

    public <S extends Function<T, Boolean>> ModularValidator<T> must(S requirement) {
        var predicate = new Predicate<T>() {
            @Override
            public boolean test(T t) {
                return requirement.apply(t);
            }
        };
        this.rules.add(new ValidationRule<>(predicate));
        return this;
    }

    public static <S> ModularValidator<S> aValid(Class<S> clazz) {
        return new ModularValidator<>();
    }

    public Checked<T> evaluate(T toValidate) {
        return Checked.of(toValidate);
    }
}
