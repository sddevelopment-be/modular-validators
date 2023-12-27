# How to Contribute

## How can I help?

Hello there! Kind of you to ask. There are a few ways you can help out:

* **Ideation and Bug hunting**: We're always looking for new ideas and ways to improve the project. If you found an issue or would like to request a
  change, please open an issue in the [github issue tracker](https://github.com/sddevelopment-be/modular-validators/issues) and let us know!
  Alternatively, you can also join our [github discussions board](https://github.com/sddevelopment-be/modular-validators/discussions) and let us
  know there.
* **Code**: If you are a developer and would like to contribute code, please read the [Code Contribution Guidelines](#code-contribution-guidelines)
  below.
* **Review**: Even if you are not a Java developer, or do not feel like writing code, You can contribute by reviewing the code written by others.
  Please read the [Code Contribution Guidelines](#code-contribution-guidelines) below.
* **Usage**: If you are using this project in your own project, please let us know! We'd love to hear about it and see how we can improve the
  project to better suit your needs.

## Getting Started

You will need to create a [github account](https://github.com/signup) in order to contribute to the repository.

### Technical Prerequisites

1. You will need to have a recent version (>= 21) of [git](https://git-scm.com/) installed. On windows, you can use
   the [Git for windows](https://gitforwindows.org/) tool, or enable
   the [Linux Subsystem for Windows](https://learn.microsoft.com/en-us/windows/wsl/install) and install git from there.
2. You will need to have a recent version of the Java Development Kit (JDK) installed. You can download the latest version
   from [AdoptOpenJDK](https://adoptium.net/).  
   Various versions of the JDK exist. If you are on a unix system, we recommend to use the [sdkman](https://sdkman.io/) tool to install and manage
   your JDK versions.
3. You will need to have a recent version of [Apache Maven](https://maven.apache.org/) installed.

### Fork the repository

The steps below assume you have SSH access to your github account enabled.
If this is not the case, please read [the official github documentation](https://docs.github.com/en/authentication/connecting-to-github-with-ssh).

1. Go to the [github repository](https://github.com/sddevelopment-be/modular-validators) and click on the `Fork` button in the top right corner.
   This will create a copy of the repository in your own github account.
2. Clone the repository to your local machine. You can do this by running the following command in a terminal:
```bash
 git clone <YOUR_REPOSIORY_LINK>
```
3. Change directory to the cloned repository:
```bash
 cd modular-validators
```
4. Add the original repository as a remote:
```bash
 git remote add upstream git@github.com:sddevelopment-be/modular-validators.git
```
5. Make your changes, and push them to your forked repository. If you are satisfied with your changes, you can launch a Pull Request (PR) to the
   original repository.

You can verify your installation by running the following command in a terminal shell:
```bash
  mvn --version
```

The output of this command should look like this:
```bash
    Maven home: /usr/share/maven
    Java version: 21.0.1, vendor: Eclipse Adoptium, runtime: /home/stijnd/.sdkman/candidates/java/21.0.1-tem
    Default locale: en_US, platform encoding: UTF-8
    OS name: "linux", version: "5.15.0-91-generic", arch: "amd64", family: "unix"
``` 

## Code Contribution Guidelines

### Recommended Reading

It is advisable to have a basic understanding of tidy coding practices. Most notable, an understanding of the SOLID principles is advisable.
You can find a good introduction to the SOLID principles [here](https://www.baeldung.com/solid-principles). For a more in-depth read, take a look at the excellent book "[Test Driven Development with Java](https://www.packtpub.com/product/test-driven-development-with-java/9781803236230)" by Allan Mellor.  

As this project is a library to assist developers with implementing validations in an application, it is also a good idea to have some prior experience with writing validation code.
We are trying to be compatible with the essay "Better validation rules with Validation Results" written by [Sven Gorts, aka. @gorowitch](https://github.com/gorowitch). The essay can be found on his LinkedIn profile [here](https://www.linkedin.com/feed/update/urn:li:activity:7139998913746329600/).

### Code Style

The code stylesheet (IntelliJ and Eclipse XML format) can be found in the `docs/utils` directory of the repository. 
Please use this stylesheet when contributing code. If you are using IntelliJ IDE, refer to [this guide](https://www.jetbrains.com/help/idea/configuring-code-style.html) on how to import the stylesheet.
