package edu.odu.cs.cwm


import org.gradle.api.Plugin
import org.gradle.api.Project


import edu.odu.cs.cwm.DocumentSet

/**
 * A plugin for describing a group of document sets that can
 * share commons defaults & policies. 
 * 
 */
class Group implements Plugin<Project> {

	void apply (Project project) {

		project.subprojects {
            // Add a DocumentSet object as a property of the project
            if (!it.hasProperty('documents')) {
                println ("Adding documents config to " + it.name)
                it.extensions.create ('documents', DocumentSet, it)
            }
    
        }
	}

}
