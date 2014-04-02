package com.esyfur.gradle.util

import org.gradle.api.Plugin
import org.gradle.api.Project

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ConfigPlugin implements Plugin<Project> {

    public final static String NAME = 'config';

    private final Logger logger = LoggerFactory.getLogger(ConfigPlugin.class)

    private Project project

    void apply(final Project project) {
        this.project = project

        logger.info('Applying plugin extension…')
        project.extensions.create(ConfigExtension.NAME, ConfigExtension, project)
        ConfigExpander.apply(project)
        logger.info('`{}` plugin has been configured.', getClass().getSimpleName())
    }

    static String getPropName(String name) {
        ConfigPlugin.NAME + '.' + name
    }

}
