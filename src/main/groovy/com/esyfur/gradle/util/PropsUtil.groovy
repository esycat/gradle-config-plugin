package com.esyfur.gradle.util

import org.gradle.api.Plugin
import org.gradle.api.Project

class PropsUtilPlugin implements Plugin<Project> {

    private final Character separator = '.'

    void apply(final Project project) {
        project.extensions.create('propsUtil', PropsUtilExtension)
        expand(project.ext.properties, separator).each { key, val -> project.ext.set(key, val) }
    }

    private static Map expand(Map props, Character separator) {
        props.inject([:]) { result, key, val ->
            merge(result, key.tokenize(separator).reverse().inject(val) {
                last, subkey -> [(subkey):last]
            })
        }
    }

    private static Map merge(Map first, Map second) {
        second.each { key, val ->
            if (first[key] && (val instanceof Map)) merge(first[key], val)
            else first[key] = val
        }
        first
    }

}

private class PropsUtilExtension {
}
