package edu.odu.cs.cowem

import com.moowork.gradle.node.task.NodeTask;

import org.apache.tools.ant.filters.ReplaceTokens
import org.apache.tools.ant.taskdefs.condition.Os

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Delete
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.Sync
import org.gradle.api.tasks.bundling.Zip

import org.hidetake.gradle.ssh.plugin.SshPlugin

import edu.odu.cs.cowem.documents.SingleScrollDocument
import edu.odu.cs.cowem.documents.WebsiteProject

/**
 * A plugin for describing a course website.
 */
class CourseWebsite implements Plugin<Project> {

    void apply (Project project) {
        
        new org.hidetake.gradle.ssh.plugin.SshPlugin().apply(project);
        project.getPluginManager().apply(com.moowork.gradle.node.NodePlugin.class);
        
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

                maven {
                    url "https://plugins.gradle.org/m2/"
                }
                
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

        project.task ('setup_copy_graphics_overrides',
            type: Copy, dependsOn: 'setup_copy_website_defaults'
        ) {
                from 'graphics'
                into 'build/website/graphics'
        }
        
        project.task ('setup_copy_styles_overrides',
            type: Copy, dependsOn: 'setup_copy_website_defaults'
        ) {
                from 'styles'
                into 'build/website/styles'
        }

        project.task ('setup_copy_index_overrides',
            dependsOn: 'setup_copy_styles_overrides'
        ) {
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
            dependsOn: ['setup_copy_index_overrides', 
                        'setup_copy_graphics_overrides',
                        'setup_copy_styles_overrides'])


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


        
        project.task ('singleScroll', dependsOn: 'build') {
            description 'Package the website into a single scroll.'
            inputs.dir 'build/website'
            // outputs.file 'build/combined/scroll-{nbasename}'
        } .doLast {
            Properties docProperties = new Properties()
            docProperties.put('_' + project.rootProject.course.delivery, '1')
            project.rootProject.course.properties.each { prop, value ->
                docProperties.put(prop, value.toString())
            }
            project.rootProject.course.ext.properties.each { prop, value ->
                docProperties.put(prop, value.toString())
            }

            String primaryName = "Directory/outline"; // eventually get from Course properties
            String primaryDoc = primaryName.substring(primaryName.indexOf('/')+1)
            String format = "scroll";

            project.delete('build/combined/' + primaryDoc)
            SingleScrollDocument doc = new SingleScrollDocument(
                    project.rootProject.website,
                    docProperties,
                    project.file('build/combined/' + primaryDoc),
                    project.file('build/website/'),
                    primaryName
                    );
            //doc.setDebugMode(true);
            doc.generate();
        }

        project.node {  
            version = '8.11.2' // current LTS version
            workDir = project.file("${project.buildDir}/nodejs")
            npmWorkDir = project.file("${project.buildDir}")
            nodeModulesDir = project.file("${project.buildDir}")
            download = true  // download node
        }
        
        project.build.doLast {
            project.copy {
                from 'build/website/styles/'
                include 'pdf-package.json'
                into 'build/'
                rename '.+', 'package.json'
            }
        }
        project.npm_install.dependsOn('build')
        
        project.task ('pdfScript', type: Copy, dependsOn: 'build') {
            from "build/website/styles/pdfScript.js"
            into "build"
            filter(ReplaceTokens, 
                   tokens: [scrollFile: project.rootDir.toString().replace("\\", "/") + '/build/combined/outline/index.html'
                            ])
        }

        project.task ('pdf', type: NodeTask, 
                dependsOn: ['npm_install', 'singleScroll', 'pdfScript']) {
            script = project.file('build/pdfScript.js')
        }
        project.pdf.doLast {
            project.delete('build/pdfScript.js')
        }
        
        
        
        
        
        project.task ('bb', dependsOn: 'build') {
            description 'Package the website for import into Blackboard.'
            inputs.dir 'build/website'
            // outputs.file 'build/packages/bb-${project.name}.zip'
        } .doLast {
            new BBPackage(project,
                project.course, 
                project.file('build')
                ).generate(project.file('build/packages/bb-' + project.name + '.zip'), false)
        }

        
        project.task ('bbthin', dependsOn: 'build') {
            description 'Create a Blackboard package that will link back to the website content.'
            inputs.files (project.fileTree('Directory').include('**/*.md'))
            outputs.file 'build/packages/bbthin-${project.name}.zip'
        } .doLast {
            new BBPackage(project,
                project.course,
                project.file('build')
                ).generate(project.file('build/packages/bbthin-' + project.name + '.zip'), true)
        }

        
        project.task ('deploy', type: Copy, dependsOn: 'build') {
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
        } .doLast {
            int k0 = project.course.sshDeployURL.indexOf('@')
            int k1 = project.course.sshDeployURL.indexOf(':')
            def hostName = project.course.sshDeployURL.substring(k0+1,k1)
            project.remotes.remotehost.host = hostName
            def userName = project.course.sshDeployURL.substring(0, k0)
            project.remotes.remotehost.user = userName
            def remotePath = project.course.sshDeployURL.substring(k1+1)
            if (project.course.sshDeployKey != null) {
                File keyFile = project.file(project.course.sshDeployKey)
                keyFile.setReadable(false,false)
                keyFile.setWritable(false,false)
                keyFile.setExecutable(false,false)
                keyFile.setReadable(true, true)
                keyFile.setWritable(true, true)
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
        } .doLast {
            if (project.course.rsyncDeployURL == null) {
                project.course.rsyncDeployURL = project.course.sshDeployURL
                if (project.course.rsyncDeployKey == null) {
                    project.course.rsyncDeployKey = project.course.sshDeployKey
                }
            }
            if (!project.course.rsyncDeployURL.endsWith('/')) {
                project.course.rsyncDeployURL = project.course.rsyncDeployURL + '/'
            }

            def sourceDir = 'build/website/'

            String sshCmd = "ssh";
            if (project.course.rsyncDeployKey != null) {
                File keyFile = project.file(project.course.rsyncDeployKey)
                println "keyFile is " + keyFile.toString() + ": setting permissions" 
                keyFile.setReadable(false,false)
                keyFile.setWritable(false,false)
                keyFile.setExecutable(false,false)
                keyFile.setReadable(true, true)
                keyFile.setWritable(true, true)
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


        project.task('listProperties') .doLast {
            println "All course properties:\n" + project.course.properties.collect{it}.join('\n')
        }
    }

}
