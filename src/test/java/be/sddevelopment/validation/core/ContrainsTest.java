package be.sddevelopment.validation.core;

import io.cucumber.java.en.Then;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static be.sddevelopment.validation.core.Constraints.haveNonNullField;

@DisplayName("Constraints utility class")
class ContrainsTest implements WithAssertions {

    @Test
    @Then("validation fails")
    void givenAnObjectWithANullField_havingNonNullFieldPredicate_fails() {
        var toValidate = new DataObject(null);
        var rule = haveNonNullField(DataObject::content);

        assertThat(rule.test(toValidate)).isFalse();
    }

    @Test
    void givenAnObjectWithNoNullField_havingNonNullFieldPredicate_succeeds() {
        var toValidate = new DataObject("Some content");
        var rule = haveNonNullField(DataObject::content);

        assertThat(rule.test(toValidate)).isTrue();
    }

    private record DataObject(String content) {
    }
}