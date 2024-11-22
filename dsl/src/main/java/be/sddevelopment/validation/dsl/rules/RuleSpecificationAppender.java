package be.sddevelopment.validation.dsl.rules;

import be.sddevelopment.validation.core.ModularRuleset;

import java.util.function.Function;

@FunctionalInterface
public interface RuleSpecificationAppender<T> extends Function<ModularRuleset.ModularValidatorBuilder<T>, ModularRuleset.ModularValidatorBuilder<T>> {
}
