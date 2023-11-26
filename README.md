# Modular Validators

A lightweight validation framework for the JVM, allowing for internal and external validation logic to be combined in a transparent and reusable 
way.

<!-- TOC -->
* [Modular Validators](#modular-validators)
  * [Value statement](#value-statement)
    * [Problem](#problem)
    * [Proposed solution](#proposed-solution)
  * [Contributing](#contributing)
<!-- TOC -->

## Value statement

### Problem
The coding of validation logic is generally tedious. While many simple validation frameworks exist to date, most of them suffer from the same 
design issues:

* Overly simplistic: no option to add business logic validation to the validator objects
* Dependency heavy: too many additional libraries being pulled in to the target codebase
* Hard to reuse validation rules: most existing frameworks tie a validator to a single object, making it hard to create compound validation rules
* Exit on first fail: once the validation is done, the system throws an immediate exception of the first failure. Requiring developers to run 
  the same validation multiple times, fixing a single error on every pass.

### Proposed solution

Our modular validator solution aims to remedy this, so give developers have the option to chose their own style of validation.
The aim is to simplify externally driven validation by providing extension points upon which developers can hook in their own api calls.
On top of this, the validator objects will behave as `monads`. This means they will be usable in both the `valid` and `invalid` state.
This allows to chain logic on top of the validator objects if so required, rather than being forced to write exceptional code paths.

The aim is to make validation of object state a first level concern when designing applicative flows, rather than a side-thought that is often 
forgotten.


## Contributing

Thank you for your interest in this repository. If you would like to help out, consider the
following:

There are various ways to contribute to this repository. You can help us out by:

* Using the library and providing feedback
* Come up with ideas or patterns to be included
* Log issues in the github issue tracker
* Send a Pull-Request

You will need to use a compatible code style. The IntelliJ stylesheet
is [included in the ./docs/utils](./docs/utils/SDDStyle.xml) folder. 