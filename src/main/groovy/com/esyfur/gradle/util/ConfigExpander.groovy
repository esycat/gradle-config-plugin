package com.esyfur.gradle.util

import java.nio.file.Path

import org.gradle.api.Project
import org.gradle.api.GradleException

private class ConfigExpander {

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
            project.logger.error('Unable to merge config values.')
            project.logger.debug('Config object: {}', config)
            throw ex
        }
    }

    private Properties process(Properties properties) {
        def isProcessValuesEnabled = isProcessValuesEnabled()

        if (isProcessValuesEnabled) {
            properties.each {
                it.value = processValue(it.value)
            }
        }
    }

    private String processValue(String value) {
        value.trim()
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
        project.logger.info('Config property name: {}', namespace)

        if (project.ext.has(namespace)) {
            def config = project.ext.get(namespace)

            if (config instanceof ConfigObject) {
                project.logger.info('Given project already has initialized ConfigObject.')
                config
            }
            else {
                def errMsg = 'Given project has property {} of type {}, but {} expected.'

                project.logger.info(errMsg, namespace, config.getClass(), ConfigObject.getSimpleName())
                project.logger.debug('Config object: {}', config)

                throw new GradleException(sprintf(errMsg, namespace, config.getClass(), ConfigObject.getSimpleName()))
            }
        }
        else {
            project.logger.info('Initializing new ConfigObject for the project…')

            def config = new ConfigObject()
            project.ext.set(namespace, config)
            config
        }
    }

    public static ConfigObject load(Path configFile, Map<String, Object> bindings) {
        def slurper = new ConfigSlurper()
        slurper.setBinding(bindings)
        slurper.parse(configFile.toUri().toURL())
    }

}
