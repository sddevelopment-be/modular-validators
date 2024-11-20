package be.sddevelopment.validation.dsl;

/**
 * Exception thrown when an error occurs while parsing a validation specification.
 * To be used by the {@link FileValidatorParser} or related specification file to {@link be.sddevelopment.validation.core.ModularRuleset} parsers.
 *
 * @since 1.1.0
 */
public class SpecificationParserException extends Exception {

    public SpecificationParserException(String message) {
        super(message);
    }

    public SpecificationParserException(String message, Throwable cause) {
        super(message, cause);
    }
}
