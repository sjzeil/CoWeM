package edu.odu.cs.cowem


import org.gradle.api.Plugin

import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Delete
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.Sync
import org.gradle.api.tasks.bundling.Zip

import org.apache.tools.ant.filters.ReplaceTokens

import java.nio.file.Path
import java.nio.file.Paths
import edu.odu.cs.cowem.Course
import edu.odu.cs.cowem.DocumentSet
import edu.odu.cs.cowem.documents.ListingDocument
import edu.odu.cs.cowem.documents.MarkdownDocument
import edu.odu.cs.cowem.documents.WebsiteProject

/**
 * A plugin for describing a documentset within a course website.
 * 
 * A document set resides in a single directory. It includes 
 *   1) a primary document (in Markdown), which can be translated into
 *      a variety of HTML-based formats, 
 *   2) an arbitrary number of secondary documents (also in Markdown),
 *      each of which will be translated into an HTML page,
 *   3) an arbitrary number of source code listings, each of which will
 *      be rendered as an HTML page with a link to the unmodified source code,
 *      and
 *   4) an arbitrary number of support files, such as graphics, css files, 
 *      etc., that will be copied to the website without modification. 
 */
class Documents implements Plugin<Project> {

	void apply (Project project) {

		project.configurations {
			build
		}




		// Add a DocumentSet object as a property of the project
        if (!project.hasProperty('documents')) {
		    project.extensions.create ('documents', DocumentSet, project)
        }

		def templatesArea = project.file('../../build/cowem/templates/')
		def websiteArea = project.file('../../build/website/'
				+ project.parent.name + "/" + project.name + '/')

		project.task (type: Copy,
		dependsOn: project.rootProject.tasks['setup'],
		'doc_setup') {
			from {return project.documents.supportDocuments
					+ project.documents.listingDocuments}
			into websiteArea
            duplicatesStrategy = 'include'
		}
		project.task (type: Copy,
		        dependsOn: project.rootProject.tasks['setup'],
		        'filtered_setup') {
		            from {return project.documents.filteredDocuments}
		            into websiteArea
		            filter (ReplaceTokens,  
		                    tokens:  {  
		                      Map<String,String> propFilters = new HashMap<String,String>(); 
		                      project.rootProject.course.properties.each { prop, value ->
	                            propFilters.put(prop, value.toString())
		                      }
		                      project.rootProject.course.ext.properties.each { prop, value ->
                              propFilters.put(prop, value.toString())
                            }
		                      propFilters;
		                    }()
		                   )
                    duplicatesStrategy = 'include'
		        }
		
		project.task ('doc_mainDoc',
            dependsOn: ['doc_setup', 'filtered_setup', project.configurations.build]) {
            //println ("mainDoc: " + project.documents.primaryDocument
			//	+ " => " + project.documents.primaryTargets.join(','))
		    inputs.file project.documents.primaryDocument
			outputs.dir project.documents.indexTarget.parentFile
		}

		project.doc_mainDoc .doLast {
			Properties docProperties = new Properties()
			docProperties.put('_' + project.rootProject.course.delivery, '1')
			project.rootProject.course.properties.each { prop, value ->
				docProperties.put(prop, value.toString())
			}
			project.rootProject.course.ext.properties.each { prop, value ->
                docProperties.put(prop, value.toString())
			}
			project.documents.properties.each { prop, value ->
				docProperties.put(prop, value.toString())
			}
			String primaryName = project.documents.primaryDocument.name;
			int k = primaryName.lastIndexOf('.');
			if (k >= 0) {
				primaryName = primaryName.substring(0, k);
			}
			if (!websiteArea.exists()) {
				websiteArea.mkdirs();
			}
			for (String format: project.documents.formats) {
				
                MarkdownDocument doc =
                    new MarkdownDocument(project.documents.primaryDocument,
                        project.rootProject.website,
                        docProperties);
                //doc.setDebugMode(true);
				String result = doc.transform(format)
                
				File resultFile = project.file(websiteArea.toString() + '/'
						+ primaryName + "__" + format + ".html")
                resultFile.getParentFile().mkdirs()
				resultFile.withWriter('UTF-8') {
					it.writeLine(result)
				}
			}
			File indexSource = project.documents.indexTarget;
			File indexDest = new File(websiteArea, 'index.html')
			if (indexSource.exists()) {
			    project.copy {
				    from indexSource
				    into websiteArea
					rename {'index.html'}
			    }
			} else {
                logger.warn (indexSource.toString() + " was not built.")
			}
		}


		project.task ('doc_secondaryDocs', dependsOn: project.doc_setup) {
            inputs.files project.documents.secondaryDocuments
            outputs.dir project.documents.indexTarget.parentFile
		} .doLast {
            Properties docProperties = new Properties()
            docProperties.put('_' + project.rootProject.course.delivery, '1')
            project.rootProject.course.properties.each { prop, value ->
                docProperties.put(prop, value.toString())
            }
            project.rootProject.course.ext.properties.each { prop, value ->
                docProperties.put(prop, value.toString())
            }
            project.documents.properties.each { prop, value ->
                docProperties.put(prop, value.toString())
            }
            String primaryName = project.documents.primaryDocument.name;
            int k = primaryName.lastIndexOf('.');
            if (k >= 0) {
                primaryName = primaryName.substring(0, k);
            }
            if (!websiteArea.exists()) {
                websiteArea.mkdirs();
            }
            for (File secondarySource: project.documents.secondaryDocuments) {
                MarkdownDocument doc =
                    new MarkdownDocument(secondarySource,
                        project.rootProject.website,
                        docProperties);
                //doc.setDebugMode(true);
                String result = doc.transform("scroll")

                Path rootProjDir = project.rootDir.toPath()
                Path p1 = rootProjDir.relativize(secondarySource.toPath())
                Path websiteDirP = project.file('../../build/website').toPath()
                Path resultFileP = websiteDirP.resolve(p1)
                File websiteParent = resultFileP.toFile().getParentFile()
                websiteParent.mkdirs()
				File resultFile = new File(websiteParent, secondarySource.getName() + ".html")
                resultFile.getParentFile().mkdirs()
                resultFile.withWriter('UTF-8') {
                    it.writeLine(result)
                }
            }
		}

		project.task ('doc_Listings', dependsOn: project.doc_setup) {
            inputs.files project.documents.listingDocuments
            outputs.dir project.documents.indexTarget.parentFile
		} .doLast {
            Properties docProperties = new Properties()
            docProperties.put('_' + project.rootProject.course.delivery, '1')
            project.rootProject.course.properties.each { prop, value ->
                docProperties.put(prop, value.toString())
            }
            project.rootProject.course.ext.properties.each { prop, value ->
                docProperties.put(prop, value.toString())
            }
            project.documents.properties.each { prop, value ->
                docProperties.put(prop, value.toString())
            }
            String primaryName = project.documents.primaryDocument.name;
            int k = primaryName.lastIndexOf('.');
            if (k >= 0) {
                primaryName = primaryName.substring(0, k);
            }
            if (!websiteArea.exists()) {
                websiteArea.mkdirs();
            }
            for (File listingSource: project.documents.listingDocuments) {
                ListingDocument doc =
                    new ListingDocument(listingSource,
                        project.rootProject.website,
                        docProperties);
                String result = doc.transform("scroll")
                
                Path rootProjDir = project.rootDir.toPath()
                Path p1 = rootProjDir.relativize(listingSource.toPath())
                Path websiteDirP = project.file('../../build/website').toPath()
                Path resultFileP = websiteDirP.resolve(p1)
                File websiteParent = resultFileP.toFile().getParentFile()
                websiteParent.mkdirs()
				File resultFile = new File(websiteParent, listingSource.getName() + ".html")
                resultFile.withWriter('UTF-8') {
                    it.writeLine(result)
                }
            }
        }


		project.task ('build',
            dependsOn: [project.doc_mainDoc,
			            project.doc_secondaryDocs,
			            project.doc_Listings]
		  ) {
			description 'Prepare document set output'
			group 'Build'
			dependsOn ':setup'
		}


		project.rootProject.tasks['build'].dependsOn(project.build)

        
        
        project.task ('deployDoc', type: Copy, dependsOn: 'build') {
            description 'Copy this document set to a local deployDestination directory.'
            group 'Deployment'
            from websiteArea
            into { return project.course.deployDestination +
                ((project.course.deployDestination.endsWith('/'))? '' : '/')  +
                     project.parent.name + '/' + project.name; }
            dirMode 0775
            includeEmptyDirs true
            duplicatesStrategy = 'include'
        }

        
        project.task ('doczip', type: Zip, dependsOn: 'build') {
            // description 'Prepare a zip file of the website.'
            group 'Packaging'
            //onlyIf {true}
            
            from "../../build/website"
            include "${project.parent.name}/${project.name}/**"
            
            destinationDirectory = project.file('../../build/packages')
            archiveFileName = "website-${project.name}.zip"
            dirMode 0775
            fileMode 0664
            includeEmptyDirs true
        }
    

        project.task ('deployDocBySsh', dependsOn: 'doczip') {
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
                project.remotes.remotehost.identity =
                        project.file(project.course.sshDeployKey)
            }

            project.ssh.run {
                settings {
                    dryRun = false
                }
                session (project.remotes.remotehost) {
                    put from: project.file("../../build/packages/website-${project.name}.zip"),
                    into: remotePath
                    execute "unzip -u -q -o ${remotePath}/website-${project.name}.zip -d ${remotePath}"
                    execute "/bin/rm -f ${remotePath}/website-${project.name}.zip"
                }
                println "Sent to " + project.course.sshDeployURL
            }
        }



