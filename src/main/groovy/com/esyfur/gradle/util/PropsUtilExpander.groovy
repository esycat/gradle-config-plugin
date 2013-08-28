package com.esyfur.gradle.util

import org.gradle.api.Plugin
import org.gradle.api.Project

import org.gradle.api.plugins.ExtraPropertiesExtension as PropExt

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

        merge(ext, target)
    }

    protected def merge(PropExt ext, ConfigObject config) {
        config.each {
            String key, val -> ext.set(key, val)
        }
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
