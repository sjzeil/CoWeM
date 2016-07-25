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
		        schedule_md);
        MarkdownDocument doc = new MarkdownDocument(source, proj, properties, mdInput);
		
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
        MarkdownDocument doc = new MarkdownDocument(source, proj, properties, mdInput);
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
        assertTrue(icon2.getAttribute("src").endsWith("lecture-kind.png"));

        Element icon3 = (Element) xPath.evaluate(
                "./ancestor::li//img[1]", item3,
                XPathConstants.NODE);
        assertNotNull(icon3);
        assertTrue(icon3.getAttribute("src").endsWith("lecture-kind.png"));

        Element icon4 = (Element) xPath.evaluate(
                "./ancestor::li//img[1]", item4,
                XPathConstants.NODE);
        assertNotNull(icon4);
        assertTrue(icon4.getAttribute("src").endsWith("lab-kind.png"));
    
    }
	

    
    
    @Test
    public void testPrefixInsertion() throws XPathExpressionException, ParserConfigurationException, SAXException, IOException {
        String mdInput = String.join(System.getProperty("line.separator"),
                schedule_md);
        MarkdownDocument doc = new MarkdownDocument(source, proj, properties, mdInput);
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

    /**
     * Regression test for a mistake in processing large outlines.
     * @throws XPathExpressionException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    @Test
    public void test333Outline() throws XPathExpressionException, ParserConfigurationException, SAXException, IOException {
        File mdInput = Paths.get("src/test/data/proj1/group1/outline333/outline333.md").toFile();
        WebsiteProject proj = new WebsiteProject(Paths.get("src/test/data/proj1")
                .toFile().getAbsoluteFile());
        MarkdownDocument doc = new MarkdownDocument(mdInput, proj, properties);
        doc.setDebugMode(true);
        
        String htmlContent = doc.transform(FORMAT);
        
        assertFalse(htmlContent.contains("Preamble"));
        assertFalse(htmlContent.contains("Postscript"));
        assertFalse(htmlContent.contains("Presentation"));
        
        DocumentBuilder b = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        org.w3c.dom.Document finalHtml = b.parse(new InputSource(new StringReader(htmlContent)));
        Element root = finalHtml.getDocumentElement();
        XPath xPath = XPathFactory.newInstance().newXPath();
        

        int[] subsectionsExpected = {0, 5, 9, 9};
        
        NodeList sections = (NodeList)xPath.evaluate(
                "/html/body//div[@class='topic1']", root,
                XPathConstants.NODESET);
        assertEquals (4, sections.getLength());
        for (int i = 0; i < sections.getLength(); ++i) {
            Node div = sections.item(i);
            String content = div.getTextContent();
            String number = Integer.toString(i+1);
            assertTrue("Did not find " + number + " in " + content,
                    content.startsWith(number));
        }

        NodeList subsections = (NodeList)xPath.evaluate(
                "/html/body//div[@class='topic2' or @class='topic1']", root,
                XPathConstants.NODESET);
        int m = 0;
        for (int i = 0; i < sections.getLength(); ++i) {
            Node div = subsections.item(m);
            String content = div.getTextContent();
            String number = Integer.toString(i+1) + " ";
            assertTrue("Did not find " + number + " in " + content,
                    content.startsWith(number));
            ++m;
            for (int j = 0; j < subsectionsExpected[i]; ++j) {
                div = subsections.item(m);
                content = div.getTextContent();
                number = Integer.toString(i+1) 
                        + "." + Integer.toString(j+1);
                assertTrue("Did not find " + number + " in " + content,
                        content.startsWith(number));
                ++m;
            }
        }

        
        int[] subsubsectionsExpected = {0, 3, 0, 0, 2};
        NodeList subsubsections = (NodeList)xPath.evaluate(
                "/html/body//div[@class='topic3']", root,
                XPathConstants.NODESET);
        m = 0;
        for (int i = 0; i < sections.getLength(); ++i) {
            for (int j = 0; j < subsectionsExpected[i]; ++j) {
                for (int k = 0; k < subsubsectionsExpected[m]; ++k) {
                    Node div = subsubsections.item(m);
                    String content = div.getTextContent();
                    String number = Integer.toString(i+1) 
                            + "." + Integer.toString(j+1)
                            + "." + Integer.toString(k+1);
                    assertTrue("Did not find " + number + " in " + content,
                            content.startsWith(number));
                    ++m;
                }
            }
        }
        
        
    }
     
    
    
}
