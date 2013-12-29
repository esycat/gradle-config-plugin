package com.esyfur.gradle.util

import java.nio.file.Path

import org.gradle.api.Project
import org.gradle.api.GradleException

private class ConfigExpander {

    private final Project project
    private final String namespace

    def ConfigExpander(final Project project) {
        this.project = project

        // Set config property name: either provided by the user or default
        this.namespace = getNamespace()
        project.logger.info(sprintf('Config property name: %s', namespace))

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

    public static ConfigObject slurp(Path configFile, Map<String, Object> bindings) {
        def slurper = new ConfigSlurper()
        slurper.setBinding(bindings)
        slurper.parse(configFile.toUri().toURL())
    }

    public ConfigObject getConfig() {
        project.ext.get(namespace)
    }

    private ConfigExpander setConfig(ConfigObject config) {
        project.ext.set(namespace, config)
        this
    }

    private void checkConfig() {
        if (project.ext.has(namespace)) {
            def config = project.ext.get(namespace)

            if (config instanceof ConfigObject) {
                def errMsg = 'Config object has been already initialized for this project.'
                throw new GradleException(errMsg)
            }
            else {
                def errMsg = 'The given project already has property %s of type %s, but %s expected.'
                throw new GradleException(sprintf(errMsg, namespace, config.getClass(), ConfigObject.getSimpleName()))
            }
        }
    }

    private String getNamespace() {
        def propName = ConfigPlugin.NAME + '.namespace'
        project.ext.has(propName) ? project.ext.get(propName).toString().trim() : ConfigPlugin.NAME
    }

}
