# Gradle Expand Properties Plugin

Gradle plugin to expand dotted properties into nested maps.

If you set a property to a build script on the command line, you may want to use
the dotted notation (e.g. `aws.s3.bucket`) to access the values as a hierarchy
of keys.

Groovy does not auto-expand dotted property names, and consequently it is not
supported in Gradle. In a build script one should use `project['aws.s3.bucket']`
syntax instead.

This tiny plugin for Gradle aspires to do the trick.


## Quick Start

```groovy
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath group: 'com.esyfur', name: 'gradle-expand-props', version: '0.1.23'
    }
}

apply plugin: 'expandProps'
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

## Acknowledgment

The implementation is trivial and was taken from
[@OverZealous](https://github.com/OverZealous)'s
[answer on StackOverflow](http://stackoverflow.com/a/7261196/115132).
