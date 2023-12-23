package be.sddevelopment.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * <p>ModularValidator class.</p>
 *
 * @author stijnd
 * @version 1.0.0-SNAPSHOT
 */
public class ModularValidator<T> {

    private final List<ValidationRule<T>> rules = new ArrayList<>();

    private ModularValidator() {
    }

    /**
     * <p>must.</p>
     *
     * @param requirement a S object
     * @param <S> a S class
     * @return a {@link be.sddevelopment.validation.ModularValidator} object
     */
    public <S extends Predicate<T>> ModularValidator<T> must(S requirement) {
        this.rules.add(new ValidationRule<>(requirement, "musn't be blank"));
        return this;
    }

    /**
     * <p>aValid.</p>
     *
     * @param clazz a {@link java.lang.Class} object
     * @param <S> a S class
     * @return a {@link be.sddevelopment.validation.ModularValidator} object
     */
    public static <S> ModularValidator<S> aValid(Class<S> clazz) {
        return new ModularValidator<>();
    }

    /**
     * <p>evaluate.</p>
     *
     * @param toValidate a T object
     * @return a {@link be.sddevelopment.validation.Checked} object
     */
    public Checked<T> evaluate(T toValidate) {
        Checked<T> accumulator = Checked.of(toValidate);
        rules.forEach(accumulator::applyRule);
        return accumulator;
    }
}
