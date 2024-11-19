package be.sddevelopment.validation.dsl;

import be.sddevelopment.commons.testing.conventions.CodeConventions;
import be.sddevelopment.commons.testing.naming.ReplaceUnderscoredCamelCasing;
import com.tngtech.archunit.junit.AnalyzeClasses;
import org.junit.jupiter.api.DisplayNameGeneration;

@AnalyzeClasses(packages = "be.sddevelopment.commons")
@DisplayNameGeneration(ReplaceUnderscoredCamelCasing.class)
public class ConventionsAdherenceTests implements CodeConventions {

    /* This class can be empty, it is used to run the test of its parent interface
     * CodeConventions. This interface contains the tests for the code conventions,
     * and will execute them based on the {@code @AnalyzeClasses} annotation.
     */

}
