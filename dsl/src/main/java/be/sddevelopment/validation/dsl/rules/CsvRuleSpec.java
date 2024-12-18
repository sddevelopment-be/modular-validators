package be.sddevelopment.validation.dsl.rules;

import be.sddevelopment.validation.dsl.CsvFile;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

public record CsvRuleSpec(
        String ruleName,
        Function<String, RuleSpecificationAppender<CsvFile>> ruleBuilder
) {

    public static final String DEFAULT_PARAMETER_SEPARATOR = ",";

    public Optional<RuleSpecificationAppender<CsvFile>> toRule(String line) {
        return accepts(line) ? ofNullable(ruleBuilder().apply(line)) : empty();
    }

    public boolean accepts(String specificationLine) {
        return specificationLine.startsWith(ruleName);
    }

    static List<String> parametersFrom(String line) {
        String[] rawParameters = line.substring(line.indexOf('(') + 1, line.indexOf(')'))
                .trim()
                .replace("'", "")
                .split(DEFAULT_PARAMETER_SEPARATOR);
        return Stream.of(rawParameters).map(String::trim).toList();
    }
}
