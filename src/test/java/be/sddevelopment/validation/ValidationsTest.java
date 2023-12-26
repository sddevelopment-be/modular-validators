package be.sddevelopment.validation;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import static org.junit.platform.commons.util.ReflectionUtils.makeAccessible;
import static org.junit.platform.commons.util.ReflectionUtils.newInstance;

@ExtendWith(SerenityJUnit5Extension.class)
class ValidationsTest implements WithAssertions {

    /*
     * This test is not strictly necessary, but it's a good way to document the fact that the class is not instantiatable.
     * In addition, it stops the coverage metrics from complaining about it.
     */
    @Test
    void utilityClassesShouldNotBeInstantiatable() throws NoSuchMethodException {
        var constructor = Validations.class.getDeclaredConstructor();
        assertThat(constructor).isNotNull()
                .extracting(Constructor::getModifiers)
                .matches(Modifier::isPrivate);

        makeAccessible(constructor);

        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> newInstance(Validations.class))
                .withMessage("Utility classes (containing shared methods or constants) should not be instantiated.");
    }

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