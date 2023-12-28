package be.sddevelopment.validation.specs.code_quality;

import be.sddevelopment.validation.core.Constraints;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import static org.junit.platform.commons.util.ReflectionUtils.makeAccessible;
import static org.junit.platform.commons.util.ReflectionUtils.newInstance;

@DisplayName("Complies with code conventions")
class CodeConventionsTest implements WithAssertions {

    /*
     * This test is not strictly necessary, but it's a good way to document the fact that the class is not instantiatable.
     * In addition, it stops the coverage metrics from complaining about it.
     */
    @Test
    void utilityClassesShouldNotBeInstantiatable() throws NoSuchMethodException {
        var constructor = Constraints.class.getDeclaredConstructor();
        assertThat(constructor).isNotNull()
                .extracting(Constructor::getModifiers)
                .matches(Modifier::isPrivate);

        makeAccessible(constructor);

        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> newInstance(Constraints.class))
                .withMessage("Utility classes (containing shared methods or constants) should not be instantiated.");
    }
}
