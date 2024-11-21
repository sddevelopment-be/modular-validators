package be.sddevelopment.validation.dsl;

import be.sddevelopment.commons.testing.naming.ReplaceUnderscoredCamelCasing;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

@DisplayName("Comma Separated Values File")
@DisplayNameGeneration(ReplaceUnderscoredCamelCasing.class)
class CsvFileTest implements WithAssertions {

    @Nested
    class LineBasedParsing {

        @Test
        void dataIsAccessibleAfterParsing() throws IOException {
            var dataWithHeader = """
                        NAME,HEIGHT,SPECIES
                        Luke Skywalker,172,Human
                        C-3PO,167,Droid
                        R2-D2,96,Droid
                        Boba Fett,183, Human
                    """;

            var csvFile = CsvFile.fromLines(dataWithHeader.lines().toList());

            assertThat(csvFile).isNotNull()
                    .extracting(CsvFile::headerFields)
                    .asInstanceOf(LIST)
                    .containsExactly("NAME", "HEIGHT", "SPECIES");
            assertThat(csvFile.line(0)).containsExactly("Luke Skywalker", "172", "Human");
        }

        @Test
        void parsingRequiresAtLeastAHeaderLine() {
            assertThatException().isThrownBy(() -> CsvFile.fromLines(List.of()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .withMessage("No lines provided. A data file requires at least one line")
                    .withNoCause();
        }

        @Test
        void canHandleEmptyDataSets() throws IOException {
            var headerOnly = List.of("NAME,HEIGHT,SPECIES");

            var csvFile = CsvFile.fromLines(headerOnly);

            assertThat(csvFile).isNotNull();
            assertThat(csvFile.headerFields())
                    .asInstanceOf(LIST)
                    .containsExactly("NAME", "HEIGHT", "SPECIES");
            assertThat(csvFile).matches(CsvFile::isEmpty);
        }
    }
}