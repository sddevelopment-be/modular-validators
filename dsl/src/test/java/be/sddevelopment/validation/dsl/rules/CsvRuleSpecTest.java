package be.sddevelopment.validation.dsl.rules;

import be.sddevelopment.commons.testing.naming.ReplaceUnderscoredCamelCasing;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;

import static be.sddevelopment.validation.dsl.rules.CsvRuleSpec.parametersFrom;

@DisplayName("Csv Rule Specification")
@DisplayNameGeneration(ReplaceUnderscoredCamelCasing.class)
class CsvRuleSpecTest implements WithAssertions {

    @Test
    void identifiesParametersFromSpecificationLine() {
        var ruleSpec = "FieldExists('SPECIES')";

        var parameters = parametersFrom(ruleSpec);

        assertThat(parameters).containsExactly("SPECIES");
    }

}