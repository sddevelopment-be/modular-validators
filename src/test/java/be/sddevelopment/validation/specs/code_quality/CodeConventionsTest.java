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
    static final ArchRule CLASSES_DO_NOT_ACCESS_STANDARD_STREAMS = NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS;

    @ArchTest
    void noAccessToStandardStreamsAsMethod(JavaClasses classes) {
        noClasses().should(ACCESS_STANDARD_STREAMS).check(classes);
    }

    @ArchTest
    static final ArchRule NO_CLASSES_USE_FIELD_INJECTION = NO_CLASSES_SHOULD_USE_FIELD_INJECTION;

    @ArchTest
    static final ArchRule NO_GENERIC_EXCEPTION = NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS;

    @ArchTest
    static final ArchRule NO_JAVA_UTIL_LOGGING = NO_CLASSES_SHOULD_USE_JAVA_UTIL_LOGGING;

    @ArchTest
    static final ArchRule NO_JODATIME = NO_CLASSES_SHOULD_USE_JODATIME;

    @ArchTest
    static final ArchRule UTILITY_CLASSES_CAN_NOT_BE_INSTANTIATED = noClasses()
            .that().haveModifier(FINAL)
            .and().haveSimpleNameNotContaining("Test")
            .and().areNotEnums()
            .should(beInstantiatable());

    static ArchCondition<? super JavaClass> beInstantiatable() {
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

    static boolean isInstantiatable(JavaClass classToCheck) {

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
