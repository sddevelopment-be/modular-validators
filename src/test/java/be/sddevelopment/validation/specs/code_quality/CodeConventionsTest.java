package be.sddevelopment.validation.specs.code_quality;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaConstructor;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import static com.tngtech.archunit.core.domain.JavaModifier.FINAL;
import static com.tngtech.archunit.lang.SimpleConditionEvent.satisfied;
import static com.tngtech.archunit.lang.SimpleConditionEvent.violated;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.GeneralCodingRules.*;
import static org.junit.platform.commons.util.ReflectionUtils.makeAccessible;

@DisplayName("Complies with code conventions")
@AnalyzeClasses(packages = "be.sddevelopment.validation.core")
class CodeConventionsTest implements WithAssertions {

    @ArchTest
    private final ArchRule no_access_to_standard_streams = NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS;

    @ArchTest
    void no_access_to_standard_streams_as_method(JavaClasses classes) {
        noClasses().should(ACCESS_STANDARD_STREAMS).check(classes);
    }

    @ArchTest
    private final ArchRule no_classes_should_use_field_injection = NO_CLASSES_SHOULD_USE_FIELD_INJECTION;

    @ArchTest
    private final ArchRule no_generic_exceptions = NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS;

    @ArchTest
    private final ArchRule no_java_util_logging = NO_CLASSES_SHOULD_USE_JAVA_UTIL_LOGGING;

    @ArchTest
    private final ArchRule no_jodatime = NO_CLASSES_SHOULD_USE_JODATIME;

    @ArchTest
    private final ArchRule utility_classes_can_not_be_instantiated = noClasses()
            .that().haveModifier(FINAL)
            .and().haveSimpleNameNotContaining("Test")
            .and().areNotEnums()
            .should(beInstantiatable());

    private ArchCondition<? super JavaClass> beInstantiatable() {
        return new ArchCondition<>("be instantiatable") {
            @Override
            public void check(JavaClass item, ConditionEvents events) {
                if (isInstantiatable(item)) {
                    events.add(satisfied(item, "Class " + item.getName() + " is instantiatable"));
                } else {
                    events.add(violated(item, "Class " + item.getName() + " is not instantiatable"));
                }
            }
        };
    }

    boolean isInstantiatable(JavaClass classToCheck) {

        try {
            if (classToCheck.getConstructors().stream()
                    .map(JavaConstructor::getParameters)
                    .anyMatch(List::isEmpty)) {
                var constructor = classToCheck.getConstructor().reflect();
                makeAccessible(constructor);
                constructor.newInstance();
                return true;
            } else {
                return false;
            }
        } catch (UnsupportedOperationException | InstantiationException |
                 IllegalAccessException | InvocationTargetException e) {
            return false;
        }
    }
}
