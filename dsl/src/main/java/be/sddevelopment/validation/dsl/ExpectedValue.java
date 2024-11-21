package be.sddevelopment.validation.dsl;

public record ExpectedValue(
        String field,
        String value
) {

    public static ExpectedValue withValue(String field, String value) {
        return new ExpectedValue(field, value);
    }

    @Override
    public String toString() {
        return "%s::'%s'".formatted(field, value);
    }
}
