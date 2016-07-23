/**
 * 
 */
package edu.odu.cs.cowem.documents;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Paths;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author zeil
 *
 */
public class ITestScrollFormat {
	
	private static final String FORMAT = "scroll";


		
	private String[] courseProperties = {
			"courseName",         "Course_Websites",
			"courseTitle",        "Course Website Management Tools",
					"semester",          "2016",
					"sem",               "latest",
					"instructor",        "Steven J Zeil",
					"email",             "zeil@cs.odu.edu",
					"copyright",         "2013-2016, Old Dominion Univ.",
					"delivery",  "online",
					"_online", "1"
	};
	
	private String[] documentSetProperties = {
			"_scroll", "1",
			"format", FORMAT,
			"indexFormat", FORMAT, 
			"primaryDocument", "primary.md",
		    "formats", "scroll,pages,slides,epub,directory,topics,modules,navigation"
	};
	
	

	
	
	private Properties properties;
    private WebsiteProject proj;
    private File source;
	
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		properties = new Properties();
		
		for (int i = 0; i < courseProperties.length; i += 2) {
			properties.put (courseProperties[i], courseProperties[i+1]);
		}
		for (int i = 0; i < documentSetProperties.length; i += 2) {
			properties.put (documentSetProperties[i], documentSetProperties[i+1]);
		}
        proj = new WebsiteProject(Paths.get("src/test/data/urlShortcuts")
                .toFile().getAbsoluteFile());
        source = 
                Paths.get("src/test/data/urlShortcuts/Group1/DocSet1/DocSet1.md")
                .toFile();
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

	/**
	 * Test method for {@link edu.odu.cs.cowem.documents.MarkdownDocument#transform(java.lang.String, java.util.Properties)}.
	 * @throws XPathExpressionException 
	 * @throws ParserConfigurationException 
	 * @throws IOException 
	 * @throws SAXException 
	 */
	@Test
	public void testSimpleDoc() throws XPathExpressionException, ParserConfigurationException, SAXException, IOException {
		String mdInput = String.join(System.getProperty("line.separator"),
				"Title: Title of Document", 
				"Author: John Doe",
				"Date: Jan 1, 2012",
				"",
				"A short",
				"paragraph."
				);
		MarkdownDocument doc = new MarkdownDocument(source, proj, properties, mdInput);
		
		String htmlContent = doc.transform(FORMAT);
		
		assertTrue (htmlContent.contains("John Doe"));
		assertTrue (htmlContent.contains("2012"));
		assertTrue (htmlContent.contains("A short"));
		assertTrue (htmlContent.contains("paragraph."));
		assertTrue (htmlContent.contains("\"../../styles/md-scroll.css\""));
        assertTrue (htmlContent.contains("\"../../styles/md-scroll-ext.css\""));
		
		DocumentBuilder b = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		org.w3c.dom.Document finalHtml = b.parse(new InputSource(new StringReader(htmlContent)));
		Element root = finalHtml.getDocumentElement();
		XPath xPath = XPathFactory.newInstance().newXPath();
		
		
		String actualTitle = (String)xPath.evaluate("/html/head/title", root);
		assertEquals ("Title of Document", actualTitle);

		NodeList pages = root.getElementsByTagName("page");
		assertEquals (0, pages.getLength());
		
		Node titleBlock = (Node)xPath.evaluate(
				"/html/body/div[@class='titleblock']", root,
				XPathConstants.NODE);
		assertNotNull(titleBlock);

		Node titleDiv = (Node)xPath.evaluate(
				"h1[@class='title']", titleBlock,
				XPathConstants.NODE);
		assertNotNull(titleDiv);
		assertEquals ("Title of Document", titleDiv.getTextContent());

		NodeList pars = (NodeList)xPath.evaluate(
				"/html/body/p", root,
				XPathConstants.NODESET);
		assertTrue(pars.getLength() > 0);
		boolean found = false;
		for (int i = 0; i < pars.getLength(); ++i) {
			Node p = pars.item(i);
			String pt = p.getTextContent(); 
			if (pt.contains("A short")) {
				found = true;
				assertTrue (pt.contains("paragraph"));
				break;
			}
		}
		assertTrue (found);
	}
	

