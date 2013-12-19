package com.esyfur.gradle.util

import org.gradle.api.Plugin
import org.gradle.api.Project

class PropsUtilPlugin implements Plugin<Project> {

    void apply(final Project project) {
        project.extensions.create(PropsUtilExtension.NAME, PropsUtilExtension)
        project.extensions.getByType(PropsUtilExtension).apply(project)

        new PropsUtilExpander(project)
    }

}
