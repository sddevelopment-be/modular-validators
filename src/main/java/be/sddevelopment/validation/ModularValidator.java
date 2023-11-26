package be.sddevelopment.validation;

import java.util.function.Function;

public class ModularValidator<T> {

    private ModularValidator() {
    }

    public <S extends Function<T, Boolean>> ModularValidator<T> must(S requirement) {
        return this;
    }

    public static <S> ModularValidator<S> aValid(Class<S> clazz) {
        return new ModularValidator<>();
    }

    public Checked<T> evaluate(T toValidate) {
        return Checked.of(toValidate);
    }
}
