# Modular Validators

A lightweight validation framework for the JVM, allowing for internal and external validation logic to be combined in a transparent and reusable
way.

| [![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=sddevelopment-be_modular-validators&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=sddevelopment-be_modular-validators) | [![All Contributors](https://img.shields.io/github/all-contributors/sddevelopment-be/modular-validators?color=ee8449&style=flat-square)](#contributors) | [JavaDoc](https://sddevelopment-be.github.io/modular-validators/apidocs/) |

---

<!-- TOC -->
* [Goal](#goal)
* [Value statement](#value-statement)
  * [Problem](#problem)
  * [Proposed solution](#proposed-solution)
* [Contributing](#contributing)
<!-- TOC -->

---

# User Guide

This section will provide a brief overview of the different parts of the framework, and how they can be used to create a modular validation solution.
The library is divided into two modules for now:

* `modular-validators-core`: This module contains the core validation logic, and the basic building blocks for creating custom validators.
* `modular-validators-dsl`: This module contains a pre-built validator for flat data files, and a Domain Specific Language to allow for the 
  customization of validation specifications. 

## Reusable Validators

The `modular-validators-core` module is the most important part of the library, as it contains the building blocks for creating custom validators.
You can find the classes and interfaces in the supplied javadocs, or by browsing the source code of the module.
We advise you to start by creating a custom validator, and get a feel for the code that you are able to write with this library.

### Creating a specification 

The main entrypoint of the `core` module is the `ModularRuleset` class. This class is a container for multiple `Constraint` objects, and
provides you with the ability to assess multiple constraints against a single object. The results of the validation are stored in a `Rationale`
object, which can be queried for the results of the validation. By default, all rules are assessed and included in the rationale.

As an example, say we have a simple record that represent a email contact address, like so:
```java
private record EmailContact(
        UUID userIdentifier, 
        String email, 
        String name, 
        String lastName
) {}
```

We want to ensure that the email address is valid, and that the name and last name are not empty. 
We can create a ruleset like so:

```java
ModularRuleset<EmailContact.class> emailChecker = aValid(EmailContact.class)
  .must(Objects::nonNull, "not be null")
  .must(haveNonNullField(EmailContact::email), "have a non-null email")
  .iHaveSpoken();
```

Note that the `must` and `may` methods can be supplied with a `Predicate` of your chosen type, and a message that represents the specification.
This enables you to create custom specification rules, that can even call external services if so desired.
For instance, you could create a specification rule that checks if the email address is valid by sending a test email to the address.
```
.must(mailContact -> emailService.sendTestMailTo(mailContact.email), "be able to receive a test email")
```

### Applying the specification

Once you have created a specification, you can apply it to an object like so:

```java
emailChecker.constrain(
        new EmailContact(
                randomUUID(), 
                "invalid email", 
                "Bob", 
                "The Builder"
        )
    );
```

This with return a `Constrained` object, which can be queried for the results of the specification validation, or used in a chain of further 
processing (similar to Java's `Optional`). The [JavaDoc](https://sddevelopment-be.github.io/modular-validators/apidocs/be/sddevelopment/validation/core/Constrained.html) of the `Constrained` class contains more information on how to use this object.



# Project and Repository Information

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

* Dejongh, S (2024) Fail Fast, Formulate Feedback!. [patterns.sddevelopment.be](https://patterns.sddevelopment.be/practices/336b0448-e78b-4428-83a5-d4d473afda63)
* Gorts, S (2023) Better validation rules with Validation Results. self-published on LinkedIn. [link](https://patterns.sddevelopment.be/attachments/GORTS_Better-Validation-Rules.pdf).

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
<table>
  <tbody>
    <tr>
      <td align="center" valign="top" width="14.28%"><a href="http://noahweasley.github.io"><img src="https://avatars.githubusercontent.com/u/43308031?v=4?s=100" width="100px;" alt="Ebenmelu Ifechukwu"/><br /><sub><b>Ebenmelu Ifechukwu</b></sub></a><br /><a href="#review-noahweasley" title="Reviewed Pull Requests">👀</a></td>
    </tr>
  </tbody>
</table>

<!-- markdownlint-restore -->
<!-- prettier-ignore-end -->

<!-- ALL-CONTRIBUTORS-LIST:END -->