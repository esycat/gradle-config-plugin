package com.esyfur.gradle.util

import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.Path
import java.nio.charset.Charset

import org.gradle.api.Project
import org.gradle.api.GradleException

private class ConfigExtension {

    public final static String NAME = 'cfgutil';

    protected Project project

    void apply(final Project project) {
        this.project = project
    }

    def load(Path configFile) {
        if (!configFile.isAbsolute()) {
            project.logger.debug("The given path isn't absolute, assume it's relative to the project's root dir.")
            configFile = project.projectDir.toPath().resolve(configFile)
        }

        if (!configFile.getFileName().toString().contains('.')) {
            project.logger.debug("The given file name doesn't have extension, assume it should be .properties.")
            configFile = Paths.get(configFile.toString() + '.properties')
        }

        project.logger.info(sprintf('Loading %s property file.', configFile.toString()))

        // Acquiring a reader and load the .properties file.
        def reader = Files.newBufferedReader(configFile, Charset.forName("UTF-8"))
        def config = new Properties()

        try {
            config.load(reader)
        }
        catch (ex) {
            project.logger.error('Unable to load property file.')
            throw ex
        }

        // Applying the config to the project.
        try {
            def expander = new ConfigExpander(project)
            expander.apply(config)
        }
        catch (ex) {
            project.logger.error('Unable to apply property values to the project.')
            throw ex
        }

        project.logger.info(sprintf('Loaded %d properties from %s file', config.size(), configFile.toString()))
        project.logger.debug('Loaded properties: ' + config)
    }

    def load(final File configFile) {
        load(configFile.toPath())
    }

    def load(final String configFile) {
        load(Paths.get(configFile))
    }

}
