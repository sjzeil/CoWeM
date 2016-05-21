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
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author zeil
 *
 */
public class TestHTMLFormatting {

	private static final String FORMAT = "html";

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

	private String[] metadataProperties = {
			"meta_TOC", "1",
			"meta_Title", "The Title",
			"meta_Author", "Jane Author",
			"meta_Date", "2016",
			"meta_CSS", "file1.css,file2.css",
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

		for (int i = 0; i < metadataProperties.length; i += 2) {
			properties.put (metadataProperties[i], metadataProperties[i+1]);
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
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 * @throws TransformerException 
	 */
	@Test
	public void testSimpleDoc() throws XPathExpressionException, TransformerException, ParserConfigurationException, SAXException, IOException {
		String titleString = "A Title";
		String line1 = "A short";
		String line2 = "paragraph";
		
		String[] htmlInput = {
				"<html>",
				"<head>",
				"<title>" + titleString + "</title>",
				"</head>",
				"<body>",
				"<p>" + line1,
				line2 + "</p></body>",
				"</html>"	
		};


		org.w3c.dom.Document basicHtml = formatHTML (htmlInput); 
		Element root = basicHtml.getDocumentElement();
		String htmlContent = root.getTextContent();

		assertTrue (htmlContent.contains(titleString));
		assertTrue (htmlContent.contains(line1));
		assertTrue (htmlContent.contains(line2));

		XPath xPath = XPathFactory.newInstance().newXPath();

		assertEquals ("html", root.getLocalName());

		String actualTitle = (String)xPath.evaluate("/html/head/title", root);
		assertEquals (titleString, actualTitle);

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
		assertEquals (titleString, titleDiv.getTextContent());

		NodeList pars = (NodeList)xPath.evaluate(
				"/html/body/p", root,
				XPathConstants.NODESET);
		assertTrue(pars.getLength() > 0);
		boolean found = false;
		for (int i = 0; i < pars.getLength(); ++i) {
			Node p = pars.item(i);
			String pt = p.getTextContent(); 
			if (pt.contains(line1)) {
				found = true;
				assertTrue (pt.contains(line2));
				break;
			}
		}
		assertTrue (found);
	}


	private org.w3c.dom.Document formatHTML(String[] htmlInput) 
			throws TransformerException, ParserConfigurationException, SAXException, IOException {
		org.w3c.dom.Document formattedDoc = null;
		Path formatConversionSheetFile = Paths.get("src", "main", "resources",
				"edu", "odu", "cs", "cwm", "templates", "md-" + FORMAT + ".xsl");

		System.setProperty("javax.xml.transform.TransformerFactory", 
				"net.sf.saxon.TransformerFactoryImpl"); 
		TransformerFactory transFact = TransformerFactory.newInstance();

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		org.w3c.dom.Document inputDoc = dBuilder.parse(
				new InputSource(
						new StringReader(
								String.join(System.getProperty("line.separator"),
										htmlInput))));

		Source xslSource = new StreamSource(formatConversionSheetFile.toFile());
		formattedDoc = dBuilder.newDocument();
		Templates template = transFact.newTemplates(xslSource);
		Transformer xform = template.newTransformer();
		xform.setParameter("format", FORMAT);
		for (Object okey: properties.keySet()) {
			String key = okey.toString();
			xform.setParameter(key, properties.getProperty(key));
		}

		Source xmlIn = new DOMSource(inputDoc);
		DOMResult htmlOut = new DOMResult(formattedDoc);
		xform.transform(xmlIn, htmlOut);
		return formattedDoc;			
	}


	private org.w3c.dom.Document parseHTML (String htmlText) 
			throws ParserConfigurationException, SAXException, IOException {
		org.w3c.dom.Document basicHtml = null;
		DocumentBuilder b 
		= DocumentBuilderFactory.newInstance().newDocumentBuilder();
		basicHtml = b.parse(new InputSource(new StringReader(htmlText)));
		return basicHtml;
	}


}