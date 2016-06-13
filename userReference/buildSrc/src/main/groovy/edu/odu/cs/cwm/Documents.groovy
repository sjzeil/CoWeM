package edu.odu.cs.cwm

/*
 buildscript {
 repositories {
 // jcenter()
 mavenCentral()
 // Use my own CS dept repo
 ivy {
 url 'https://secweb.cs.odu.edu/~zeil/ivyrepo'
 }
 }
 dependencies {
 classpath 'edu.odu.cs.zeil:cwm-utils:1.0-SNAPSHOT'
 }
 }
 */

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Sync

import java.nio.file.Path
import java.nio.file.Paths
import edu.odu.cs.cwm.Course
import edu.odu.cs.cwm.DocumentSet
import edu.odu.cs.cwm.documents.ListingDocument
import edu.odu.cs.cwm.documents.MarkdownDocument

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
		project.extensions.create ('documents', DocumentSet, project)


		def templatesArea = project.file('../../build/cwm/templates/')
		def websiteArea = project.file('../../build/website/'
				+ project.parent.name + "/" + project.name + '/')

		project.task (type: Copy,
		dependsOn: project.rootProject.tasks['setup'],
		'doc_setup') {
			from (project.documents.supportDocuments
					+ project.documents.listingDocuments)
			into websiteArea
		}

		project.task (dependsOn: ['doc_setup', project.configurations.build],
		'doc_mainDoc') {
            println ("configuring mainDoc")
		    inputs.file project.documents.primaryDocument
			outputs.files project.documents.primaryTargets
            println ("done configuring mainDoc")
		}

		project.doc_mainDoc << {
			Properties docProperties = new Properties()
			docProperties.put('_' + project.rootProject.course.delivery, '1')
			project.rootProject.course.properties.each { prop, value ->
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
				println "starting format ${format}"

                MarkdownDocument doc =
                    new MarkdownDocument(project.documents.primaryDocument,
                        docProperties);
                //doc.setDebugMode(true);
				String result = doc.transform(format)

				File resultFile = project.file(websiteArea.toString() + '/'
						+ primaryName + "__" + format + ".html")
				resultFile.withWriter('UTF-8') {
					it.writeLine(result)
				}
				println "finished format ${format}"
			}
			File indexSource = new File(websiteArea,
				    primaryName + "__" 
					+ project.documents.indexFormat + ".html")
			File indexDest = new File(websiteArea, 'index.html')
			if (indexSource.exists()) {
			    project.copy {
				    from indexSource
				    into websiteArea
					rename {'index.html'}
			    }
			}
		}


		project.task (dependsOn: project.doc_setup, 'doc_secondaryDocs') {
            inputs.files project.documents.secondaryDocuments
            outputs.dir project.documents.secondaryTargets
		} << {
            Properties docProperties = new Properties()
            docProperties.put('_' + project.rootProject.course.delivery, '1')
            project.rootProject.course.properties.each { prop, value ->
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
                println "secondary: {secondarySource}"

                MarkdownDocument doc =
                    new MarkdownDocument(secondarySource,
                        docProperties);
                //doc.setDebugMode(true);
                String result = doc.transform("html")

                Path rootProjDir = project.file('../../').toPath()
                Path p1 = rootProjDir.relativize(secondarySource.toPath())
                Path websiteDirP = project.file('../../build/website').toPath()
                Path resultFileP = websiteDirP.resolve(p1)
                File websiteParent = resultFileP.toFile().getParentFile()
                websiteParent.mkdirs()
				File resultFile = new File(websiteParent, secondarySource.getName() + ".html")
                resultFile.withWriter('UTF-8') {
                    it.writeLine(result)
                }

                println "finished secondary doc ${secondarySource}"
            }
		}

		project.task (dependsOn: project.doc_setup, 'doc_Listings') {
            inputs.files project.documents.listingDocuments
            outputs.dir project.documents.listingTargets
		} << {
            Properties docProperties = new Properties()
            docProperties.put('_' + project.rootProject.course.delivery, '1')
            project.rootProject.course.properties.each { prop, value ->
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
                println "listing: ${listingSource}"

                ListingDocument doc =
                    new ListingDocument(listingSource,
                        docProperties);
                String result = doc.transform("html")
                
                Path rootProjDir = project.file('../../').toPath()
                Path p1 = rootProjDir.relativize(listingSource.toPath())
                Path websiteDirP = project.file('../../build/website').toPath()
                Path resultFileP = websiteDirP.resolve(p1)
                File websiteParent = resultFileP.toFile().getParentFile()
                websiteParent.mkdirs()
				File resultFile = new File(websiteParent, listingSource.getName() + ".html")
                resultFile.withWriter('UTF-8') {
                    it.writeLine(result)
                }
                println "finished listing doc ${listingSource}"
            }
        }


		project.task (dependsOn: [project.doc_mainDoc,
			project.doc_secondaryDocs,
			project.doc_Listings],
		  'build') {
			description 'Prepare document set output'
			dependsOn ':setup'
		}


		project.rootProject.tasks['build'].dependsOn(project.build)




		project.task('listProperties') << {
			println "All docSet properties:\n" + project.documents.properties.collect{it}.join('\n')
			println "\n secondaryDocuments:\t" + project.documents.secondaryDocuments.collect{it}.join(' ')
			println " listingDocuments:\t" + project.documents.listingDocuments.collect{it}.join(' ')
			println " supportDocuments:\t" + project.documents.supportDocuments.collect{it}.join(' ')
		}

	}

}
