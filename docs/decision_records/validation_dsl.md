# Generate validator code from PlantUML custom theme specification

## Context

Most organizations have business-minded individuals or non-software engineers defining the specifications of the software (internal clients, product owners, analysts, managers, end-users).

## Problem
Business validation rules are tedious to maintain, as the translation from business specification into executable validation code involves multiple hand-overs and knowledge-extraction events. In the end, there is seldom time to maintain this consistently.

Current "Behaviour Driven Development" approaches lack the direct feedback to the end users, and are rather verbose
(cfr. Cucumber, Gherkin, Finesse, JBehave, ...)

## Intent

* ’maintainability:‘ significant increase
* ‘adaptability:‘ significant increase

## Solution

* Work with file based validation inputs only, to simplify process and allow for extensibility. ( If your data can be written to a file, it can be validated. Covering most of the real-world use cases for backend BDD approaches).
* Use a minimal plantUML based specification
* Write a plantUML to executable code parser

## Reasoning

* Out of the box documentation due to PlantUML visualisation
* Out of the box syntax checks due to PlantUML
* Compatibility with most tech stacks
* Specification can be kept, even if validation framework is removed

## Considered alternatives

* **Write a custom`<insert BDD spec>`  to `ModularValidator` implementation**:  rejected due to stack/dependency impact.
* **Write a Kotlin based DSL**:  rejected due to language onboarding requirements.
* **Use an existing tool**: rejected as that would defeat the purpose of this project.

# References

* [JBehave](https://jbehave.org/?r=qal-bddtt)
* [Finesse](https://fitnesse.org/)
* [Cucumber](https://cucumber.io/?r=qal-bddtt)
* [Serenity](https://serenity-bdd.github.io/)

# Example: Potential DSL API

```plantUML

ValidatorFor(".*IDENTIFIABLE_PART_OF_FILE_NAME.*\\.csv") {
   RecordIdentifier('ROW_ID')
   FieldMatches('FIELD_NAME', 'MAY_NOT_BE_EMPTY')
   FieldExists('JOSKE_WAS_HERE')
   
   ValidationCase("description") {
     RecordExists('12345')
     RecordHasValue('12345', 'FIELD_NAME', 'expectedValue')
   }
}
```
