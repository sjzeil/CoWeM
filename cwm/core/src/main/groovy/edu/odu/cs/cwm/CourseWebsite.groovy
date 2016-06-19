package edu.odu.cs.cwm

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.Sync
import org.gradle.api.tasks.bundling.Zip

import edu.odu.cs.cwm.documents.WebsiteProject

/**
 * A plugin for describing a course website.
 */
class CourseWebsite implements Plugin<Project> {

    void apply (Project project) {

        // Add a Course object as a property of the project
        project.extensions.create ("course", Course);
        project.extensions.create ("website", WebsiteProject, project.rootDir);

        project.remotes {
            remotehost {
                // Values will be filled in from course settings
                host = 'placeholder'
                user = 'placeHolder'
                agent = true
            }
        }

        project.allprojects {

            defaultTasks 'build'

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

        project.task('setup_cwm', type: Copy) {
            dependsOn project.configurations.setup
            into project.file('build/cwm')
            from ({ project.zipTree(project.configurations.setup.find {
                    it.name.startsWith("cwm-core") }
                ) }) {
                include 'edu/odu/cs/cwm/core/graphics/**'
                include 'edu/odu/cs/cwm/core/styles/**'
            }
        }


        project.task ('setup_copy_website_defaults',
            type: Copy, dependsOn: 'setup_cwm'
        ) {
            from 'build/cwm/edu/odu/cs/cwm/core'
            into 'build/website'
            include 'graphics/**'
            include 'styles/**'
        }

        project.task ('setup_copy_website_overrides',
            dependsOn: 'setup_copy_website_defaults') << {
            project.copy  {
                from 'graphics'
                into 'build/website/graphics'
            }
            project.copy  {
                from 'styles'
                into 'build/website/styles'
            }
            if (project.file('index.html').exists()) {
                project.copy  {
                    from 'index.html'
                    into 'build/website/'
                }
            } else {
                project.copy {
                    from 'build/website/styles/'
                    include 'homeRedirect.html'
                    into 'build/website/'
                    rename '.+', 'index.html'
                }
            }
        }

        project.task ('setup_website',
            dependsOn: 'setup_copy_website_overrides')


        project.task ("setup") {
            // description 'Prepare output and support directories'
            dependsOn project.setup_cwm, project.setup_website
        }


        project.task ('build', dependsOn: 'setup') {
            description 'Process documents and course outline to prepare the basic course website.'
            group 'Build'
        }

        project.task ('packages', dependsOn: 'build')  {
            description 'prepare optional course cartridges'
            group 'Packaging'
        }

        project.task ('zip', type: Zip, dependsOn: 'build') {
            description 'Prepare a zip file of the website.'
            group 'Packaging'
            from 'build/website'
            into '.'
            destinationDir = project.file('build/packages')
            archiveName 'website.zip'
            dirMode 0775
            fileMode 0664
            includeEmptyDirs true
        }


        project.task ('deploy', type: Sync, dependsOn: 'build') {
            description 'Copy course website to a local deployDestination directory.'
            group 'Deployment'
            from 'build/website'
            into { return project.course.deployDestination; }
            dirMode 0775
            includeEmptyDirs true
        }

        project.task ('deployBySsh', dependsOn: 'zip') {
            description 'Copy course website to a remote machine.'
            group 'Deployment'
            inputs.file 'build/packages/website.zip'
        } << {
            int k0 = project.course.sshDeployURL.indexOf('@')
            int k1 = project.course.sshDeployURL.indexOf(':')
            def hostName = project.course.sshDeployURL.substring(k0+1,k1)
            project.remotes.remotehost.host = hostName
            def userName = project.course.sshDeployURL.substring(0, k0)
            project.remotes.remotehost.user = userName
            def remotePath = project.course.sshDeployURL.substring(k1+1)
            if (project.course.sshDeployKey != null) {
                project.remotes.remotehost.identity =
                        project.file(project.course.sshDeployKey)
            }

            project.ssh.run {
                settings {
                    dryRun = false
                }
                session (project.remotes.remotehost) {
                    put from: project.file('build/packages/website.zip'),
                    into: remotePath
                    execute "unzip -u -q -o ${remotePath}/website.zip -d ${remotePath}"
                    execute "/bin/rm -f ${remotePath}/website.zip"
                }
                println "Sent to " + project.course.sshDeployURL
            }
        }



        project.task ('deployByRsync', dependsOn: 'build') {
            // Deloy by rsync requires an external installation of rsync and ssh.
            description 'Copy course website to a remote machine by rsync'
            group 'Deployment'
            inputs.dir 'build/website'
        } << {
            if (project.course.rsyncDeployURL == null) {
                project.course.rsyncDeployURL = project.course.sshDeployURL
                if (project.course.rsyncDeployKey == null) {
                    project.course.rsyncDeployKey = project.course.sshDeployKey
                }
            }

            String sshCmd = "ssh";
            if (project.course.rsyncDeployKey != null) {
                sshCmd = "-i ${project.course.rsyncDeployKey}"
            }

            project.exec {
                commandLine = [
                    'rsync',
                    '-auzv',
                    '-e',
                    sshCmd,
                    'build/website/',
                    project.course.rsyncDeployURL +
                       ((project.course.rsyncDeployURL.endsWith('/')) ?
                           "" : '/')
                    ]
            }
        }


        project.task ('publish', dependsOn: 'build') {

        }


        project.task('listProperties') << {
            println "All course properties:\n" + project.course.properties.collect{it}.join('\n')
        }
    }

}
