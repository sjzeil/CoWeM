/**
 * 
 */
package edu.odu.cs.cowem.documents;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Collects a base document (in scroll format) and all documents referenced
 * within it via doc: links into a single scroll.
 * 
 * @author zeil
 *
 */
class SingleScrollDocument {

    private static Logger logger = LoggerFactory.getLogger(SingleScrollDocument.class);

    /**
     * Context info for this document. Indicates the project's root and all
     * available document sets.
     */
    private WebsiteProject project;

    /**
     * Properties to be used when processing this document.
     */
    private Properties properties;

    File buildDir;

    String baseDocument;

    File tempAreaAbs;
    File webcontentAbs;
    String webcontentRel;
    
    File websiteBase;
    
    org.w3c.dom.Document baseDoc;

    /**
     * Initialize the package builder
     * 
     * @param theProject
     *            the website project
     * @param theProperties
     *            the course properties
     * @param theBuildDirectory
     *            the project build directory
     * @param theWebsite
     *            the already constructed website
     * @param theBaseDocument
     *            the document ("Group/documentSet") forming the outline/TOC of
     *            the combined scroll
     */
    SingleScrollDocument(final WebsiteProject theProject, final Properties theProperties, File theBuildDirectory,
            File theWebsite, String theBaseDocument) {
        project = theProject;
        properties = theProperties;
        buildDir = theBuildDirectory;
        baseDocument = theBaseDocument;
        websiteBase = theWebsite;
    }

    /**
     * Generate the single scroll package suitable for import
     * 
     */
    public void generate() {
        buildDir.mkdirs();
        List<String> referencedDocuments;
        try {
            referencedDocuments = copyBaseDocument();
            Set<String> copiedDocuments = new HashSet<String>();
            copiedDocuments.add(baseDocument);
            for (String docRef : referencedDocuments) {
                String docName = documentName(docRef);
                if (!copiedDocuments.contains(docName)) {
                    copiedDocuments.add(docName);
                    mergeScroll(docName);
                }
            }
            rewriteDocReferences();
        } catch (IOException e) {
            logger.error("Error handling files: ", e);
        }
    }

