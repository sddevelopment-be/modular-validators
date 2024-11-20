package be.sddevelopment.validation.dsl;

import be.sddevelopment.commons.testing.naming.ReplaceUnderscoredCamelCasing;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;

@DisplayName("Comma Separated Values File")
@DisplayNameGeneration(ReplaceUnderscoredCamelCasing.class)
class CsvFileTest implements WithAssertions {

    @Test
    void canBeCreatedFromLines() {
        var dataWithHeader = """
            NAME,HEIGHT,SPECIES
            Luke Skywalker,172,Human
            C-3PO,167,Droid
            R2-D2,96,Droid
            Boba Fett,183, Human
        """;

        var csvFile = CsvFile.fromLines(dataWithHeader.lines());

        assertThat(csvFile).isNotNull()
                .extracting(CsvFile::headerFields)
                .asInstanceOf(LIST)
                .containsExactly("NAME", "HEIGHT", "SPECIES");
        assertThat(csvFile.line(0)).containsExactly("Luke Skywalker", "172", "Human");
    }
}