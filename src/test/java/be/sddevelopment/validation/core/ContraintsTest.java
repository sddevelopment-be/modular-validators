package be.sddevelopment.validation.core;

import be.sddevelopment.commons.testing.naming.ReplaceUnderscoredCamelCasing;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;

import static be.sddevelopment.validation.core.Checks.haveNonNullField;

@DisplayName("Constraints utility class")
@DisplayNameGeneration(ReplaceUnderscoredCamelCasing.class)
class ContraintsTest implements WithAssertions {

    @Test
    void validationFails_givenAnObjectWithANullField_andUsingNonNullFieldPredicate() {
        var toValidate = new DataObject(null);
        var rule = haveNonNullField(DataObject::content);

        assertThat(rule.test(toValidate)).isFalse();
    }

    @Test
    void validationSucceeds_givenAnObjectWithNoNullField_andUsingNonNullFieldPredicate() {
        var toValidate = new DataObject("Some content");
        var rule = haveNonNullField(DataObject::content);

        assertThat(rule.test(toValidate)).isTrue();
    }

    private record DataObject(String content) {
    }
}