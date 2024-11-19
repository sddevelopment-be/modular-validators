package be.sddevelopment.validation.specs.code_quality;

import be.sddevelopment.commons.testing.conventions.CodeConventions;
import be.sddevelopment.commons.testing.naming.ReplaceUnderscoredCamelCasing;
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
import org.junit.jupiter.api.DisplayNameGeneration;

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
public class ConventionsAdherenceTests implements CodeConventions {

    /* This class can be empty, it is used to run the test of its parent interface
     * CodeConventions. This interface contains the tests for the code conventions,
     * and will execute them based on the {@code @AnalyzeClasses} annotation.
     */

}
