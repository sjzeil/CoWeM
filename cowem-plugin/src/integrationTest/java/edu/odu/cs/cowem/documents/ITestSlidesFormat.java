/**
 * 
 */
package edu.odu.cs.cowem.documents;

import static org.junit.Assert.*;

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
public class ITestSlidesFormat {
	
	private static final String FORMAT = "slides";


		
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
        proj = new WebsiteProject(Paths.get("src/test/data/slidesTest")
                .toFile().getAbsoluteFile());
        source = 
                Paths.get("src/test/data/slidesTest/Group0/DocSet0/slides.md")
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
		MarkdownDocument doc = new MarkdownDocument(source, proj, properties);
		doc.setDebugMode(true);
		String htmlContent = doc.transform(FORMAT);
		
		
		DocumentBuilder b = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		org.w3c.dom.Document finalHtml = b.parse(new InputSource(new StringReader(htmlContent)));
		Element root = finalHtml.getDocumentElement();
		XPath xPath = XPathFactory.newInstance().newXPath();
		

		Node syllabusPar = (Node)xPath.evaluate(
                "//strong[normalize-space(.) = 'Syllabus:']", root,
                XPathConstants.NODE);
		assertNotNull(syllabusPar);
		Node syllabusPage = (Node)xPath.evaluate(
                "./ancestor::div[@class='page']", syllabusPar,
                XPathConstants.NODE);
        assertNotNull(syllabusPage);
        
        Node section1 = getElementById(finalHtml, "course-structure");
        Node section1Page = (Node)xPath.evaluate(
                "./ancestor::div[@class='page']", section1,
                XPathConstants.NODE);
        assertNotNull(section1Page);
        
        assertNotSame (syllabusPage, section1Page);
        
        Node sessions = getElementById(finalHtml, "sessions");
        Node sessionsPage = (Node)xPath.evaluate(
                "./ancestor::div[@class='page']", sessions,
                XPathConstants.NODE);
        assertNotNull(sessionsPage);
        
        assertSame(section1Page, sessionsPage);
        
        Node recitations = getElementById(finalHtml, "recitations");
        Node recitationsPage = (Node)xPath.evaluate(
                "./ancestor::div[@class='page']", recitations,
                XPathConstants.NODE);
        assertNotNull(recitationsPage);
        
        assertNotSame(recitationsPage, sessionsPage);
        
        
        Node readings = getElementById(finalHtml, "readings");
        Node readingsPage = (Node)xPath.evaluate(
                "./ancestor::div[@class='page']", readings,
                XPathConstants.NODE);
        assertNotNull(readingsPage);
        assertNotSame(recitationsPage, readingsPage);
        
	}
	

	
}
