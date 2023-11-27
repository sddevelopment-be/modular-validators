package be.sddevelopment.validation;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

class ValidationsTest implements WithAssertions {

    @Test
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

    private record DataObject(String content) {}
}