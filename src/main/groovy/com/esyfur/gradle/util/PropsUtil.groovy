package com.esyfur.gradle.util

import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.Path

import org.gradle.api.Plugin
import org.gradle.api.Project

import org.gradle.api.plugins.ExtraPropertiesExtension

class PropsUtilPlugin implements Plugin<Project> {

    void apply(final Project project) {
        project.extensions.create('propsUtil', PropsUtilExtension)
        project.extensions.getByType(PropsUtilExtension).apply(project)

        def expander = new PropsUtilExpander()
        expander.apply(project.ext.properties, project.ext)
    }

}

private class PropsUtilExpander {

    def Character separator = '.'

    def apply(Map properties, ExtraPropertiesExtension ext) {
        explode(ext.properties, properties).each {
            String key, val -> ext.set(key, val)
        }
    }

    private Map explode(Map target, Map properties) {
        properties.inject(target) {
            Map result, String key, val -> merge(result, unfold(key, val))
        }
    }

    private Map unfold(String key, val) {
        key.tokenize(separator).reverse().inject(val) {
            last, String subkey -> [(subkey):last]
        }
    }

    public static Map merge(Map first, Map second) {
        second.each { String key, val ->
            if (first[key] && (val instanceof Map)) merge(first[key], val)
            else first[key] = val
        }
        first
    }

}

private class PropsUtilExtension {

    protected Project project

    def final logMsg = 'Loaded %d properties from %s file'

    void apply(final Project project) {
        this.project = project
    }

    def load(File propertyFile) {
        propertyFile.withReader { reader ->
            def properties = new Properties()
            properties.load(reader)

            def expander = new PropsUtilExpander()
            expander.apply(properties, project.ext)

            project.logger.info(sprintf(logMsg, properties.size(), propertyFile.toString()))
        }
    }

    def load(Path path) {
        this.load(path.toFile())
    }

    def load(String filePath) {
        def path = Paths.get(filePath)

        // if the given path isn't absolute, assume it is relative to the project dir
        if (!path.isAbsolute())
            path = Paths.get(project.projectDir.toString(), path.toString())

        // if the given file name doesn't have extension, assume it should be .properties
        if (!path.getFileName().toString().contains('.'))
            path = Paths.get(path.toString() + '.properties')

        this.load(path)
    }

}
