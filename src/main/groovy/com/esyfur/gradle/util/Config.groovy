package com.esyfur.gradle.util

import org.gradle.api.Plugin
import org.gradle.api.Project

class ConfigPlugin implements Plugin<Project> {

    public final static String NAME = 'config';

    void apply(final Project project) {
        project.extensions.create(ConfigExtension.NAME, ConfigExtension, project)
        // project.extensions.getByType(ConfigExtension).apply(project)

        ConfigExpander.apply(project)
    }

    static String getPropName(String name) {
        ConfigPlugin.NAME + '.' + name
    }

}
