package be.sddevelopment.validation.dsl;

import java.nio.file.Path;
import java.util.Vector;
import java.util.stream.Stream;

public record CsvFile (
    String fileIdentifier,
    Vector<String> headerFields,
    Vector<Vector<String>> lines
) {
    public CsvFile {
        if (headerFields == null || lines == null) {
            throw new IllegalArgumentException("Header fields and lines must not be null");
        }
    }

    public Vector<String> line(int lineNumber) {
        return lines.get(lineNumber);
    }

    public static CsvFile fromLines(Stream<String> lines) {
        return null;
    }

    public static CsvFile fromFile(Path dataFile) {
        return null;
    }
}
