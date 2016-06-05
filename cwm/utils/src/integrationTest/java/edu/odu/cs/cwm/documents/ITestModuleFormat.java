package edu.odu.cs.cwm.documents;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.StringReader;
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
public class ITestModuleFormat {
	
	private static final String FORMAT = "modules";


		
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
			"_html", "1",
			"format", FORMAT,
			"indexFormat", FORMAT, 
			"primaryDocument", "primary.md",
		    "formats", "html,pages,slides,epub,directory,topics,modules,navigation"
	};
	
	
    private String[] schedule_md = {
            "Title: @courseName@ Outline",
            "Author: @semester@",
            "",
            "# Software for Courses",
            "",
            "## Course Websites & Documents",
            "",
            "**Overview**",
            "",
            "The **Course Website Manager** is a software package",
            "that allows creation of a course website containing slides and",
            "web pages, with all content originally written",
            "in Markdown.",
            "",
            "---",
            "",
            "**Subject [Note](note1)**",
            "",
            "1. Look at the [Course Websites](item1)",
            "2. [Old:](lecture) [TBD](item2)",
            "",
            "## Assignment Submission and Grading",
            "",
            "Support for submitting assignments via the web and",
            "triggering automatic grading.",
            "",
            "3. [ ](lecture) [Web-based Assignment Submission](item3)",
            "4. [ ](lab) [Programming Assignments](item4)", 
            "",
            "# Preamble",
            "",
            "preamble text",
            "",
            "# Postscript",
            "",
            "All times in this schedule are given in Eastern Time.",
            "",
            "# Presentation",
            "",
            "| Topics | Lecture Notes | Readings | Assignments & Other Events |",
            "|--------|---------------|----------|----------------------------|",
            "| topics | slides video lecturenotes construct | text | quiz asst selfassess lecture exam event |",
            "",
            "| Document Kind | Prefix |",
            "|---------------|--------|",
            "| lecture          | Read chapters |",
            "| lab           | In the lab:   |",
            ""              
      
    };
	
	
	private Properties properties;
	
	
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
	 * Test method for {@link edu.odu.cs.cwm.documents.MarkdownDocument#transform(java.lang.String, java.util.Properties)}.
	 * @throws XPathExpressionException 
	 * @throws ParserConfigurationException 
	 * @throws IOException 
	 * @throws SAXException 
	 */
	@Test
	public void testSimpleDoc() throws XPathExpressionException, ParserConfigurationException, SAXException, IOException {
		String mdInput = String.join(System.getProperty("line.separator"),
		        schedule_md);
		MarkdownDocument doc = new MarkdownDocument(mdInput, properties, 2);
		
		String htmlContent = doc.transform(FORMAT);
		
		assertTrue (htmlContent.contains("Software for Courses"));
		assertFalse(htmlContent.contains("Preamble"));
        assertTrue (htmlContent.contains("preamble text"));
        assertFalse(htmlContent.contains("Postscript"));
        assertTrue (htmlContent.contains("Eastern"));
        assertFalse(htmlContent.contains("Presentation"));
        assertFalse (htmlContent.contains("@courseName@"));
        assertFalse (htmlContent.contains("@Title@"));
		
		DocumentBuilder b = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		org.w3c.dom.Document finalHtml = b.parse(new InputSource(new StringReader(htmlContent)));
		Element root = finalHtml.getDocumentElement();
		XPath xPath = XPathFactory.newInstance().newXPath();
		
		
		
		NodeList modules = (NodeList)xPath.evaluate(
				"/html/body//div[@class='module']", root,
				XPathConstants.NODESET);
		assertEquals (3, modules.getLength());

	}
	

    @Test
    public void testIconInsertion() throws XPathExpressionException, ParserConfigurationException, SAXException, IOException {
        String mdInput = String.join(System.getProperty("line.separator"),
                schedule_md);
        MarkdownDocument doc = new MarkdownDocument(mdInput, properties, 2);
        doc.setDebugMode(true);
        
        String htmlContent = doc.transform(FORMAT);
        
        
        DocumentBuilder b = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        org.w3c.dom.Document finalHtml = b.parse(new InputSource(new StringReader(htmlContent)));
        Element root = finalHtml.getDocumentElement();
        XPath xPath = XPathFactory.newInstance().newXPath();
        
        
        
        Node item1 = (Node) xPath.evaluate(
                "/html/body//a[@href='item1']", root,
                XPathConstants.NODE);
        assertNotNull(item1);
        Node item2 = (Node) xPath.evaluate(
                "/html/body//a[@href='item2']", root,
                XPathConstants.NODE);
        assertNotNull(item2);
        Node item3 = (Node) xPath.evaluate(
                "/html/body//a[@href='item3']", root,
                XPathConstants.NODE);
        assertNotNull(item3);
        Node item4 = (Node) xPath.evaluate(
                "/html/body//a[@href='item4']", root,
                XPathConstants.NODE);
        assertNotNull(item4);
        
        Element icon1 = (Element) xPath.evaluate(
                "./ancestor::li//img[1]", item1,
                XPathConstants.NODE);
        assertNull(icon1);
        
        Element icon2 = (Element) xPath.evaluate(
                "./ancestor::li//img[1]", item2,
                XPathConstants.NODE);
        assertNotNull(icon2);
        assertTrue(icon2.getAttribute("src").endsWith("lecture.png"));

        Element icon3 = (Element) xPath.evaluate(
                "./ancestor::li//img[1]", item3,
                XPathConstants.NODE);
        assertNotNull(icon3);
        assertTrue(icon3.getAttribute("src").endsWith("lecture.png"));

        Element icon4 = (Element) xPath.evaluate(
                "./ancestor::li//img[1]", item4,
                XPathConstants.NODE);
        assertNotNull(icon4);
        assertTrue(icon4.getAttribute("src").endsWith("lab.png"));
    
    }
	

    
    
    @Test
    public void testPrefixInsertion() throws XPathExpressionException, ParserConfigurationException, SAXException, IOException {
        String mdInput = String.join(System.getProperty("line.separator"),
                schedule_md);
        MarkdownDocument doc = new MarkdownDocument(mdInput, properties, 2);
        doc.setDebugMode(true);
        
        String htmlContent = doc.transform(FORMAT);
        
        
        DocumentBuilder b = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        org.w3c.dom.Document finalHtml = b.parse(new InputSource(new StringReader(htmlContent)));
        Element root = finalHtml.getDocumentElement();
        XPath xPath = XPathFactory.newInstance().newXPath();
        
        
        
        Node item1 = (Node) xPath.evaluate(
                "/html/body//a[@href='item1']/..", root,
                XPathConstants.NODE);
        assertNotNull(item1);
        Node item2 = (Node) xPath.evaluate(
                "/html/body//a[@href='item2']/..", root,
                XPathConstants.NODE);
        assertNotNull(item2);
        Node item3 = (Node) xPath.evaluate(
                "/html/body//a[@href='item3']/..", root,
                XPathConstants.NODE);
        assertNotNull(item3);
        Node item4 = (Node) xPath.evaluate(
                "/html/body//a[@href='item4']/..", root,
                XPathConstants.NODE);
        assertNotNull(item4);
        
        assertTrue (item2.getTextContent().contains("Old: "));
        assertTrue (item3.getTextContent().contains("Read chapters "));
        assertTrue (item4.getTextContent().contains("In the lab: "));
    
    }

    
    
    
}
