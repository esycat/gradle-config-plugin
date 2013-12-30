# Gradle Config Plugin
(aka Gradle Property Utilities Plugin)

Gradle plugin to expand dotted properties into nested objects.

In Java applications it is a common practice to use property names in dotted notation, e.g. `aws.s3.bucket`.
It also may be desirable to work with the values as nested structures.

Alas, dotted names are not auto-expanded by Groovy. Thus, properties set on the command line or in `gradle.properties`
file can be accessed in a build script only via plain string keys, e.g. `project.ext['aws.s3.bucket']`.

This tiny plugin for Gradle aspires to do the trick.

JDK 1.7 is required.

The latest stable version is `0.4.5`.


## Quick Start

```groovy
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath group: 'com.esyfur', name: 'gradle-config-plugin', version: '0.4.+'
    }
}

apply plugin: 'config'
```


## Usage

```bash
gradle test -Pbuild.name=myTestBuild -Pbuild.version=1.23
```

`build.name` and `build.version` values will now be available in `build.gradle` as nested objects:

```groovy
task test << {
    println 'Build Name:    ' + config.build.name
    println 'Build Version: ' + config.build.version
}
```

It is also possible to leverage the safe navigation operator:

```groovy
assert config.aws.s3?.bucket ?: 'com.esyfur.gradle' == 'com.esyfur.gradle'
```


## Configuration

To avoid name collisions, the plugin creates `config` property and uses it as the root scope for all processed values.

It is possible to change the name of the property by defining `config.namespace` value in `gradle.properties`:
```
config.namespace = cfg
```


## Loading .properties files

Gradle automatically reads settings from `gradle.properties` files in project build and user home directories.
But no concise ways provided to load arbitrary Properties files.

This plugin adds a handy `load()` helper for that:
```groovy
cfgutil.load('commons')
```

The method accepts `java.nio.file.Path`, `java.io.File` or a `String`.
If the given path isn't absolute, it is treated as relative to `projectDir`.
If the file name does not have an extension, `.properties` is assumed.


## Acknowledgment

The implementation is trivial and was initially borrowed from
[@OverZealous](https://github.com/OverZealous)'s
[answer on StackOverflow](http://stackoverflow.com/a/7261196/115132),
but in v0.3 it was replaced with
[ConfigObject](http://groovy.codehaus.org/gapi/groovy/util/ConfigObject.html)
and [ConfigSlurper](http://groovy.codehaus.org/gapi/groovy/util/ConfigSlurper.html).
