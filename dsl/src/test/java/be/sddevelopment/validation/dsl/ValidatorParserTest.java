package be.sddevelopment.validation.dsl;

import be.sddevelopment.commons.testing.naming.ReplaceUnderscoredCamelCasing;
import be.sddevelopment.validation.core.ModularRuleset;
import be.sddevelopment.validation.core.Rationale;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;

@DisplayName("Parsing of validation rules")
@DisplayNameGeneration(ReplaceUnderscoredCamelCasing.class)
class ValidatorParserTest implements WithAssertions {

    @Test
    void createsAValidatorBasedOnSpecifications() throws IOException {
        var validationSpec = Paths.get("src/test/resources/parsing/star_wars/STARWARS_VALIDATOR.puml");
        var dataFile = Paths.get("src/test/resources/parsing/star_wars/STARWARS_INPUT_DATA.csv");
        assertThat(validationSpec).exists();
        assertThat(validationSpec).isRegularFile();

        var validator = ValidatorParser.fromSpecification(validationSpec);

        assertThat(validator).isNotNull();

        var result = validator.check(dataFile);

        assertThat(result).isNotNull()
                .matches(Rationale::isPassing);
    }


}
