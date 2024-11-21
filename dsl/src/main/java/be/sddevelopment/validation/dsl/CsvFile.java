package be.sddevelopment.validation.dsl;

import be.sddevelopment.commons.exceptions.ExceptionSuppressor;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Vector;
import java.util.stream.Stream;

/**
 * Represents a CSV file with a header and data lines, to be used as an input for a {@link be.sddevelopment.validation.core.ModularRuleset}.
 *
 * @param fileIdentifier a regular expression, used to identify the file in the validation report.
 * @param headerFields   the header fields of the CSV file.
 * @param lines          the data lines of the CSV file.
 *
 */
public record CsvFile(
        String fileIdentifier,
        Vector<String> headerFields,
        Vector<Vector<String>> lines
) {
    private static final String DEFAULT = ".*\\.csv";

    public CsvFile {
        if (headerFields == null || lines == null) {
            throw new IllegalArgumentException("Header fields and lines must not be null");
        }
    }

    public Vector<String> line(int lineNumber) {
        return lines.get(lineNumber);
    }

    public static CsvFile fromLines(List<String> lines) throws IOException {
        if (lines.isEmpty()) {
            throw new IllegalArgumentException("No lines provided. A data file requires at least one line");
        }

        var header = parseHeader(lines.getFirst());

        var dataLines = lines.stream().skip(1).map(ExceptionSuppressor.uncheck(CsvFile::parseLine)).toList();
        return new CsvFile(DEFAULT, header, new Vector<>(dataLines));
    }

    private static Vector<String> parseHeader(String lineToParse) throws IOException {
        List<String> parsedFields = Stream.of(createParser().parseLine(lineToParse))
                .map(String::trim)
                .toList();
        return new Vector<>(parsedFields);
    }

    private static Vector<String> parseLine(String lineToParse) throws IOException {
        return new Vector<>(Stream.of(createParser().parseLine(lineToParse)).map(String::trim).toList());
    }

    private static CSVParser createParser() {
        return new CSVParserBuilder()
                .withSeparator(',')
                .withEscapeChar('\\')
                .withQuoteChar('"')
                .build();
    }

    public static @Nullable CsvFile fromFile(Path dataFile) {
        return null;
    }

    public boolean isEmpty() {
        return this.lines.isEmpty();
    }
}
