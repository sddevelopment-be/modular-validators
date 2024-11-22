package be.sddevelopment.validation.dsl;

public record FieldValue(
        String field,
        String value
) {

    public static FieldValue withValue(String field, String value) {
        return new FieldValue(field, value);
    }

    @Override
    public String toString() {
        return "%s::'%s'".formatted(field, value);
    }
}
