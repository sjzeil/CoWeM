package edu.odu.cs.cowem


import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy


import edu.odu.cs.cowem.DocumentSet

/**
 * A plugin for describing a group of document sets that can
 * share commons defaults & policies. 
 * 
 */
class Group implements Plugin<Project> {

    Group() {
    }
    
	void apply (Project project) {
        
		project.subprojects {
            // Add a DocumentSet object as a property of the project
            if (!it.hasProperty('documents')) {
                //println ("Adding documents config to " + it.name)
                it.extensions.create ('documents', DocumentSet, it)
            }
        }
        
        project.task ("setup", type: Copy) {
            from project.projectDir
            into new File(project.rootDir, 'build/website/' + project.name)
            include ('*.*')
            exclude ('build.gradle')
        } << {
            File groupIndex = new File(project.rootDir, 'build/website/' 
                + project.name + '/' + 'index.html')
            if (!groupIndex.exists()) {
                groupIndex.withWriter('UTF-8') {
                    it.writeLine('<html><body>' + project.name + '</body></html>')
                }
            }
        }
        
        project.task ("build", dependsOn: 'setup')
        
        project.rootProject.tasks['build'].dependsOn(project.setup)

	}

}
