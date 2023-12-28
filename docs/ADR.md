# Architectural Decision Records (ADRs)

> An Architectural Decision (AD) is a justified software design choice that addresses a functional or non-functional requirement that is
> architecturally significant. An Architecturally Significant Requirement (ASR) is a requirement that has a measurable effect on a software system’s
> architecture and quality. An Architectural Decision Record (ADR) captures a single AD and its rationale; the collection of ADRs created and
> maintained in a project constitute its decision log. All these are within the topic of Architectural Knowledge Management (AKM), but ADR usage can
> be extended to design and other decisions (“any decision record”).

see: [adr.github.io](https://adr.github.io/)

## Maintainability 1: Self-documenting tests

**Problem:** Documenting the code and intended functionalities is tedious. It is often neglected and becomes outdated fast.
**Decision:** Use the SerenityBDD framework, but avoid the use of `Gherkin` and `Cucumber`. Try to minimize the impact on the existing test when adding
the self-documenting boilerplate.
**Drivers:**

* maintainability
* extensibility
* readability
* minimalism

**Goal:** Write tests that document the code and intended functionalities, use a framework to aggregate these intended 
functionalities in a human-readable format.
**Concerns:**

* Complexity of adding yet another framework simply for test aggregation
* Overhead of writing artifacts that are not directly related to the code
* Results of the additional documentation will only be read by a few interested parties, most of them are developers who can quite easily read the
  executable tests themselves and have less need for an aggregated report.