	/**
	 * Test method for {@link edu.odu.cs.cowem.documents.MarkdownDocument#transform(java.lang.String, java.util.Properties)}.
	 * @throws XPathExpressionException 
	 * @throws ParserConfigurationException 
	 * @throws IOException 
	 * @throws SAXException 
	 */
	@Test
	public void testSimpleDocCSS() throws XPathExpressionException, ParserConfigurationException, SAXException, IOException {
		String mdInput = String.join(System.getProperty("line.separator"),
				"Title: Title of Document", 
				"Author: John Doe",
				"CSS: file1.css",
				"CSS: file2.css",
				"",
				"A short",
				"paragraph."
				);
		MarkdownDocument doc = new MarkdownDocument(source, proj, properties, mdInput);
		
		String htmlContent = doc.transform(FORMAT);
		
		assertTrue (htmlContent.contains("John Doe"));
		assertTrue (htmlContent.contains("file1.css"));
		assertTrue (htmlContent.contains("file2.css"));
	}
	
	   @Test
	    public void testSectionTitles() throws XPathExpressionException, ParserConfigurationException, SAXException, IOException {
	        String mdInput = String.join(System.getProperty("line.separator"),
	                "Title: Title of Document", 
	                "Author: John Doe",
	                "Date: Jan 1, 2012",
	                "",
	                "# A section title",
	                "",
	                "A short",
	                "paragraph.",
	                "",
	                "## A subsection title",
	                "",
	                "A shorter one."
	                );
	        MarkdownDocument doc = new MarkdownDocument(source, proj, properties, mdInput);
	        doc.setDebugMode(true);
	        String htmlContent = doc.transform(FORMAT);
	        
	        assertTrue (htmlContent.contains("John Doe"));
	        assertTrue (htmlContent.contains("2012"));
	        assertTrue (htmlContent.contains("A short"));
	        assertTrue (htmlContent.contains("paragraph."));
	        assertTrue (htmlContent.contains("A section title"));
            assertTrue (htmlContent.contains("A subsection title"));
            
	        assertTrue (htmlContent.contains("\"../../styles/md-scroll.css\""));
	        assertTrue (htmlContent.contains("\"../../styles/md-scroll-ext.css\""));
	        
	        DocumentBuilder b = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	        org.w3c.dom.Document finalHtml = b.parse(new InputSource(new StringReader(htmlContent)));
	        Element root = finalHtml.getDocumentElement();
	        XPath xPath = XPathFactory.newInstance().newXPath();
	        
	        
	        String actualTitle = (String)xPath.evaluate("/html/head/title", root);
	        assertEquals ("Title of Document", actualTitle);

	        NodeList pages = root.getElementsByTagName("page");
	        assertEquals (0, pages.getLength());
	        
	        Node titleBlock = (Node)xPath.evaluate(
	                "/html/body/div[@class='titleblock']", root,
	                XPathConstants.NODE);
	        assertNotNull(titleBlock);

	        Node titleDiv = (Node)xPath.evaluate(
	                "h1[@class='title']", titleBlock,
	                XPathConstants.NODE);
	        assertNotNull(titleDiv);
	        assertEquals ("Title of Document", titleDiv.getTextContent());

	        NodeList pars = (NodeList)xPath.evaluate(
	                "/html/body/p", root,
	                XPathConstants.NODESET);
	        assertTrue(pars.getLength() > 0);
	        boolean found = false;
	        for (int i = 0; i < pars.getLength(); ++i) {
	            Node p = pars.item(i);
	            String pt = p.getTextContent(); 
	            if (pt.contains("A short")) {
	                found = true;
	                assertTrue (pt.contains("paragraph"));
	                break;
	            }
	        }
	        assertTrue (found);
	    }

	
	
	
	
}
