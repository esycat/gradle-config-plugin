Gradle Expand Properies Plugin
==============================

Gradle plugin to expand dotted properties into nested maps.

# Quick Start

```groovy

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath group: 'com.esyfur', name: 'gradle-expand-props', version: '0.1.20'
    }
}
```

apply plugin: 'expandProps'

# Usage
```bash
gradle build -Pbuild.name=myTestBuild
```
