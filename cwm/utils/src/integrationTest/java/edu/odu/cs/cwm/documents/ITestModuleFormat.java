package edu.odu.cs.cwm.documents;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
            "* [lecture]() [Course Websites](public:courseWebsite)",
            "* [](lecture) [TBD](public:markdown)",
            "",
            "## Assignment Submission and Grading",
            "",
            "Support for submitting assignments via the web and",
            "triggering automatic grading.",
            "",
            "* [](lecture) [Web-based Assignment Submission](../../Public/websubmit/websubmit.pdf)",
            "* [Old:](lecture) [Programming Assignments](public:assignments)", 
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
		MarkdownDocument doc = new MarkdownDocument(mdInput);
		
		String htmlContent = doc.transform(FORMAT, properties);
		
		assertTrue (htmlContent.contains("Software for Courses"));
		assertTrue (htmlContent.contains("websubmit.pdf"));
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
		assertEquals (2, modules.getLength());

	}
	

	
	
}
