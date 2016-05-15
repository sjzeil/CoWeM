package edu.odu.cs.zeil.cwm

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Sync

/**
 * A plugin for describing a course website.
 */
class CourseWebsite implements Plugin<Project> {

    void apply (Project project) {
    
        // Add a Course object as a property of the project
        project.extensions.create ("course", Course);
        
        project.allprojects {
            repositories {
                 // jcenter()
                 mavenCentral()
    
                // Use my own CS dept repo
                ivy {
                    url 'https://secweb.cs.odu.edu/~zeil/ivyrepo'
                }
            }
        }
        project.configurations {
            setup
            build
            deploy
        }
        
        project.dependencies {
            setup 'edu.odu.cs.zeil:cwm-core:latest.integration'
            build 'edu.odu.cs.zeil:cwm-utils:1.0-SNAPSHOT'
        }
        
        /*
         * Tasks for course website construction:
         *
         * setup: copies core files & prepares directories as necessary
         *
         * build: collects document metadata
         *        processes each document set
         *        processes course outline
         *
         * packages: (optional) prepare course cartridges
         *              available formats are imscc, bb, bbthin, canvas, canvasthin, epub, mobi
         *
         * deploy: (optional) copy website to local destination
         *
         * publish: (optional) copy website to remote destination (via rsync or scp)
         *
         * -----------------------------------------------------------------------------
         * Directory structure
         * 
         * course root/
         * |
         * |- course.properties
         * |
         * |- Directory/    website navigation pages
         * |--|
         * |--|- outline/   course topics/outline description (required)
         * |--|- nav/       navigation bar shared by all Directory pages
         * |--|- .../       Other navigational document sets, e.g., policies/, library/, grades/
         * |
         * |- Public/    public document sets
         * |--|
         * |--|- syllabus/ course syllabus
         * |--|- .../      other public documetn sets (typically, slides & lecture notes)
         * |
         * |- Protected/  document sets restricted to certain readers
         * |--|- Assts/     assignments
         * |--|- .../       other protected document sets (e.g., semester projects)
         * |
         * |--|- graphics/  Course-specific files that override defaults graphics used for
         * |                  navigation, callouts, backgrounds, etc.  
         * |--|- styles/  Course-specific overrides for .css and .js defaults
         * |
         * |- build/ All automatically constructed content goes here. This entire directory
         * |--|      may be deleted without loss of anything but time.   
         * |--|- cwm/   Copies of CWM core files
         * |--|--|
         * |--|--|- graphics/  Default graphics for navigation, callouts, backgrounds, etc.
         * |--|--|
         * |--|--|- styles/    Default css & js files
         * |--|--|
         * |--|--|- templates/ XSL and other templates used to process web content
         * |--|         
         * |--|- website/  generated website
         * |--|--|
         * |--|--|- index.html
         * |--|--|- Directory/  website navigation pages
         * |--|--|- Public/     public document sets
         * |--|--|- Protected/  document sets restricted to certain readers
         * |--|--|- graphics/    
         * |--|--|- styles/
         * |--|         
         * |--|- cartridges/  generated cartridges
         */

       project.task(type: Copy,  'setup_cwm') {
            dependsOn project.configurations.setup
            into project.file('build/cwm')     
            from ({ zipTree(configurations.setup.singleFile) }) {  
                include 'edu/odu/cs/cwm/core/templates/**'
                include 'edu/odu/cs/cwm/core/graphics/**'
                include 'edu/odu/cs/cwm/core/styles/**'
            }
        }

        project.setup_cwm << {
           ant.move (file: "${projectDir}/build/cwm/edu/odu/cs/cwm/core/templates", 
                     tofile: "${projectDir}/build/cwm/templates") 
        }


        project.task (type: Copy, dependsOn: 'setup_cwm',
                      'setup_copy_website_defaults') {
            from 'build/cwm/edu/odu/cs/cwm/core'
            into 'build/website'
            include 'graphics/**'
            include 'styles/**'
        }  

        project.task (dependsOn: 'setup_copy_website_defaults', 'setup_copy_website_overrides') << {
            copy  {
                from 'graphics'
                into 'build/website/graphics'
            }
            copy  {
                from 'styles'
                into 'build/website/styles'
            }
            if (file('index.html').exists()) {
                copy  {
                    from 'index.html'
                    into 'build/website/'
                }
            } else {
                copy  {
                    from 'cwm/templates/default-index.html'
                    into 'build/website/'
                }
            }
        }  

        project.task (dependsOn: 'setup_copy_website_overrides', 'setup_website')


        project.task ("setup") {
            description 'Prepare output and support directories'
            dependsOn project.setup_cwm, project.setup_website
        }


        project.task (dependsOn: 'setup', 'build') {
            description 'Process documents and course outline to prepare the basic course website.'
        } << {
        }


        project.task (dependsOn: 'build', 'packages')  {
            description 'prepare optional course cartridges'
        }



        project.task (type: Sync, dependsOn: 'build', 'deploy') {
            from 'build/website'
            into project.rootProject.deployDestination
            dirMode 0775
            includeEmptyDirs true   
        }

        project.task (dependsOn: 'build', 'publish') {

        }
 

        project.task('listProperties') << {
            println "All course properties:\n" + project.course.properties.collect{it}.join('\n')
        }
    }
    
}
