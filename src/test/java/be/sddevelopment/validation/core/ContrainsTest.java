package be.sddevelopment.validation.core;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static be.sddevelopment.validation.core.Checks.haveNonNullField;

@DisplayName("Constraints utility class")
class ContrainsTest implements WithAssertions {

    @Test
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