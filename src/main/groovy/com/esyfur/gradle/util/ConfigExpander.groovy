package com.esyfur.gradle.util

import java.nio.file.Path

import org.gradle.api.Project
import org.gradle.api.GradleException

private class ConfigExpander {

    private final ConfigObject config = new ConfigObject()
    private final Project project

    def ConfigExpander(final Project project) {
        this.project = project
        initConfig()
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
            project.logger.debug('Config object: ' + localConfig)
            throw ex
        }
    }

    public static ConfigObject slurp(Path configFile, Map<String, Object> bindings) {
        def slurper = new ConfigSlurper()
        slurper.setBinding(bindings)
        slurper.parse(configFile.toUri().toURL())
    }

    private String getNamespace() {
        def propName = ConfigPlugin.NAME + '.namespace'
        project.ext.has(propName) ? project.ext.get(propName).toString().trim() : ConfigPlugin.NAME
    }

    private void initConfig() {
        def namespace = getNamespace()
        project.logger.info(sprintf('Config property name: %s', namespace))

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

        project.ext.set(namespace, config)
    }

}
