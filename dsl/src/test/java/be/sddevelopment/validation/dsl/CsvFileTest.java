package be.sddevelopment.validation.dsl;

import be.sddevelopment.commons.testing.naming.ReplaceUnderscoredCamelCasing;
import be.sddevelopment.validation.core.Constrained;
import be.sddevelopment.validation.core.InvalidObjectException;
import be.sddevelopment.validation.core.ModularRuleset;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Vector;

import static be.sddevelopment.validation.core.ModularRuleset.aValid;
import static be.sddevelopment.validation.core.Reason.passed;
import static be.sddevelopment.validation.dsl.FieldValue.withValue;
import static be.sddevelopment.validation.dsl.rules.CsvValidationRules.*;

@Slf4j
@DisplayName("Comma Separated Values File")
@DisplayNameGeneration(ReplaceUnderscoredCamelCasing.class)
class CsvFileTest implements WithAssertions {

    @Test
    void doesNotAllowNullHeaders() {
        var identifier = "STARWARS_INPUT_DATA.csv";
        var lines = new Vector<Vector<String>>();

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new CsvFile(identifier, null, lines)
                )
                .withMessage("Header fields and lines must not be null")
                .withNoCause();
    }

    @Test
    void doesNotAllowNullLines() {
        var identifier = "STARWARS_INPUT_DATA.csv";
        var header = new Vector<>(List.of("NAME", "HEIGHT", "SPECIES"));

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new CsvFile(identifier, header, null)
                )
                .withMessage("Header fields and lines must not be null")
                .withNoCause();
    }

    @Test
    void containsFileNameAfterParsing() throws Exception {
        var dataFile = Paths.get(CsvFileTest.class.getClassLoader().getResource("parsing/star_wars/STARWARS_INPUT_DATA.csv").toURI());
        assertThat(dataFile).exists().isRegularFile().hasExtension("csv");

        var result = CsvFile.fromFile(dataFile);

        assertThat(result).satisfies(CsvFile::isNotEmpty);
        assertThat(result.fileIdentifier()).contains("STARWARS_INPUT_DATA.csv");
    }

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

    @Nested
    class ContentAccessors {
        @Test
        void listsAllDistinctValuesForExistingField() throws Exception {
            var dataWithHeader = """
                        NAME,HEIGHT,SPECIES
                        Luke Skywalker,172,Human
                        C-3PO,167,Droid
                        R2-D2,96,Droid
                        Boba Fett,183, Human
                    """;
            var file = CsvFile.fromLines(dataWithHeader.lines().toList());

            var result = file.distinctValuesFor("SPECIES");

            assertThat(result).contains("Human", "Droid");
        }

        @Test
        void listNoValuesForMissingField() throws Exception {
            var dataWithHeader = """
                        NAME,HEIGHT,SPECIES
                        Luke Skywalker,172,Human
                        C-3PO,167,Droid
                    """;
            var file = CsvFile.fromLines(dataWithHeader.lines().toList());

            var result = file.distinctValuesFor("HOMEWORLD");

            assertThat(result).isEmpty();
        }

        @Test
        void listNoValuesForEmptyFile() throws Exception {
            var dataWithHeader = """
                    NAME,HEIGHT,SPECIES
                    """;
            var file = CsvFile.fromLines(dataWithHeader.lines().toList());
            assertThat(file).isNotNull().satisfies(CsvFile::isEmpty);

            var result = file.distinctValuesFor("NAME");

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("Using CsvFile for validation")
    class UseCaseTest {

        public static final ModularRuleset<CsvFile> THE_DROIDS_WE_ARE_LOOKING_FOR = aValid(CsvFile.class)
                .must(haveField("NAME"))
                .must(haveDistinctValuesFor("NAME"))
                .must(haveField("SPECIES"))
                .must(containRecord(withName("C-3PO"), withSpecies("Droid"), withHomeWorld("Tatooine")))
                .must(containRecord(withName("R2-D2"), withSpecies("Droid"), withHomeWorld("Naboo")))
                .iHaveSpoken();

        @Test
        void passesWhenExpectedRecordsArePresent() throws Exception {
            var dataFile = Paths.get(CsvFileTest.class.getClassLoader().getResource("parsing/star_wars/STARWARS_INPUT_DATA.csv").toURI());
            assertThat(dataFile).exists().isRegularFile().hasExtension("csv");

            var fileToValidate = CsvFile.fromFile(dataFile);
            assertThat(fileToValidate).isNotNull().satisfies(CsvFile::isNotEmpty);

            var result = THE_DROIDS_WE_ARE_LOOKING_FOR.constrain(fileToValidate);

            assertThat(result).satisfies(Constrained::isValid);
            assertThat(result.rationale().details())
                    .contains(
                            passed("Record identified by NAME::'C-3PO' with values [SPECIES::'Droid' and HOMEWORLD::'Tatooine'] must exist in the data file"),
                            passed("Record identified by NAME::'R2-D2' with values [SPECIES::'Droid' and HOMEWORLD::'Naboo'] must exist in the data file")
                    );
        }

        @Test
        void failsWhenExpectedRecordsAreNotPresent() throws Exception {
            var fileToValidate = CsvFile.fromLines(
                    """
                            NAME,HEIGHT,MASS,HAIR_COLOR,SKIN_COLOR,EYE_COLOR,BIRTH_YEAR,GENDER,HOMEWORLD,SPECIES
                            Luke Skywalker,172,77,blond,fair,blue,19BBY,male,Tatooine,Human
                            Leia Organa,150,49,brown,light,brown,19BBY,female,Alderaan,Human
                            Obi-Wan Kenobi,182,77,"auburn, white",fair,blue-gray,57BBY,male,Stewjon,Human
                            Wilhuff Tarkin,180,NA,"auburn, grey",fair,blue,64BBY,male,Eriadu,Human
                            Chewbacca,228,112,brown,NA,blue,200BBY,male,Kashyyyk,Wookiee
                            Han Solo,180,80,brown,fair,brown,29BBY,male,Corellia,Human        
                            """.lines().toList()
            );
            assertThat(fileToValidate).isNotNull().satisfies(CsvFile::isNotEmpty);

            var result = THE_DROIDS_WE_ARE_LOOKING_FOR.constrain(fileToValidate);
            assertThatExceptionOfType(InvalidObjectException.class)
                    .isThrownBy(() -> result.feedback("These are not the droids you are looking for"))
                    .withMessage("These are not the droids you are looking for")
                    .extracting(InvalidObjectException::errors).asInstanceOf(LIST)
                    .contains(
                            "FAIL: [Record identified by NAME::'C-3PO' with values [SPECIES::'Droid' and HOMEWORLD::'Tatooine'] must exist in the data file]",
                            "FAIL: [Record identified by NAME::'R2-D2' with values [SPECIES::'Droid' and HOMEWORLD::'Naboo'] must exist in the data file]"
                    );
        }

        private static FieldValue withSpecies(String species) {
            return withValue("SPECIES", species);
        }

        private static FieldValue withHomeWorld(String homeWorld) {
            return withValue("HOMEWORLD", homeWorld);
        }

        private static FieldValue withName(String name) {
            return withValue("NAME", name);
        }

    }
}