    public List<String> copyBaseDocument() throws IOException {
        System.err.println("base Document is " + baseDocument);
        File baseDocumentSource = Paths.get(websiteBase.getAbsolutePath(), baseDocument).toFile();
        // TODO
        /*
         * project.copy { from baseDocumentSource into buildDir }
         */
        File stylesDir = new File(buildDir, "styles");
        stylesDir.mkdirs();
        copyAll (new File(websiteBase, "styles").toPath(), stylesDir.toPath());
        File graphicsDir = new File(buildDir, "graphics");
        graphicsDir.mkdirs();
        copyAll (new File(websiteBase, "graphics").toPath(), graphicsDir.toPath());
        
        /*
         * 
         * TODO project.copy { from "build/website/styles/" into stylesDir }
         */
        String baseDocumentName = baseDocument.substring(baseDocument.indexOf('/'));
        File baseDocFile = new File(baseDocumentSource, baseDocumentName + "__scroll.html");
        if (!baseDocFile.exists()) {
            System.err.println("Could not find scroll format for " + baseDocumentName);
            return new ArrayList<String>();
        }
        baseDoc = parseXML(new FileReader(baseDocFile));
        Node baseRoot = baseDoc.getDocumentElement();
        XPath xPath = XPathFactory.newInstance().newXPath();

        // Collect list of referenced docs
        NodeList nodes;
        try {
            nodes = (NodeList) xPath.evaluate("/html/body//a[@class='doc']", baseRoot, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            e.printStackTrace();
            nodes = baseDoc.getElementsByTagName("emptyListPlease");
        }
        List<String> referencedDocuments = new ArrayList<String>();
        for (int i = 0; i < nodes.getLength(); ++i) {
            Element node = (Element) nodes.item(i);
            String href = node.getAttribute("href");
            if (href.startsWith("../")) {
                String referencedDocument = href.substring(3);
                if (referencedDocument.startsWith("../")) {
                    referencedDocument = referencedDocument.substring(3);
                }
                int firstSlashPos = referencedDocument.indexOf('/');
                int secondSlashPos = referencedDocument.indexOf('/', firstSlashPos + 1);
                referencedDocument = referencedDocument.substring(0, secondSlashPos);
                referencedDocuments.add(referencedDocument);
                System.out.println("Base document refers to " + referencedDocument);
            }
        }

        // Rewrite the base document

        // 1. Remove the navigation header & footer
        try {
            Node body = (org.w3c.dom.Node) xPath.evaluate("/html/body", baseRoot, XPathConstants.NODE);
            NodeList topLevel = body.getChildNodes();
            for (int i = topLevel.getLength() - 1; i >= 0; --i) {
                Node node = topLevel.item(i);
                if (node instanceof Element) {
                    Element el = (Element) node;
                    if (el.getTagName().contentEquals("div")) {
                        String elClass = el.getAttribute("class");
                        if (elClass.contentEquals("navHeader") || elClass.equals("navFooter")) {
                            body.removeChild(node);
                        }
                    }
                }
            }
        } catch (XPathExpressionException e) {
            logger.error ("xpath error: ", e);
        }

        
        return referencedDocuments;
    }

    /**
     * Copies all files from one directory into another
     * @param from  source directory
     * @param into  destination directory
     * @throws IOException if unable to copy a file
     */
    private void copyAll(Path from, Path into) throws IOException {
        Files.walkFileTree(from,
                new SimpleFileVisitor<Path> () {
            @Override
            public FileVisitResult visitFile(Path file,
                    BasicFileAttributes attributes) throws IOException {
                Path targetFile = into.resolve(from.relativize(file));
                Files.copy(file, targetFile);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult preVisitDirectory(Path dir,
                    BasicFileAttributes attributes) throws IOException {
                Path newDir = into.resolve(from.relativize(dir));
                if (!Files.exists(newDir)) {
                    Files.createDirectory(newDir);
                }

                return FileVisitResult.CONTINUE;
            }


        }
                );

    }

    String documentName(String docRef) {
        return "x"; // TODO
    }

    void mergeScroll(String documentName) {
        // TODO
    }

    void rewriteDocReferences() {
        // TODO
    }

    void copyFiles(boolean isThin) {
        if (isThin) {
            webcontentRel = "webcontent";
            webcontentAbs = new File(tempAreaAbs, webcontentRel);
            webcontentAbs.mkdirs();
            File placeHolder = new File(webcontentAbs, "placeHolder.txt");
            /*
             * TODO placeHolder.withWriter('UTF-8') { it.writeLine('foo') }
             */
        } else {
            DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH-mm-ss");
            webcontentRel = "webcontent-" + LocalDateTime.now().format(format);
            webcontentAbs = tempAreaAbs.toPath().resolve("csfiles/home_dir/" + webcontentRel).toFile();
            webcontentAbs.mkdirs();
            // TODO
            // def websiteFiles = project.fileTree(
            // dir: 'build/website/', include: '**/*')
            /*
             * project.copy { from websiteFiles into webcontentAbs }
             */
        }
    }

    org.w3c.dom.Document parseXML(Reader xmlIn) {
        org.w3c.dom.Document result = null;
        try {
            DocumentBuilder b = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            result = b.parse(new InputSource(xmlIn));
        } catch (ParserConfigurationException e) {
            logger.error("Could not set up XML parser: " + e);
        } catch (SAXParseException e) {
            logger.error("Parsing error from outline: " + e);
        } catch (SAXException e) {
            logger.error("Unable to parse xml: ", e);
        } catch (IOException e) {
            logger.error("Unable to parse xml: ", e);
        }
        return result;
    }

    org.w3c.dom.Document parseXML(String xml) {
        return parseXML(new StringReader(xml));
    }

    void write(Writer output, org.w3c.dom.Document formattedDoc) {
        String debugMode = "no";
        // Generate result text
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, debugMode);
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            Source source = new DOMSource(formattedDoc.getDocumentElement());
            StreamResult htmlOut = new StreamResult(output);
            transformer.transform(source, htmlOut);
        } catch (TransformerConfigurationException e) {
            logger.error("Problem creating empty stylesheet " + ": " + e);
        } catch (TransformerException e) {
            logger.error("Problem serializing formatted document " + e);
        }
    }

    void listFiles(StringBuilder buf) {
        // ToDo
    }

    void addHiddenFiles() {
        // Currently handled as a side effect of the bb*manifest.xsl
    }

    public org.w3c.dom.Document toXML() {
        return baseDoc;
    }
    
    public String toString() {
        StringWriter stringOut = new StringWriter();
        write(stringOut, baseDoc);
        stringOut.flush();
        return stringOut.toString();
    }

    public void copyComponentDocument(String string) {
        // TODO Auto-generated method stub
        
    }

}
