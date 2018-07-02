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
import java.io.InputStream;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
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
public class SingleScrollDocument {

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
    
    Set<String> documentsInScroll;
    List<String> documentQueue;

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
        documentsInScroll = new HashSet<String>();
        documentsInScroll.add(theBaseDocument);
        documentQueue = new LinkedList<String>();
    }

    /**
     * Generate the single scroll package suitable for import
     * 
     */
    public void generate() {
        buildDir.mkdirs();
        try {
            copyBaseDocument();
            Set<String> copiedDocuments = new HashSet<String>();
            copiedDocuments.add(baseDocument);
            for (String docRef : documentQueue) {
                if (!copiedDocuments.contains(docRef)) {
                    copiedDocuments.add(docRef);
                    copyComponentDocument(docRef);
                }
            }
            
            org.w3c.dom.Document finalDoc = transformEntireScroll();
            
            File outputFile = new File(buildDir, "index.html");
            Writer writer = new BufferedWriter(new FileWriter(outputFile));
            write (writer, finalDoc);
            writer.close();
            
        } catch (IOException e) {
            logger.error("Error handling files: ", e);
        }
    }

    /**
     * Perform final transformations on entire scroll.
     * (From md-combined.xsl)
     */
    private org.w3c.dom.Document transformEntireScroll() {
        final String xsltLocation  = "/edu/odu/cs/cowem/templates/";
        final InputStream formatConversionSheet = 
                MarkdownDocument.class.getResourceAsStream(
                    xsltLocation + "md-combined.xsl");
        
        System.setProperty("javax.xml.transform.TransformerFactory", 
                "net.sf.saxon.TransformerFactoryImpl"); 
        TransformerFactory transFact = TransformerFactory.newInstance();
        transFact.setURIResolver((href, base) -> {
            //System.err.println("resolving URI to: " + xsltLocation + href);
            final InputStream s = this.getClass()
                    .getResourceAsStream(xsltLocation + href);
            return new StreamSource(s);
        });

        DocumentBuilder dBuilder = null;
        try {
            DocumentBuilderFactory dbFactory = 
                    DocumentBuilderFactory.newInstance();
            dBuilder = dbFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            logger.error ("Problem creating new XML document ", e); 
            return null;        
        }
        
        
        // Transform basic HTML into the selected format
        
        org.w3c.dom.Document formattedDoc = null;       
        try {
            Source xslSource = new StreamSource(formatConversionSheet);
            xslSource.setSystemId("http://www.cs.odu.edu/~zeil");
            formattedDoc = dBuilder.newDocument();
            Templates template = transFact.newTemplates(xslSource);
            Transformer xform = template.newTransformer();
            xform.setParameter("format", "combined");
            for (Object okey: properties.keySet()) {
                String key = okey.toString();
                xform.setParameter(key, properties.getProperty(key));
                logger.info("prop " + key + " => " 
                                   + properties.getProperty(key));
            }
            Source xmlIn = new DOMSource(baseDoc.getDocumentElement());
            DOMResult htmlOut = new DOMResult(formattedDoc);
            xform.transform(xmlIn, htmlOut);
            logger.trace("combined transformation completed");
            baseDoc = formattedDoc;
        } catch (TransformerConfigurationException e) {
            logger.error ("Problem parsing XSLT2 stylesheet " 
                    + formatConversionSheet, e);
        } catch (TransformerException e) {
            logger.error ("Problem applying stylesheet " 
                    + formatConversionSheet, e);
        }
        return formattedDoc;
    }

    public Set<String> copyBaseDocument() throws IOException {
        logger.info("base Document is " + baseDocument);
        File baseDocumentSource = Paths.get(websiteBase.getAbsolutePath(), baseDocument).toFile();
        File stylesDir = new File(buildDir, "styles");
        stylesDir.mkdirs();
        copyAll (new File(websiteBase, "styles").toPath(), stylesDir.toPath());
        File graphicsDir = new File(buildDir, "graphics");
        graphicsDir.mkdirs();
        copyAll (new File(websiteBase, "graphics").toPath(), graphicsDir.toPath());
        
        String baseDocumentName = baseDocument.substring(baseDocument.indexOf('/')+1);
        String baseDocumentGroup = baseDocument.substring(0, baseDocument.indexOf('/'));
        File baseDocFile = new File(baseDocumentSource, baseDocumentName + "__scroll.html");
        if (!baseDocFile.exists()) {
            logger.error("Could not find scroll format for " + baseDocumentName);
            return new HashSet<String>();
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
                if (!documentsInScroll.contains(referencedDocument)) {
                    documentsInScroll.add(referencedDocument);
                    documentQueue.add(referencedDocument);
                }
                System.out.println("Base document refers to " + referencedDocument);
            }
        }

        // Rewrite the base document

        // 1. Remove the navigation header & footer
        Node body = null;
        try {
            body = (org.w3c.dom.Node) xPath.evaluate("/html/body", baseRoot, XPathConstants.NODE);
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

        // 2. Rewrite links
        try {
            replaceURLs((NodeList)
                    xPath.evaluate("/html/head/link[@type='text/css']", baseRoot, XPathConstants.NODESET),
                    "href", baseDocumentGroup, baseDocumentName);
            replaceURLs((NodeList)
                    xPath.evaluate("/html/body//img", baseRoot, XPathConstants.NODESET),
                    "src", baseDocumentGroup, baseDocumentName);
            replaceURLs((NodeList)
                    xPath.evaluate("/html/body//a", baseRoot, XPathConstants.NODESET),
                    "href", baseDocumentGroup, baseDocumentName);
        } catch (XPathExpressionException e) {
            logger.error("Unable to process xpath", e);
        }
        // 2. Rewrite IDs
        try {
            replaceIDs((NodeList)
                    xPath.evaluate("/html/body//*[@id != '']", baseRoot, XPathConstants.NODESET),
                    baseDocumentGroup, baseDocumentName);
        } catch (XPathExpressionException e) {
            logger.error("Unable to process xpath", e);
        }
        Element eBody = (Element)body;
        eBody.setAttribute("id", baseDocumentGroup + "__" + baseDocumentName);
        
        
        // 3. Copy non-html files
        copyAuxiliaryFiles (baseDocumentSource, buildDir, baseDocumentGroup, baseDocumentName);
        
        
        return documentsInScroll;
    }
    
    
    public void copyComponentDocument(String documentID) throws IOException {
        logger.info("component Document is " + documentID);
        File componentDocSource = Paths.get(websiteBase.getAbsolutePath(), documentID).toFile();
        
        String compDocumentName = documentID.substring(documentID.indexOf('/')+1);
        String compDocumentGroup = documentID.substring(0, documentID.indexOf('/'));
        File compDocFile = new File(componentDocSource, compDocumentName + "__scroll.html");
        if (!compDocFile.exists()) {
            logger.error("Could not find scroll format for " + compDocumentName);
        }
        org.w3c.dom.Document componentDoc = parseXML(new FileReader(compDocFile));
        Node componentRoot = componentDoc.getDocumentElement();
        XPath xPath = XPathFactory.newInstance().newXPath();

 
        // Rewrite the component document

        // 1. Find the main content area
       
        Node content = null;
        try {
            content = (org.w3c.dom.Node) xPath.evaluate("/html/body/div[@class='mainBody']", componentRoot, 
                    XPathConstants.NODE);
        } catch (XPathExpressionException e1) {
            content = null;
        }
        if (content == null) {
            logger.error("Unable to find mainContent in " + documentID);
            return;
        }
        

        // 2. Rewrite links
        try {
            replaceURLs((NodeList)
                    xPath.evaluate("/html/head/link[@type='text/css']", componentRoot, XPathConstants.NODESET),
                    "href", compDocumentGroup, compDocumentName);
            replaceURLs((NodeList)
                    xPath.evaluate("/html/body//img", componentRoot, XPathConstants.NODESET),
                    "src", compDocumentGroup, compDocumentName);
            replaceURLs((NodeList)
                    xPath.evaluate("/html/body//a", componentRoot, XPathConstants.NODESET),
                    "href", compDocumentGroup, compDocumentName);
        } catch (XPathExpressionException e) {
            logger.error("Unable to process xpath", e);
        }
        // 2. Rewrite IDs
        try {
            replaceIDs((NodeList)
                    xPath.evaluate("/html/body//*[@id != '']", componentRoot, XPathConstants.NODESET),
                    compDocumentGroup, compDocumentName);
        } catch (XPathExpressionException e) {
            logger.error("Unable to process xpath", e);
        }
        Element eContent = (Element)content;
        eContent.setAttribute("id", compDocumentGroup + "__" + compDocumentName);

        
        // 3. Copy non-html files
        copyAuxiliaryFiles (componentDocSource, buildDir, compDocumentGroup, compDocumentName);
        
        // 4. Add component to main document.
        try {
            Node baseRoot = baseDoc.getDocumentElement();
            Node body = (org.w3c.dom.Node) xPath.evaluate("/html/body", baseRoot, XPathConstants.NODE);
            Node adoptedContent = baseDoc.importNode(content, true);
            body.appendChild(adoptedContent);
        } catch (XPathExpressionException e) {
            logger.error ("xpath error: ", e);
        }
    }


    /**
     * Copy files that might be referenced by a document, prepending the document identification
     * to each copied file name.  (Flat copy only?)
     * 
     * @param sourceDir  directory from which to copy files
     * @param destDir    directory into which to copy files
     * @param baseDocumentGroup document group associated with these files
     * @param baseDocumentName  primary document associated with these files 
     */
    private void copyAuxiliaryFiles(File sourceDir, File destDir, String baseDocumentGroup,
            String baseDocumentName) {
        for (File fileToCopy: sourceDir.listFiles()) {
            if (!fileToCopy.isDirectory()) {
                String fileName = fileToCopy.getName();
                if (!fileName.equals("index.html")) {
                    if ((!fileName.endsWith(".html"))
                            || (!fileName.contains("__"))) {
                        String newFileName = baseDocumentGroup + "__" + baseDocumentName + "__"
                                + fileName;
                        File destinationFile = new File(destDir, newFileName);
                        try {
                            Files.copy(fileToCopy.toPath(), destinationFile.toPath());
                        } catch (IOException e) {
                            logger.error("Unable to copy " + fileToCopy + " to " + destinationFile, e);
                        }
                    }
                }
            }
        }
    }

    /**
     * Replace URLs in a list of nodes.
     * @param nodes list of nodes to examine
     * @param attributeName name of the attribute within which urls can be found.
     * @param thisDocumentGroup group within which this document set resides
     * @param thisDocumentName document set name
     */
    private void replaceURLs(NodeList nodes, String attributeName, 
            String thisDocumentGroup, String thisDocumentName) {
        for (int i = 0; i < nodes.getLength(); ++i) {
            Node node = nodes.item(i);
            Element el = (Element)node;
            String url = el.getAttribute(attributeName);
            String url2 = replaceURL(url, thisDocumentGroup, thisDocumentName);
            if (!url.equals(url2)) {
                el.setAttribute(attributeName, url2);
            }
        }
    }

    /**
     * Replace URLs in a list of nodes.
     * @param nodes list of nodes to examine
     * @param attributeName name of the attribute within which urls can be found.
     * @param thisDocumentGroup group within which this document set resides
     * @param thisDocumentName document set name
     */
    private void replaceIDs(NodeList nodes, String thisDocumentGroup, String thisDocumentName) {
        for (int i = 0; i < nodes.getLength(); ++i) {
            Node node = nodes.item(i);
            Element el = (Element)node;
            String id0 = el.getAttribute("id");
            String id2 = thisDocumentGroup + "__" + thisDocumentName + "__" + id0;
            el.setAttribute("id", id2);
        }
    }

    /**
     * Compute a replacement url for a link that may refer to a file that is moved in
     * the single scroll format.
     * @param url  the url to change
     * @param thisDocumentGroup group within which this document set resides
     * @param thisDocumentName document set name
     * @return  replacement value of the url
     */
    private String replaceURL(String url, String thisDocumentGroup, String thisDocumentName) {
        if (url.startsWith("../../")) {
            String url2 = url.substring(6); // strip off the ../../
            if (url2.startsWith("styles") || url2.startsWith("graphics")) {
                return url2;
            }
            int pos = url2.indexOf('/');
            if (pos < 0)
                return url;
            pos = url2.indexOf('/', pos+1);
            if (pos < 0)
                return url;
            String documentSet = url2.substring(0, pos);
            String documentSpec = url2.substring(pos+1);
            logger.info("group: " + documentSet + " within " + url);
            if (documentSpec.startsWith("index.html")) {
                // Reference to a primary document.  Is it one that we are copying into the scroll?
                if (documentsInScroll.contains(documentSet)) {
                    String url3 = "#" + documentSet.replace("/", "__");
                    pos = url2.indexOf("#");
                    if (pos >= 0) { // If original URL had an anchor, retain it.
                        url3 = url3 + "__" + url2.substring(pos+1);
                    }
                    return url3;
                } else {
                    String baseURL = properties.getProperty("baseURL");
                    return baseURL + ((baseURL.endsWith("/"))? "" : "/") + url2;
                }
            } else {
                String baseURL = properties.getProperty("baseURL");
                return baseURL + ((baseURL.endsWith("/"))? "" : "/") + url2;
            }  

        } else if (url.startsWith("../")) {
            String url2 = url.substring(3); // strip off the ../../
            int pos = url2.indexOf('/');
            if (pos < 0)
                return url;
            String documentSet = thisDocumentGroup + "/" + url2.substring(0, pos);
            String documentSpec = url2.substring(pos+1);
            logger.info("group: " + documentSet + " within " + url);
            if (documentSpec.startsWith("index.html")) {
                // Reference to a primary document.  Is it one that we are copying into the scroll?
                if (documentsInScroll.contains(documentSet)) {
                    String url3 = "#" + documentSet.replace("/", "__");
                    pos = url2.indexOf("#");
                    if (pos >= 0) { // If original URL had an anchor, retain it.
                        url3 = url3 + "__" + url2.substring(pos+1);
                    }
                    return url3;
                } else {
                    String baseURL = properties.getProperty("baseURL");
                    return baseURL + ((baseURL.endsWith("/"))? "" : "/") + thisDocumentGroup + "/" + url2;
                }
            } else {
                String baseURL = properties.getProperty("baseURL");
                return baseURL + ((baseURL.endsWith("/"))? "" : "/") + thisDocumentGroup + "/" + url2;                
            }
        } else if (url.startsWith("#")) {
            String url2 = "#" + thisDocumentGroup + "__" + thisDocumentName + "__" + url.substring(1);
            return url2;
        } else if (!url.contains("://") && (url.endsWith(".png") || url.endsWith(".gif") || url.endsWith(".jpg"))) {
            return thisDocumentGroup + "__" + thisDocumentName + "__" + url;
        }
        return url;
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


}
