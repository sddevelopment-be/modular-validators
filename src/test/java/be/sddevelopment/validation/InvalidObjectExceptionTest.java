package be.sddevelopment.validation;

import net.serenitybdd.junit5.SerenityJUnit5Extension;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SerenityJUnit5Extension.class)
class InvalidObjectExceptionTest implements WithAssertions {

    @Test
    @Disabled("Working on it...")
    void invalidObjectExceptionMustContainAllErrors_givenMultipleErrors() {
        var rationale = new EvaluationRationale();

        var result = new InvalidObjectException("You done goofed", rationale);

        assertThat(result).isNotNull().extracting(InvalidObjectException::getMessage).isEqualTo("You done goofed");
        assertThat(result.errors()).containsExactly("");
    }

}