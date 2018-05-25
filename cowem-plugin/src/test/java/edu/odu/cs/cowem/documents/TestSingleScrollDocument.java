/**
 * 
 */
package edu.odu.cs.cowem.documents;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.Properties;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author zeil
 *
 */
public class TestSingleScrollDocument {
	
	
	private Properties properties;
	private WebsiteProject proj;
	private File source;
	private File buildDir;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		properties = new Properties();
		properties.put("Title", "Title of Document");
		properties.put("baseURL", "https://webhost/dir/");
		proj = new WebsiteProject(Paths.get("src/test/data/singleScroll")
		        .toFile().getAbsoluteFile());
		Path buildPath = Paths.get("build/test/singlescroll");
		buildDir = buildPath.toFile();
		if (buildDir.exists()) {
		    clearDirectory(buildPath);
		}
        buildDir.mkdirs();
		source = 
		   Paths.get("src/test/data/urlShortcuts/Group1/DocSet1/DocSet1.md")
		   .toFile();
	}

	
	private void clearDirectory(Path dir) throws IOException {
	    Files.walkFileTree(
	            dir,
	            new SimpleFileVisitor<Path>() {
	                @Override
	                public FileVisitResult visitFile(Path file,
                            BasicFileAttributes attr) throws IOException {
	                    Files.delete(file);
	                    return FileVisitResult.CONTINUE;
	                }
                    @Override
                    public FileVisitResult postVisitDirectory(Path dir,
                            IOException exc) throws IOException {
                        Files.delete(dir);
                        return FileVisitResult.CONTINUE;
                    }
	    }
	            );
        
    }


    public Element getElementById (org.w3c.dom.Document doc, String id) {
		Element root = doc.getDocumentElement();
		XPath xPath = XPathFactory.newInstance().newXPath();
		Node n;
		try {
			n = (Node)xPath.evaluate("//*[@id='" + id + "']",
					root, XPathConstants.NODE);
		} catch (XPathExpressionException e) {
			return null;
		}
		return (Element)n;
	}

	@Test
	public void testBaseDocumentCopy() throws Exception {
	    SingleScrollDocument sdoc = new SingleScrollDocument(proj, properties, buildDir, "Directory/outline");
	    sdoc.copyBaseDocument();
	    
	    File stylesDir = new File(buildDir, "styles"); 
	    assertTrue (stylesDir.exists());
	    assertTrue (new File(stylesDir, "scroll.css").exists());
	    
        File graphicsDir = new File(buildDir, "graphics"); 
        assertTrue (graphicsDir.exists());
        assertTrue (new File(graphicsDir, "icon.png").exists());
	    
        File index = new File(buildDir, "index.html"); 
        assertFalse (index.exists()); // Should not be written out yet - still lots of work to do first.
        
        // There should be no *__*.html files in the build directory
        File[] files = buildDir.listFiles();
        for (File f: files) {
            if (f.getName().contains("__")) {
                fail ("Should not have copied " + f.getName());
            }
        }
        

        assertTrue (sdoc.toString().contains("Outline text"));
        
        org.w3c.dom.Document xmlDoc = sdoc.toXML();
        Node root = xmlDoc.getDocumentElement();
        XPath xPath = XPathFactory.newInstance().newXPath();

        Node cssLinkNode = (Node)xPath.evaluate("/html/head/link[@href='styles/scroll.css']",
                root, XPathConstants.NODE);
        assertNotNull(cssLinkNode);
	    
        Node imgNode = (Node)xPath.evaluate("/html/body//img[@src='graphics/icon.png']",
                root, XPathConstants.NODE);
        assertNotNull(imgNode);
        
        // navigation header & footer should be removed
        Node navNode = (Node)xPath.evaluate("/html/body//div[@class='navHeader']",
                root, XPathConstants.NODE);
        assertNull(navNode);
        navNode = (Node)xPath.evaluate("/html/body//div[@class='navFooter']",
                root, XPathConstants.NODE);
        assertNull(navNode);
        
        // Should find links to component documents, converted to hashes
        
        Node linkNode1 = (Node)xPath.evaluate("/html/body//a[@class='doc' AND @href='#Public__component1']",
                root, XPathConstants.NODE);
        assertNotNull(linkNode1);
        
        Node linkNode2 = (Node)xPath.evaluate("/html/body//a[@class='doc' AND @href='#Public__component2__originalAnchor']",
                root, XPathConstants.NODE);
        assertNotNull(linkNode2);

        // Links to external websites should be unchanged
        
        Node extLinkNode1 = (Node)xPath.evaluate("/html/body//a[@href='http://www.cs.odu.edu/~zeil/']",
                root, XPathConstants.NODE);
        assertNotNull(extLinkNode1);

        Node extImageNode = (Node)xPath.evaluate("/html/body//src[@src='http://www.cs.odu.edu/~zeil/zeilcs.png']",
                root, XPathConstants.NODE);
        assertNotNull(extImageNode);

        // Website-internal links should become absolute
        String websitebase = properties.getProperty("baseURL");

        Node intLinkNode1 = (Node)xPath.evaluate("/html/body//a[@href='" + websitebase + "Public/component3/foo.png" + "']",
                root, XPathConstants.NODE);
        assertNotNull(intLinkNode1);
        Node intLinkNode2 = (Node)xPath.evaluate("/html/body//a[@href='" + websitebase + "Directory/component4/foo.png" + "']",
                root, XPathConstants.NODE);
        assertNotNull(intLinkNode2);

        Node intImgNode1 = (Node)xPath.evaluate("/html/body//img[@src='" + websitebase + "Public/component3/foo.png" + "']",
                root, XPathConstants.NODE);
        assertNotNull(intImgNode1);
        Node intImgNode2 = (Node)xPath.evaluate("/html/body//img[@src='" + websitebase + "Directory/component4/foo.png" + "']",
                root, XPathConstants.NODE);
        assertNotNull(intImgNode2);

        // All IDs should now be prefixed with the document string
        String documentPrefix = "Directory__outline__";
        Node nodeWithID = (Node)xPath.evaluate("/html/body//*[@id='#" + documentPrefix + "id1" + "']",
                root, XPathConstants.NODE);
        assertNotNull(nodeWithID);
        
        // Internal links should use the longer form: 
        Node internalLink = (Node)xPath.evaluate("/html/body//a[@href='#" + documentPrefix + "id1" + "']",
                root, XPathConstants.NODE);
        assertNotNull(internalLink);
        
	}
	
    @Test
    public void testReferencedDocumentCopy() throws Exception {
        SingleScrollDocument sdoc = new SingleScrollDocument(proj, properties, buildDir, "Directory/outline");
        sdoc.copyBaseDocument();
        sdoc.copyComponentDocument("Public/component1");
        
        
        // There should be no *__*.html files in the build directory
        File[] files = buildDir.listFiles();
        for (File f: files) {
            if (f.getName().contains("__")) {
                fail ("Should not have copied " + f.getName());
            }
        }
        
        String documentText = sdoc.toString();
        assertTrue (documentText.contains("Outline text"));
        assertTrue (documentText.contains("Component 1 text"));

        org.w3c.dom.Document xmlDoc = sdoc.toXML();
        Node root = xmlDoc.getDocumentElement();
        XPath xPath = XPathFactory.newInstance().newXPath();

        Node cssLinkNode = (Node)xPath.evaluate("/html/head/link[@href='styles/scroll.css']",
                root, XPathConstants.NODE);
        assertNotNull(cssLinkNode);
        
        Node imgNode = (Node)xPath.evaluate("/html/body//img[@src='graphics/icon.png']",
                root, XPathConstants.NODE);
        assertNotNull(imgNode);
        
        // navigation header & footer should be removed
        Node navNode = (Node)xPath.evaluate("/html/body//div[@class='navHeader']",
                root, XPathConstants.NODE);
        assertNull(navNode);
        navNode = (Node)xPath.evaluate("/html/body//div[@class='navFooter']",
                root, XPathConstants.NODE);
        assertNull(navNode);
        
        // Should find links to component documents, converted to hashes
        
        Node linkNode1 = (Node)xPath.evaluate("/html/body//a[@class='doc' AND @href='#Public__component1']",
                root, XPathConstants.NODE);
        assertNotNull(linkNode1);
        
        Node linkNode2 = (Node)xPath.evaluate("/html/body//a[@class='doc' AND @href='#Public__component2__originalAnchor']",
                root, XPathConstants.NODE);
        assertNotNull(linkNode2);

        Node linkNode3 = (Node)xPath.evaluate("/html/body//a[@class='doc' AND @href='#Directory__outline']",
                root, XPathConstants.NODE);
        assertNotNull(linkNode3);

        // Links to external websites should be unchanged
        
        Node extLinkNode1 = (Node)xPath.evaluate("/html/body//a[@href='http://odu.edu/compsci/academics']",
                root, XPathConstants.NODE);
        assertNotNull(extLinkNode1);

        Node extImageNode = (Node)xPath.evaluate("/html/body//src[@src='https://www.cs.odu.edu/~zeil/cs252/sum18/graphics/home.png']",
                root, XPathConstants.NODE);
        assertNotNull(extImageNode);

        // Website-internal links should become absolute
        String websitebase = properties.getProperty("baseURL");

        Node intLinkNode1 = (Node)xPath.evaluate("/html/body//a[@href='" + websitebase + "Public/component5/foo.png" + "']",
                root, XPathConstants.NODE);
        assertNotNull(intLinkNode1);
        Node intLinkNode2 = (Node)xPath.evaluate("/html/body//a[@href='" + websitebase + "Directory/component6/foo.png" + "']",
                root, XPathConstants.NODE);
        assertNotNull(intLinkNode2);

        Node intImgNode1 = (Node)xPath.evaluate("/html/body//img[@src='" + websitebase + "Public/component5/foo.png" + "']",
                root, XPathConstants.NODE);
        assertNotNull(intImgNode1);
        Node intImgNode2 = (Node)xPath.evaluate("/html/body//img[@src='" + websitebase + "Directory/component6/foo.png" + "']",
                root, XPathConstants.NODE);
        assertNotNull(intImgNode2);

        // All IDs should now be prefixed with the document string
        String documentPrefix = "Public__component1__";
        Node nodeWithID = (Node)xPath.evaluate("/html/body//*[@id='#" + documentPrefix + "id2" + "']",
                root, XPathConstants.NODE);
        assertNotNull(nodeWithID);
        
        // Internal links should use the longer form: 
        Node internalLink = (Node)xPath.evaluate("/html/body//a[@href='#" + documentPrefix + "id2" + "']",
                root, XPathConstants.NODE);
        assertNotNull(internalLink);
        
    }
	
	
}