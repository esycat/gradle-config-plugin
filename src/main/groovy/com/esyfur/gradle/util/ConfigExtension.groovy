package com.esyfur.gradle.util

import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.Path
import java.nio.charset.Charset

import org.gradle.api.Project

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ConfigExtension {

    public final static String NAME = 'cfgutil';

    private final Logger logger = LoggerFactory.getLogger(ConfigExtension.class)

    private final Project project

    def ConfigExtension(final Project project) {
        this.project = project
    }

    def load(Path configFile) {
        if (!configFile.isAbsolute()) {
            logger.debug("Given path isn't absolute, assume it's relative to the project's root dir.")
            configFile = project.projectDir.toPath().resolve(configFile)
        }

        if (!configFile.getFileName().toString().contains('.')) {
            logger.debug("Given file name doesn't have extension, assume it should be .properties.")
            configFile = Paths.get(configFile.toString() + '.properties')
        }

        logger.info('Loading {} property file.', configFile)

        // Acquiring a reader and load the .properties file.
        def reader = Files.newBufferedReader(configFile, Charset.forName("UTF-8"))
        def config = new Properties()

        try {
            config.load(reader)
        }
        catch (ex) {
            logger.error('Unable to load property file.')
            throw ex
        }

        // Applying the config to the project.
        try {
            def expander = new ConfigExpander(project)
            expander.apply(config)
        }
        catch (ex) {
            logger.error('Unable to apply property values to the project.')
            throw ex
        }

        logger.info('Loaded {} properties from {} file', config.size(), configFile)
        logger.debug('Loaded properties: {}', config)
    }

    def load(final File configFile) {
        load(configFile.toPath())
    }

    def load(final String configFile) {
        load(Paths.get(configFile))
    }

}
