# Self-documenting tests

**Problem:** Documenting the code and intended functionalities is tedious. It is often neglected and becomes outdated fast.
**Decision:** Rely on self-documenting tests. Write tests that document the code and intended functionalities.
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

**Considered Alternatives:**

* Use of [Serenity BDD](https://serenity-bdd.github.io/): Removed after experimentation, as the usage was tedious and it tended to overload any PRs
  by adding in a multitude of HTML files.
* Custom reporting plugin, combining Javadoc, Markdown, and Jacoco reporting. Did not pursue further, as this would take a significant amount of
  effort.