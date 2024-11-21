package be.sddevelopment.validation.specs.code_quality;

import be.sddevelopment.commons.testing.conventions.CodeConventions;
import com.tngtech.archunit.junit.AnalyzeClasses;
import org.junit.jupiter.api.DisplayName;

@DisplayName("Complies with code conventions")
@AnalyzeClasses(packages = "be.sddevelopment.validation.core")
public class ConventionsAdherenceTests implements CodeConventions {

    /* This class can be empty, it is used to run the test of its parent interface
     * CodeConventions. This interface contains the tests for the code conventions,
     * and will execute them based on the {@code @AnalyzeClasses} annotation.
     */

}
