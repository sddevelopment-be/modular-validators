package be.sddevelopment.validation;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SerenityJUnit5Extension.class)
class ValidationsTest implements WithAssertions {
    @Test
    @Given("an Object with a null field, having a non-null field predicate")
    @Then("validation fails")
    void givenAnObjectWithANullField_havingNonNullFieldPredicate_fails() {
        var toValidate = new DataObject(null);
        var rule = Validations.haveNonNullField(DataObject::content);

        assertThat(rule.test(toValidate)).isFalse();
    }

    @Test
    void givenAnObjectWithNoNullField_havingNonNullFieldPredicate_succeeds() {
        var toValidate = new DataObject("Some content");
        var rule = Validations.haveNonNullField(DataObject::content);

        assertThat(rule.test(toValidate)).isTrue();
    }

    private record DataObject(String content) {
    }
}