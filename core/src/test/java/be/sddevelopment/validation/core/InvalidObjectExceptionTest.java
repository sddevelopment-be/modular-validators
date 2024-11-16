package be.sddevelopment.validation.core;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static be.sddevelopment.validation.core.Reason.failed;
import static be.sddevelopment.validation.core.Reason.passed;

@DisplayName("InvalidObject Exceptions")
class InvalidObjectExceptionTest implements WithAssertions {

    @Test
    void mustContainAllErrors_givenMultipleErrors() {
        var rationale = Rationale.emptyRationale()
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