package com.esyfur.gradle.util

import java.nio.file.Path

import org.gradle.api.Plugin
import org.gradle.api.Project

import org.gradle.api.plugins.ExtraPropertiesExtension as PropExt

private class PropsUtilExpander {

    private Project project

    // def Character separator = '.'

    def PropsUtilExpander(final Project project) {
        this.project = project

    }

    public static apply(final Project project) {
        def expander = new PropsUtilExpander(project)
        expander.apply(project.ext.properties)
    }

    void apply(Map<String, Object> properties) {
        apply(new Properties(properties))
    }

    void apply(Properties properties) {
        def slurper = new ConfigSlurper()
        slurper.setBinding(project.ext.properties)
        def config = slurper.parse(properties)

        apply(config)
    }

    void apply(ConfigObject config) {
        def init = new ConfigObject()
        init.putAll(project.ext.properties)

        def target = config.clone()
        target.merge(init)

        merge(project, target)
    }

    protected static void merge(Project project, ConfigObject config) {
        config.each { String key, val ->
            // if the project has a property of the same name,
            // but that is not an additional property, add a suffix to the name
            if (project && project.hasProperty(key) && !project.ext.has(key)) key += 'Prop'

            project.ext.set(key, val)
        }
    }

    public static ConfigObject slurp(Path configFile, Map<String, Object> bindings) {
        def slurper = new ConfigSlurper()
        slurper.setBinding(bindings)
        slurper.parse(configFile.toUri().toURL())
    }

    /*
    def apply(Map<String, Object> properties, PropExt ext) {
        explode(ext.properties, properties).each {
            String key, val -> ext.set(key, val)
        }
    }

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
    */

}
