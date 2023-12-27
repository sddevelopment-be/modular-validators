# Modular Validators

A lightweight validation framework for the JVM, allowing for internal and external validation logic to be combined in a transparent and reusable
way.

| [![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=sddevelopment-be_modular-validators&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=sddevelopment-be_modular-validators) | [![All Contributors](https://img.shields.io/github/all-contributors/sddevelopment-be/modular-validators?color=ee8449&style=flat-square)](#contributors) | [Test report](serenity/) | [JavaDoc](apidocs/) |

---

<!-- TOC -->
* [Goal](#goal)
* [Value statement](#value-statement)
  * [Problem](#problem)
  * [Proposed solution](#proposed-solution)
* [Contributing](#contributing)
<!-- TOC -->

---

## Goal

The goal of this project is to provide a lightweight rule enforcement and validation framework for Java projects, allowing for internal and external validation logic to be combined in a transparent and reusable way.

It will help avoid the need for writing complex custom validation solutions, by providing developers with pre-built rules and enforcers that can be combined in a modular way. The sections below describe the most common issues with existing (simplistic) validation frameworks, and how this project aims to adress them.

If you are interested in seeing potential use cases for this project, please refer to the `ValidationDogfoodTest` included in the `src/main/test` directory of the repository.
These tests cases are intended to provide a real-world example of how the different parts of the framework can be used.

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

Our modular validator solution aims to remedy this, so developers have the option to chose their own style of validation.
The aim is to simplify externally driven validation by providing extension points upon which developers can hook in their own api calls.
On top of this, the validator objects will behave as `monads`. This means they will be usable in both the `valid` and `invalid` state.
This allows to chain logic on top of the validator objects if so required, rather than being forced to write exceptional code paths.

The aim is to make validation of object state a first level concern when designing applicative flows, rather than a side-thought that is often 
forgotten.

## References

* Gorts, S (2023) Better validation rules with Validation Results. self-published on LinkedIn. [link](https://www.linkedin.com/feed/update/urn:li:activity:7139998913746329600/).

## Contributing

Thank you for your interest in this repository. If you would like to help out, consider the
following:

There are various ways to contribute to this repository. You can help us out by:

* Using the library and providing feedback
* Come up with ideas or patterns to be included
* Log issues in the github issue tracker
* Send a Pull-Request

If you would like to contribute code, please read the [`How to Contribute`](./CONTRIBUTING.md) guide included in the repository.

## Contributors

<!-- ALL-CONTRIBUTORS-LIST:START - Do not remove or modify this section -->
<!-- prettier-ignore-start -->
<!-- markdownlint-disable -->

<!-- markdownlint-restore -->
<!-- prettier-ignore-end -->

<!-- ALL-CONTRIBUTORS-LIST:END -->