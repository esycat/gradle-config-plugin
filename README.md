# Gradle Expand Properties Plugin

Gradle plugin to expand dotted properties into nested maps.

In Java applications it is a common practice to use property names in dotted notation, e.g. `aws.s3.bucket`.
It also may be desirable to work with the values as nested structures.

Alas, dotted names are not auto-expanded by Groovy. Thus, properties set on the command line or in `gradle.properties`
file can be accessed in a build script only via plain string keys, e.g. `project.ext['aws.s3.bucket']`.

This tiny plugin for Gradle aspires to do the trick.


## Quick Start

```groovy
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath group: 'com.esyfur', name: 'gradle-expand-props', version: '0.2.3'
    }
}

apply plugin: 'propsUtil'
```


## Usage

```bash
gradle test -Pbuild.name=myTestBuild -Pbuild.version=1.23
```

`build.name` and `build.version` values will now be available in `build.gradle`
as nested objects:

```groovy
task test << {
    println 'Build Name: '    + build.name
    println 'Build Version: ' + build.version
}
```


## Loading .properties files

Gradle automatically reads settings from `gradle.properties` files in project build and user home directories.
But no concise way exists to load arbitrary properties files.

This plugin adds a handy `load()` helper for that:
```groovy
propsUtil.load('commons')
```

The method accepts either `java.nio.file.Path`, `java.io.File` or a `String`.
If the given path isn't absolute, it is treated as relative to `projectDir`.
If the file name does not have extension, `.properties` is assumed.


## Acknowledgment

The implementation is trivial and was taken from
[@OverZealous](https://github.com/OverZealous)'s
[answer on StackOverflow](http://stackoverflow.com/a/7261196/115132).
