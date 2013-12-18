package com.esyfur.gradle.util

import org.gradle.api.Project

import java.nio.file.Paths
import java.nio.file.Path

private class PropsUtilExtension {

    public static final String NAME = 'propsUtil';

    protected Project project

    def final logMsg = 'Loaded %d properties from %s file'

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

        def expander = new PropsUtilExpander()
        def config = expander.apply(project.ext, configFile)
        project.logger.info(sprintf(logMsg, config.size(), configFile.toString()))
    }

    def load(final File configFile) {
        load(configFile.toPath())
    }

    def load(final String configFile) {
        load(Paths.get(configFile))
    }

}
