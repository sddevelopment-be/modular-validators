package be.sddevelopment.validation;

import net.serenitybdd.junit5.SerenityJUnit5Extension;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static be.sddevelopment.validation.Reason.failed;
import static be.sddevelopment.validation.Reason.passed;

@ExtendWith(SerenityJUnit5Extension.class)
class InvalidObjectExceptionTest implements WithAssertions {

    @Test
    void invalidObjectExceptionMustContainAllErrors_givenMultipleErrors() {
        var rationale = new EvaluationRationale()
                .add(passed("This is fine"))
                .add(failed("The consequences will never be the same"));

        var result = new InvalidObjectException("You done goofed", rationale);

        assertThat(result).isNotNull().extracting(InvalidObjectException::getMessage).isEqualTo("You done goofed");
        assertThat(result.errors()).containsExactlyInAnyOrder(
                "PASS: [This is fine]",
                "FAIL: [The consequences will never be the same]"
        );
    }

}