package com.esyfur.gradle.util

import java.nio.file.Path

import org.gradle.api.Project
import org.gradle.api.GradleException

import org.slf4j.Logger
import org.slf4j.LoggerFactory

private class ConfigExpander {

    private final Logger logger = LoggerFactory.getLogger(ConfigExpander.class)

    private final ConfigObject config
    private final Project project

    def ConfigExpander(final Project project) {
        this.project = project
        this.config = initConfig()
    }

    /**
     * Creates a new ConfigExpander for the given Project
     *
     * @param project
     * @return ConfigExpander
     */
    public static ConfigExpander apply(final Project project) {
        // project's properties must be copied prior to the config object initialization
        def properties = project.ext.properties

        def expander = new ConfigExpander(project)
        expander.apply(properties)

        expander
    }

    void apply(Map<String, Object> properties) {
        apply(new Properties(properties))
    }

    void apply(Properties properties) {
        process(properties)

        def slurper = new ConfigSlurper()
        slurper.setBinding(this.config.toProperties())
        def config = slurper.parse(properties)

        apply(config)
    }

    void apply(ConfigObject config) {
        try {
            def properties = config.clone().merge(this.config)
            this.config.putAll(properties)
        }
        catch (ex) {
            logger.error('Unable to merge config values.')
            logger.debug('Config object: {}', config)
            throw ex
        }
    }

    private Properties process(Properties properties) {
        def isProcessValuesEnabled = isProcessValuesEnabled()

        if (isProcessValuesEnabled) {
            logger.debug('Value processing is enabled.')
            properties.each {
                it.value = processValue(it.value)
            }
        }
    }

    private String processValue(String value) {
        value.trim()
    }

    private processValue(value) {
        value
    }

    private Boolean isProcessValuesEnabled() {
        def propName = ConfigPlugin.getPropName('processValues')
        project.ext.has(propName) ? project.ext.get(propName).toBoolean() : true;
    }

    private String getNamespace() {
        def propName = ConfigPlugin.getPropName('namespace')
        project.ext.has(propName) ? project.ext.get(propName).toString().trim() : ConfigPlugin.NAME
    }

    private ConfigObject initConfig() {
        def namespace = getNamespace()
        logger.info('Config property namespace: {}', namespace)

        if (project.ext.has(namespace)) {
            def config = project.ext.get(namespace)

            if (config instanceof ConfigObject) {
                logger.warn('Given project already has initialized ConfigObject.')
                config
            }
            else {
                def errMsg = 'Given project has property {} of type {}, but {} expected.'
                errMsg = sprintf(errMsg, namespace, config.getClass(), ConfigObject.getSimpleName())

                logger.error(errMsg)
                logger.debug('Config object: {}', config)

                throw new GradleException(errMsg)
            }
        }
        else {
            logger.info('Initializing new ConfigObject for the project…')

            def config = new ConfigObject()
            project.ext.set(namespace, config)
            config
        }
    }

}
