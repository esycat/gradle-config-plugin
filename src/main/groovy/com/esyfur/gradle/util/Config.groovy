package com.esyfur.gradle.util

import org.gradle.api.Plugin
import org.gradle.api.Project

class ConfigPlugin implements Plugin<Project> {

    void apply(final Project project) {
        project.extensions.create(ConfigExtension.NAME, ConfigExtension)
        project.extensions.getByType(ConfigExtension).apply(project)

        ConfigExpander.apply(project)
    }

}
