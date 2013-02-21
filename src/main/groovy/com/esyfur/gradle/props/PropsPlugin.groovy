package com.eriwen.gradle.js

import org.gradle.api.Plugin
import org.gradle.api.Project

greeting {
    message = 'Hi'
    greeter = 'Gradle'
}

class PropsPlugin implements Plugin<Project> {

    void apply(final Project project) {
    	// Add the 'greeting' extension object
        project.extensions.create("greeting", GreetingPluginExtension)

        // Add a task that uses the configuration
        project.task('hello') << {
        	println "${project.greeting.message} from ${project.greeting.greeter}"
        }
    }

}

class GreetingPluginExtension {
	String message
    String greeter
}