        project.task ('deployDocByRsync', dependsOn: 'build') {
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
            def sourceDir = "../../build/website/${project.parent.name}/${project.name}/"

            String sshCmd = "ssh";
            if (project.course.rsyncDeployKey != null) {
                sshCmd = "ssh -i ${project.rootProject.file(project.course.rsyncDeployKey)}"
            }
            def cmd = [
                'rsync',
                '-auzv',
                '-e' + sshCmd,
                sourceDir,
                project.course.rsyncDeployURL + 
                    "${project.parent.name}/${project.name}/"
                ]

            println ("Issuing rsync command\n" + cmd.iterator().join(" "))
            project.exec {
                commandLine = cmd
                if (project.course.rsyncDeployKey != null) {
                    environment ('SSH_AGENT_PID', '')
                    environment ('SSH_AUTH_SOCK', '')
                }
            }
        }



		project.task('listProperties') .doLast {
			println "All docSet properties:\n" + project.documents.properties.collect{it}.join('\n')
			println "\n secondaryDocuments:\t" + project.documents.secondaryDocuments.collect{it}.join(' ')
			println " listingDocuments:\t" + project.documents.listingDocuments.collect{it}.join(' ')
			println " supportDocuments:\t" + project.documents.supportDocuments.collect{it}.join(' ')
		}

	}

}
