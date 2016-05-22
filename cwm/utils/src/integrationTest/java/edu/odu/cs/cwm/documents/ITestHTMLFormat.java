/**
 * 
 */
package edu.odu.cs.cwm.documents;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.Reader;
import java.io.StringBufferInputStream;
import java.io.StringReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.junit.After;
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
public class ITestHTMLFormat {

	private String[] deferredSubsitutions = {
			"MathJaxURL","highlightjsURL", "slidyURL", 
			"stylesURL", "graphicsURL", "baseURL", "homeURL"
	};
		
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
			"format", "html",
			"indexFormat", "html", 
			"primaryDocument", "primary.md",
		    "formats", "html,pages,slides,epub,directory,topics,modules,navigation"
	};
	
	

	private String mdInput = "Title: Title of Document\n"
			+ "Author: John Doe\n"
			+ "Date: Jan 1, 2012\n"
			+ "TOC: yes\n"
			+ "Macros: macro1.md\n"
			+ "CSS: test1.css\n"
			+ "Macros: macro2.md\n"
			+ "CSS: test2.css\n"
			+ "\n# Section 1\n\n"
			+ "%if _includeThis\n"
			+ "## Section 1.1\n\n"
			+ "A paragraph in\nsection 1.1\n\n"
			+ "%endif\n\n"
			+ "## Section 1.2\n\n"
			+ "A paragraph in\nsection 1.2\n\n"
			+ "Something in _italics_ and\n"
			+ "something else in **bold**\n"
			+ "and \\em{even\nemphasized}.\n";
	
	private String preProcessed1 =
			"\n# Section 1\n\n\n"
			+ "## Section 1.1\n\n"
			+ "A paragraph in\nsection 1.1\n\n"
			+ "\n\n"
			+ "## Section 1.2\n\n"
			+ "A paragraph in\nsection 1.2\n\n"
			+ "Something in _italics_ and\n"
			+ "something else in **bold**\n"
			+ "and <em>even\nemphasized</em>.\n";
	
	private String preProcessed2 = 
			"\n# Section 1\n\n"
			+ "\n\n"
			+ "## Section 1.2\n\n"
			+ "A paragraph in\nsection 1.2\n\n"
			+ "Something in _italics_ and\n"
			+ "something else in **bold**\n"
			+ "and <em>even\nemphasized</em>.\n";
	
	
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
	 */
	@Test
	public void testSimpleDoc() throws XPathExpressionException {
		String mdInput = String.join(System.getProperty("line.separator"),
				"Title: Title of Document", 
				"Author: John Doe",
				"Date: Jan 1, 2012",
				"",
				"A short",
				"paragraph."
				);
		MarkdownDocument doc = new MarkdownDocument(mdInput);
		
		String htmlContent = doc.transform("html", properties);
		
		assertTrue (htmlContent.contains("John Doe"));
		assertTrue (htmlContent.contains("2012"));
		assertTrue (htmlContent.contains("a short"));
		assertTrue (htmlContent.contains("paragraph."));
		
		org.w3c.dom.Document basicHtml = doc.process(preProcessed1);
		Element root = basicHtml.getDocumentElement();
		XPath xPath = XPathFactory.newInstance().newXPath();
		
		assertEquals ("html", root.getLocalName());
		
		String actualTitle = (String)xPath.evaluate("/html/head/title", root);
		assertEquals ("Title of Document", actualTitle);

		NodeList pages = root.getElementsByTagName("page");
		assertEquals (0, pages.getLength());
		
		Node titleBlock = (Node)xPath.evaluate(
				"/html/body/div[@class='titleblock']", root,
				XPathConstants.NODE);
		assertNotNull(titleBlock);

		Node titleDiv = (Node)xPath.evaluate(
				"div[@class='title']", titleBlock,
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
			if (pt.contains("a short")) {
				found = true;
				assertTrue (pt.contains("paragraph"));
				break;
			}
		}
		assertTrue (found);
	}
	

	
	
}