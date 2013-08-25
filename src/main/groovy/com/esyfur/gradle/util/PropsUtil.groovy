package com.esyfur.gradle.util

import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.Path

import org.gradle.api.Plugin
import org.gradle.api.Project

import org.gradle.api.plugins.ExtraPropertiesExtension as PropExt

class PropsUtilPlugin implements Plugin<Project> {

    void apply(final Project project) {
        project.extensions.create('propsUtil', PropsUtilExtension)
        project.extensions.getByType(PropsUtilExtension).apply(project)

        new PropsUtilExpander().apply(project)
    }

}

private class PropsUtilExpander {

    def Character separator = '.'

    def apply(Project project) {
        apply(project.ext)
    }

    def apply(PropExt ext) {
        apply(ext, ext.properties)
    }

    def apply(PropExt ext, Map<String, Object> map) {
        apply(ext, new Properties(map))
    }

    def apply(PropExt ext, Properties properties) {
        def slurper = new ConfigSlurper()
        slurper.setBinding(ext.properties)
        def config = slurper.parse(properties)

        apply(ext, config)
    }

    def apply(PropExt ext, ConfigObject config) {
        def target = new ConfigObject()
        target.putAll(ext.properties)
        target.merge(config)

        target.each {
            String key, val -> ext.set(key, val)
        }
    }

    /*
    def apply(Map<String, Object> properties, PropExt ext) {
        explode(ext.properties, properties).each {
            String key, val -> ext.set(key, val)
        }
    }
    */

    private Map explode(Map<String, Object> target, Map<String, Object> properties) {
        properties.inject(target) {
            Map result, String key, val -> merge(result, unfold(key, val))
        }
    }

    private Map unfold(String key, val) {
        key.tokenize(separator).reverse().inject(val) {
            last, String subkey -> [(subkey):last]
        }
    }

    public static Map merge(Map<String, Object> dest, Map<String, Object> src) {
        src.each { String key, val ->
            if (dest[key] && (val instanceof Map)) merge(dest[key], val)
            else dest[key] = val
        }
        dest
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
            expander.apply(project.ext, properties)

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
