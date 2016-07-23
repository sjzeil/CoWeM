package edu.odu.cs.cowem

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Delete
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.Sync
import org.gradle.api.tasks.bundling.Zip

import org.hidetake.gradle.ssh.plugin.SshPlugin

import edu.odu.cs.cowem.documents.WebsiteProject

/**
 * A plugin for describing a course website.
 */
class CourseWebsite implements Plugin<Project> {

    void apply (Project project) {
        
        new org.hidetake.gradle.ssh.plugin.SshPlugin().apply(project);

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
            build
            deploy
        }



        project.task('setup_cowem', type: Copy) {
            //dependsOn project.configurations.setup
            into project.file('build/temp/cowem')
            from ({ project.zipTree(project.buildscript.configurations.classpath.find {
                    it.name.startsWith("cowem-plugin") }
                ) }) {
                include 'edu/odu/cs/cowem/core/graphics/**'
                include 'edu/odu/cs/cowem/core/styles/**'
            }
        }


        project.task ('setup_copy_website_defaults',
            type: Copy, dependsOn: 'setup_cowem'
        ) {
            from 'build/temp/cowem/edu/odu/cs/cowem/core'
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
            dependsOn project.setup_cowem, project.setup_website
        }


        project.task ('build', dependsOn: 'setup') {
            description 'Process documents and course outline to prepare the basic course website.'
            group 'Build'
        }
        
        
        project.task ('clean', type: Delete)
        {
            description 'Clean the project (delete the build directory)'
            delete 'build'
            //followSymlinks = false
        }



        project.task ('packages', dependsOn: 'build')  {
            description 'prepare optional course cartridges'
            group 'Packaging'
        }

        project.task ('zip', type: Zip, dependsOn: 'build') {
            description 'Prepare a zip file of the website.'
            group 'Packaging'
            from 'build/website'
            //into '.'
            destinationDir = project.file('build/packages')
            archiveName 'website.zip'
            dirMode 0775
            fileMode 0664
            includeEmptyDirs true
        }


        
        project.task ('bb', dependsOn: 'build') {
            description 'Package the website for import into Blackboard.'
            inputs.dir 'build/website'
            // outputs.file 'build/packages/bb-${project.name}.zip'
        } << {
            new BBPackage(project,
                project.course, 
                project.file('build')
                ).generate(project.file('build/packages/bb-' + project.name + '.zip'), false)
        }

        
        project.task ('bbthin', dependsOn: 'build') {
            description 'Create a Blackboard package that will link back to the website content.'
            inputs.dir 'build/website'
            outputs.file 'build/packages/bbthin-${project.name}.zip'
        } << {
            new BBPackage(project,
                project.course,
                project.file('build')
                ).generate(project.file('build/packages/bbthin-' + project.name + '.zip'), true)
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
            if (!project.course.rsyncDeployURL.endsWith('/')) {
                project.course.rsyncDeployURL = project.course.rsyncDeployURL + '/'
            }
            def sourceDir = project.file('build/website/').toString()
            if (!sourceDir.endsWith('/')) {
                sourceDir = sourceDir + '/'
            }

            String sshCmd = "ssh";
            if (project.course.rsyncDeployKey != null) {
                sshCmd = "ssh -i ${project.course.rsyncDeployKey}"
            }
            def cmd = [
                    'rsync',
                    '-auzv',
                    '-e' + sshCmd,
                    sourceDir,
                    project.course.rsyncDeployURL
                    ]

            println ("Issuing rsync command\n" + cmd.iterator().join(" "))
            project.exec {
                commandLine cmd
                if (project.course.rsyncDeployKey != null) {
                    environment ('SSH_AGENT_PID', '')
                    environment ('SSH_AUTH_SOCK', '')
                }
            }
        }


        project.task ('publish', dependsOn: 'build') {

        }


        project.task('listProperties') << {
            println "All course properties:\n" + project.course.properties.collect{it}.join('\n')
        }
    }

}
