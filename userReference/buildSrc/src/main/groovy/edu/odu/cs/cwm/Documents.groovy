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

import java.nio.file.Path;;
import edu.odu.cs.cwm.Course
import edu.odu.cs.cwm.DocumentSet
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
		    inputs.file project.documents.primaryDocument
			outputs.file new File(websiteArea, 'index.html')
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
			MarkdownDocument doc =
					new MarkdownDocument(project.documents.primaryDocument);
			String primaryName = project.documents.primaryDocument.name;
			int k = primaryName.lastIndexOf('.');
			if (k >= 0) {
				primaryName = primaryName.substring(0, k);
			}
			for (String format: project.documents.formats) {
				docProperties.put('_' + format, '1')
				String result = doc.transform(format, docProperties)

				File resultFile = project.file(websiteArea.toString() + '/'
						+ primaryName + "__" + format + ".html")
				resultFile.withWriter('UTF-8') {
					it.writeLine(result)
				}
				docProperties.remove('_' + format)
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

		}

		project.task (dependsOn: project.doc_setup, 'doc_Listings') {

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
