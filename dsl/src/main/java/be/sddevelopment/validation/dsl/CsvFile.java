package be.sddevelopment.validation.dsl;

import java.nio.file.Path;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record CsvFile (
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

    public static CsvFile fromLines(List<String> lines) {
        if(lines.isEmpty()) {
            throw new IllegalArgumentException("No lines provided. A data file requires at least one line");
        }

        var header = parseHeader(lines.getFirst());
        var dataLines = lines.stream().skip(1).map(CsvFile::parseLine).toList();
        return new CsvFile(DEFAULT, header, new Vector<>(dataLines));
    }

    private static Vector<String> parseHeader(String s) {
        return null;
    }

    private static Vector<String> parseLine(String s) {
        return null;
    }

    public static CsvFile fromFile(Path dataFile) {
        return null;
    }

    public boolean isEmpty() {
        return this.lines.isEmpty();
    }
}
