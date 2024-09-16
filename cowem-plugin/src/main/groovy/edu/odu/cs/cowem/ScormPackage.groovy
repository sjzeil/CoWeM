/**
 * 
 */
package edu.odu.cs.cowem;

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.FileTree
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.Sync
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream;
import java.io.StringReader
import java.io.StringWriter;
import java.lang.reflect.Array
import java.net.MalformedURLException
import java.nio.file.Files
import java.nio.file.FileSystem
import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipOutputStream

import edu.odu.cs.cowem.documents.MarkdownDocument
import edu.odu.cs.cowem.documents.Utils;


/**
 * Prepares a SCORM 1.2 package suitable for importation into Blackbaord
 * and many other Learning Management Systems.
 * 
 * @author zeil
 *
 */
class ScormPackage {

    private static Logger logger =
    LoggerFactory.getLogger(ScormPackage.class);



    Project project;

    Course course;

    File buildDir;


    File tempAreaAbs;
    File webcontentAbs;
    String webcontentRel;

    /**
     * Initialize the package builder
     * @param theProject    the website project 
     * @param theCourse     the course website descriptor
     * @param theBuildDirectory  the project build directory
     */
    ScormPackage (Project theProject, Course theCourse,
    File theBuildDirectory)
    {
        project = theProject
        course = theCourse
        buildDir = theBuildDirectory
    }

    /**
     * Generate the package suitable for import
     * @param destination  where to store the resulting package
     * @param thin if true, package will contain a modules outline and
     *     calendar entries but the outline will link to an external website
     *     for actual content. If false, the full content is included in the
     *     package. 
     */
    void generate (File destination)
    {
        destination.getParentFile().mkdirs()
        tempAreaAbs = new File(new File(buildDir, "temp"), "scorm")
        if (tempAreaAbs.exists()) {
            tempAreaAbs.deleteDir()  // Groovy extension to java.io.File
        }
        tempAreaAbs.mkdirs()
        def manifestFiles = copyFiles ()
        buildManifest (manifestFiles)
        zipItAllUp (destination)
    }



    FileTree copyFiles ()
    {
		webcontentAbs = tempAreaAbs
		webcontentAbs.mkdirs()
		def websiteFiles = project.fileTree(
				dir: 'build/website/', include: '**/*')

		project.copy {
			from websiteFiles
			into webcontentAbs
		}
		project.delete (new File(webcontentAbs, 'index.html'))
		
		project.copy {
			into webcontentAbs
			from ({ project.zipTree(project.buildscript.configurations.classpath.find {
					it.name.startsWith("cowem-plugin") }
				) }) {
				include 'edu/odu/cs/cowem/core/scorm/**'
			}
		}

		return websiteFiles
	}

    void buildManifest (FileTree websiteFiles)
    {
		def manifest = new BufferedWriter(
			new FileWriter(new File(tempAreaAbs, 'imsmanifest.xml'))
			)
		def manifestPrefix = [
			'<?xml version="1.0" standalone="no" ?>',
			'<manifest identifier="com.scorm.golfsamples.contentpackaging.singlesco.12" version="1"',
            '   xmlns="http://www.imsproject.org/xsd/imscp_rootv1p1p2"',
            '   xmlns:adlcp="http://www.adlnet.org/xsd/adlcp_rootv1p2"',
            '   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"',
            '   xsi:schemaLocation="http://www.imsproject.org/xsd/imscp_rootv1p1p2 imscp_rootv1p1p2.xsd',
            '                http://www.imsglobal.org/xsd/imsmd_rootv1p2p1 imsmd_rootv1p2p1.xsd',
            '                http://www.adlnet.org/xsd/adlcp_rootv1p2 adlcp_rootv1p2.xsd">',
            '  <metadata>',
			'     <schema>ADL SCORM</schema>',
            '     <schemaversion>1.2</schemaversion>',
            '  </metadata>',
 	        '  <organizations default="default_org">',
		    '     <organization identifier="default_org">']
		for(String line: manifestPrefix) {
			manifest.println (line);
		}
		manifest.println ('<title>' + course.courseName + '</title>')
		manifest.println ('<item identifier="item_1" identifierref="resource_1">')
		manifest.println ('  <title>' + course.courseName + '</title>')
		manifest.println('</item>')
		manifest.println('</organization>')
		manifest.println('</organizations>')
		manifest.println('<resources>')
		manifest.println(' <resource identifier="resource_1" type="webcontent" adlcp:scormtype="sco" href="Directory/outline/index.html">')
		
		def websiteBase = project.file('build/website')
		for (File f: websiteFiles.getFiles()) {
			if (!f.isDirectory()) {
				Path p = f.toPath()
				Path relPath = websiteBase.toPath().relativize(p)
				String relPathStr = relPath.toString().replace('\\', '/')
				if (!relPathStr.equals('index.html')) {
					manifest.println('<file href="' + relPathStr + '"/>')
			    }
			}
		}
		manifest.println("</resource>\n</resources>\n</manifest>\n")
		manifest.close()
    }



    void zipItAllUp (File destination)
    {
        File bbDir = project.file("build/temp/scorm")
        Path bbBase = bbDir.toPath();
        Map<String, String> env = new HashMap<>(); 
        env.put("create", "true");
        Path zipfile = destination.toPath();
        Queue<File> q = new LinkedList<File>();
        q.push(bbDir);
        FileSystem zipfs = FileSystems.newFileSystem(zipfile, env);
        if (zipfs == null) {
            logger.error ("Could not create zip file system at " + zipfile)
        }
        while (!q.isEmpty()) {
            File dir = q.remove()
            for (File child : dir.listFiles()) {
                Path relChild = bbBase.relativize(child.toPath());
                if (child.isDirectory()) {
                    q.add(child);
                    Path directory = zipfs.getPath("/", relChild.toString())
                    Files.createDirectories(directory)
                } else {
                    Path childLoc = zipfs.getPath("/", relChild.toString())
                    Files.copy(child.toPath(), childLoc)
                }
            }
        }
        zipfs.close()
    }
}
