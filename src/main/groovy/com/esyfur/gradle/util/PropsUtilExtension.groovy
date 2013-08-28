package com.esyfur.gradle.util

import org.gradle.api.Plugin
import org.gradle.api.Project

import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.Path

private class PropsUtilExtension {

    public static final String NAME = 'propsUtil';

    protected Project project

    def final logMsg = 'Loaded %d properties from %s file'

    void apply(final Project project) {
        this.project = project
    }

    def load(final File propertyFile) {
        propertyFile.withReader { reader ->
            def properties = new Properties()
            properties.load(reader)

            def expander = new PropsUtilExpander()
            expander.apply(project.ext, properties)

            project.logger.info(sprintf(logMsg, properties.size(), propertyFile.toString()))
        }
    }

    def load(final Path path) {
        this.load(path.toFile())
    }

    def load(final String filePath) {
        def path = Paths.get(filePath)

        // if the given path isn't absolute, assume it is relative to the project dir
        if (!path.isAbsolute())
            path = Paths.get(project.projectDir.toString(), path.toString())

        // if the given file name doesn't have extension, assume it should be .properties
        if (!path.getFileName().toString().contains('.'))
            path = Paths.get(path.toString() + '.properties')

        this.load(path)
    }

}
