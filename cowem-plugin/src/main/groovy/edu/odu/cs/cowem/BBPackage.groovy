/**
 * 
 */
package edu.odu.cs.cowem;

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.Sync
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream;
import java.io.StringReader
import java.io.StringWriter;
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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;

import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import edu.odu.cs.cowem.documents.MarkdownDocument
import edu.odu.cs.cowem.documents.Utils;


/**
 * Prepares a package suitable for importation into Blackbaord.
 * This is an extension of the IMS CC standard.
 * 
 * @author zeil
 *
 */
class BBPackage {

    private static Logger logger =
    LoggerFactory.getLogger(BBPackage.class);



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
    BBPackage (Project theProject, Course theCourse,
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
    void generate (File destination, boolean thin)
    {
        destination.getParentFile().mkdirs()
        tempAreaAbs = new File(new File(buildDir, "temp"), "bb")
        if (tempAreaAbs.exists()) {
            tempAreaAbs.deleteDir()  // Groovy extension to java.io.File
        }
        tempAreaAbs.mkdirs()
        copyFiles (thin)
        buildManifest (thin)
        addHiddenFiles ()
        zipItAllUp (destination)
    }



    void copyFiles (boolean isThin)
    {
        if (isThin) {
            webcontentRel = 'webcontent'
            webcontentAbs = new File(tempAreaAbs, webcontentRel)
            webcontentAbs.mkdirs()
            File placeHolder = new File(webcontentAbs, 'placeHolder.txt')
            placeHolder.withWriter('UTF-8') {
                it.writeLine('foo')
            }
        } else {
            DateTimeFormatter format = DateTimeFormatter.ofPattern(
                "yyyy-MM-dd'T'HH-mm-ss")
            webcontentRel = 'webcontent-' + 
                LocalDateTime.now().format(format)
            webcontentAbs = tempAreaAbs.toPath().resolve(
                    'csfiles/home_dir/' + webcontentRel).toFile()
            webcontentAbs.mkdirs()
            def websiteFiles = project.fileTree(
                    dir: 'build/website/', include: '**/*')

            project.copy {
                from websiteFiles
                into webcontentAbs
            }
        }
    }

    void buildManifest (boolean isThin)
    {
        Properties docProperties = new Properties()
        docProperties.put('_' + project.rootProject.course.delivery, '1')
        project.rootProject.course.properties.each { prop, value ->
            docProperties.put(prop, value.toString())
        }

        String primaryName = 'outline';
        docProperties.put ("_bb", "1")
        MarkdownDocument doc =
                new MarkdownDocument(project.file('Directory/outline/outline.md'),
                project.rootProject.website, docProperties);
        doc.setDebugMode(true);
        String result = doc.transform("modules")

        int bodyPos = result.indexOf("<body")
        int lastTagPos = result.indexOf("</html")
        result = result.substring(bodyPos, lastTagPos)

        StringBuilder outlineDoc = new StringBuilder("<imscc>\n")

        outlineDoc.append("<courseName>");
        outlineDoc.append(project.rootProject.course.courseName)
        outlineDoc.append("</courseName>\n");

        outlineDoc.append("<outline>\n")
        outlineDoc.append(result)
        outlineDoc.append("\n</outline>\n<navigation>\n")

        MarkdownDocument navDoc =
                new MarkdownDocument(project.file('Directory/navigation/navigation.md'),
                project.rootProject.website,
                docProperties);

        navDoc.setDebugMode(true);
        String navResult = navDoc.transform("navigation")
        int start = navResult.indexOf("<body");
        start = navResult.indexOf('>', start);
        navResult = navResult.substring(start+1);
        int stop = navResult.indexOf("</body");
        navResult = navResult.substring(0, stop);

        outlineDoc.append (navResult)
        outlineDoc.append("\n</navigation>\n")

        outlineDoc.append("\n<files>\n")
        if (!isThin) {
            listFiles (outlineDoc)
        }
        outlineDoc.append("\n</files>\n")
        outlineDoc.append("\n</imscc>\n")

        transformManifest (outlineDoc.toString(), isThin)



    }


    org.w3c.dom.Document parseXML (String xml)
    {
        org.w3c.dom.Document result = null;
        try {
            DocumentBuilder b =
                    DocumentBuilderFactory.newInstance().newDocumentBuilder();
            result = b.parse(new InputSource(new StringReader(xml)));
        } catch (ParserConfigurationException e) {
            logger.error ("Could not set up XML parser: " + e);
        } catch (SAXParseException e) {
            logger.error("Parsing error from outline: "
                    + e);
            if (e.toString().contains("lineNumber:")) {
                Pattern p = Pattern.compile(
                        "lineNumber: (\\d+); columnNumber: (\\d+);");
                Matcher m  = p.matcher(e.toString());
                if (m.find()) {
                    String lNum = m.group(1);
                    int ln = Integer.parseInt(lNum);
                    String cNum = m.group(2);
                    int cn = Integer.parseInt(cNum);
                    String context = Utils.extractContext(xml, ln-1, cn-1);
                    logger.error("Generated output was:\n" + context);
                } else {
                    logger.error("Text was:\n" + xml);
                }
            } else {
                logger.error("Text was:\n" + xml);
            }
        } catch (SAXException e) {
            logger.error("Unable to parse outline: ", e);
            logger.error("Text was:\n" + xml);
        } catch (IOException e) {
            logger.error("Unable to parse outline: ", e);
            logger.error("Text was:\n" + xml);
        }
        return result;
    }


    void transformManifest (String outlineXML, boolean isThin)
    {
        String transformName = (isThin) ? "bbthinManifest.xsl": "bbManifest.xsl"

        org.w3c.dom.Document outlineDoc = parseXML(outlineXML);
        
        final String xsltLocation  = "/edu/odu/cs/cowem/templates/";
        final InputStream outlineConversionSheet =
                MarkdownDocument.class.getResourceAsStream(xsltLocation + transformName);

        if (outlineConversionSheet == null) {
            logger.error("Could not load stylesheet: " + transformName);
            return;
        }

        final InputStream oCSheet =
                MarkdownDocument.class.getResourceAsStream(xsltLocation + transformName);
        

        System.setProperty("javax.xml.transform.TransformerFactory",
                "net.sf.saxon.TransformerFactoryImpl");
        TransformerFactory transFact = TransformerFactory.newInstance();

        DocumentBuilder dBuilder = null;
        try {
            DocumentBuilderFactory dbFactory =
                    DocumentBuilderFactory.newInstance();
            dBuilder = dbFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            logger.error ("Problem creating new XML document ", e);
            return;
        }

        // Transform basic HTML into the selected format
        Properties projProperties = new Properties()
        projProperties.put('_' + project.rootProject.course.delivery, '1')
        project.rootProject.course.properties.each { prop, value ->
            projProperties.put(prop, value.toString())
        }

        String manifestContent = "";
        try {
            Source xslSource = new StreamSource(outlineConversionSheet);
            xslSource.setSystemId("http://www.cs.odu.edu/~zeil");
            Templates template = transFact.newTemplates(xslSource);
            Transformer xform = template.newTransformer();
            xform.setParameter("workDir", tempAreaAbs.toString());
            xform.setParameter("webcontentURL", webcontentRel);
            for (Object okey: projProperties.keySet()) {
                String key = okey.toString();
                String value = projProperties.getProperty(key).toString();
                xform.setParameter(key, value);
            }
            Source xmlIn = new DOMSource(outlineDoc.getDocumentElement());

            StringWriter xmlString = new StringWriter();
            StreamResult xmlOut = new StreamResult(xmlString);

            xform.transform(xmlIn, xmlOut);
            manifestContent = xmlString.toString()
        } catch (TransformerConfigurationException e) {
            logger.error ("Problem parsing XSLT2 stylesheet "
                    + outlineConversionSheet, e);
            return;
        } catch (TransformerException e) {
            logger.error ("Problem applying stylesheet "
                    + outlineConversionSheet, e);
            return;
        }

        File resultFile = new File(tempAreaAbs, "imsmanifest.xml")
        resultFile.getParentFile().mkdirs()
        resultFile.withWriter('UTF-8') {
            it.writeLine(manifestContent)
        }

    }


    void listFiles (StringBuilder buf)
    {
        // ToDo
    }

    void addHiddenFiles()
    {
        // Currently handled as a side effect of the bb*manifest.xsl
    }

    void zipItAllUp (File destination)
    {
        File bbDir = project.file("build/temp/bb")
        Path bbBase = bbDir.toPath();
        ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(destination));
        zout.close();
        Path zipfile = destination.toPath();
        Queue<File> q = new LinkedList<File>();
        q.push(bbDir);
        FileSystem zipfs = FileSystems.newFileSystem(zipfile, null);
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
