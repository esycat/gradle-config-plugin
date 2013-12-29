package com.esyfur.gradle.util

import java.nio.file.Path

import org.gradle.api.Project
import org.gradle.api.GradleException

import org.gradle.api.plugins.ExtraPropertiesExtension as PropExt

private class ConfigExpander {

    private final Project project
    private final String propName

    // def Character separator = '.'

    def ConfigExpander(final Project project) {
        this.project = project

        // Set config property name: either provided by the user or default
        this.propName = project.ext.has(ConfigExtension.NAME + '.propName') ?
                        project.ext.get(ConfigExtension.NAME + '.propName').toString().trim() : ConfigPlugin.NAME

        project.logger.info(sprintf('Config property name: %s', propName))

        // Initializing config object
        checkConfig()
        setConfig(new ConfigObject())
    }

    /**
     * Creates a new ConfigExpander for the given Project
     *
     * @param project
     * @return ConfigExpander
     */
    public static ConfigExpander apply(final Project project) {
        def expander = new ConfigExpander(project)
        expander.apply(project.ext.properties)
        expander
    }

    void apply(Map<String, Object> properties) {
        apply(new Properties(properties))
    }

    void apply(Properties properties) {
        def slurper = new ConfigSlurper()
        slurper.setBinding(getConfig().toProperties())
        def config = slurper.parse(properties)

        apply(config)
    }

    void apply(ConfigObject config) {
        def localConfig = config.clone()

        try {
            localConfig.merge(getConfig())
        }
        catch (ex) {
            project.logger.error('Unable to merge config values.')
            project.logger.debug('Config object: ' + localConfig)
            throw ex
        }

        setConfig(localConfig)
    }

    /*
    void apply(ConfigObject config) {
        def init = new ConfigObject()
        init.putAll(project.ext.properties)

        def target = config.clone()

        try {
            target.merge(init)
        }
        catch (ex) {
            project.logger.error('Unable to merge config values.')
            project.logger.debug('Config object: ' + config)
            throw ex
        }

        merge(project, target)
    }
    */

    /*
    protected static void merge(Project project, ConfigObject config) {
        config.each { String key, val ->
            // if the project has a property of the same name,
            // but that is not an additional property, add a suffix to the name
            if (project && project.hasProperty(key) && !project.ext.has(key)) key += 'Prop'

            project.ext.set(key, val)
        }
    }
    */

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

    public ConfigObject getConfig() {
        project.ext.get(propName)
    }

    private ConfigExpander setConfig(ConfigObject config) {
        project.ext.set(propName, config)
        this
    }

    private void checkConfig() {
        if (project.ext.has(propName)) {
            def config = project.ext.get(propName)

            if (config instanceof ConfigObject) {
                def errMsg = 'Config object has been already initialized for this project.'
                throw new GradleException(errMsg)
            }
            else {
                def errMsg = 'The given project already has property %s of type %s, but %s expected.'
                throw new GradleException(sprintf(errMsg, propName, config.getClass(), ConfigObject.getSimpleName()))
            }
        }
    }

}
