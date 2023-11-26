package be.sddevelopment.validation;

public final class Checked<T> {

    private final T data;

    private Checked(T toValidate) {
        this.data = toValidate;
    }

    public boolean isValid() {
        return false;
    }

    public static <T> Checked<T> of(T toValidate) {
        return new Checked<>(toValidate);
    }
}
