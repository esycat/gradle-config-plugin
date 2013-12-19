package com.esyfur.gradle.util

import org.gradle.api.Project

import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.Path
import java.nio.charset.Charset

private class PropsUtilExtension {

    public final static String NAME = 'propsUtil';

    private final static logMsg = 'Loaded %d properties from %s file'

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

        // Acquiring a reader and load the .properties file.
        def reader = Files.newBufferedReader(configFile, Charset.fromName("UTF-8"))
        def config = new Properties()
        config.load(reader)

        // Applying the config to the project.
        def expander = new PropsUtilExpander(project)
        expander.apply(config)

        project.logger.info(sprintf(logMsg, config.size(), configFile.toString()))
        project.logger.debug('Loaded properties: ' + config)
    }

    def load(final File configFile) {
        load(configFile.toPath())
    }

    def load(final String configFile) {
        load(Paths.get(configFile))
    }

}
