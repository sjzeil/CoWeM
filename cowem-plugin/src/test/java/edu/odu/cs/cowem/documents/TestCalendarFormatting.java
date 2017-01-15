/**
 * 
 */
package edu.odu.cs.cowem.documents;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
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

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author zeil
 *
 */
public class TestCalendarFormatting {

	private static final String FORMAT = "calendar";

	public String lastTransformed;
	


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

	private String[] metadataProperties = {
			"TOC", "1",
			"Title", "A Title",
			"Author", "Jane Author",
			"Date", "2016",
			"CSS", "file1.css,file2.css",
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

	@Test
	public void testSimpleDates() throws XPathExpressionException, TransformerException, ParserConfigurationException, SAXException, IOException {

		String[] htmlInput = {
				"<html>",
				"<head>",
				"<title>A Title</title>",
				"</head>",
				"<body>",
				"<h1 id='h11'>Section 1</h1>",
				"<ul>",
				
                "<li id='li1'> A <a href='date:'>2016-01-02T07:30</a> date</li>",
                "<li id='li2'> A <a href='date:'>2016-01-03</a> date</li>",
                "<li id='li3'>item3</li>",
				"</ul>",
				"<h1 id='h12'>Section 2</h1>",
				"<h2 id='h21'>Subsection 1</h2>",
				"<ul>",
                "<li id='li4'> A <a href='due:'>2020-01-01</a> date</li>",
				"<li id='li5'>item5</li>",
				"</ul>",
				"<h2 id='h22'>Subsection 2</h2>",
				"<ul>",
				"<li id='li6'>item6</li>",
				"</ul>",
				"</body>",
				"</html>"	
		};


		org.w3c.dom.Document basicHtml = formatHTML (htmlInput); 
		Element root = basicHtml.getDocumentElement();
		String htmlContent = root.getTextContent();

		assertFalse (htmlContent.contains("Section 1"));
		
		XPath xPath = XPathFactory.newInstance().newXPath();

		assertEquals ("html", root.getLocalName());

		
		Element item1 = getElementById(basicHtml, "li1");
		assertNotNull (item1);
		Node container = item1.getParentNode();
		
		Element item2 = getElementById(basicHtml, "li2");
		assertNotNull (item2);
		assertSame (container, item2.getParentNode());
		
		Element item3 = getElementById(basicHtml, "li3");
		assertNull (item3);
		Element item4 = getElementById(basicHtml, "li4");
		assertNotNull (item4);
        assertSame (container, item2.getParentNode());

        Element item5 = getElementById(basicHtml, "li5");
		assertNull (item5);
		
		assertEquals("2016-01-02T07:30:00", item1.getAttribute("start"));
		assertEquals("2016-01-02T07:30:59", item1.getAttribute("stop"));
        
		assertEquals("2016-01-03T00:00:00", item2.getAttribute("start"));
		assertEquals("2016-01-03T23:59:59", item2.getAttribute("stop"));
        
        assertEquals("2020-01-01T23:59:00", item4.getAttribute("start"));
        assertEquals("2020-01-01T23:59:59", item4.getAttribute("stop"));
	}


	@Test
	public void testEndingDates() throws XPathExpressionException, TransformerException, ParserConfigurationException, SAXException, IOException {

		String[] htmlInput = {
				"<html>",
				"<head>",
				"<title>A Title</title>",
				"</head>",
				"<body>",
				"<h1 id='h11'>Section 1</h1>",
				"<ul>",
                "<li id='li1'> A <a href='date:'>2016-01-02T07:30</a> <a href='enddate:'>2016-01-05T08:50</a>date </li>",
                "<li id='li2'> A <a href='date:'>2016-01-03</a> <a href='enddate:'>2016-01-04</a>date</li>",
                "<li id='li3'> A <a href='date:'>2016-01-04T13:45</a> <a href='due:'>2016-01-04T14:20</a> date</li>",

                
				"</ul>",
				"<h1 id='h12'>Section 2</h1>",
				"<h2 id='h21'>Subsection 1</h2>",
				"<ul>",
				"<li id='li4'>item4</li>",
				"<li id='li5'>item5</li>",
				"</ul>",
				"<h2 id='h22'>Subsection 2</h2>",
				"<ul>",
				"<li id='li6'>item6</li>",
				"</ul>",
				"<h1 id='preamble'>Preamble</h1>",
				"<p id='p1'>preamble text</p>",
				"<h1 id='postscript'>Postscript</h1>",
				"<p id='p2'>postscript text</p>",
				"<h1 id='presentation'>Presentation</h1>",
				"<p id='p4'>presentation text</p>",
				"</body>",
				"</html>"	
		};


        org.w3c.dom.Document basicHtml = formatHTML (htmlInput); 
        Element root = basicHtml.getDocumentElement();
        String htmlContent = root.getTextContent();

        assertFalse (htmlContent.contains("Section 1"));
        
        XPath xPath = XPathFactory.newInstance().newXPath();

        assertEquals ("html", root.getLocalName());

        
        Element item1 = getElementById(basicHtml, "li1");
        assertNotNull (item1);
        Node container = item1.getParentNode();
        
        Element item2 = getElementById(basicHtml, "li2");
        assertNotNull (item2);
        assertSame (container, item2.getParentNode());
        
        Element item3 = getElementById(basicHtml, "li3");
        assertNotNull (item3);
        assertSame (container, item3.getParentNode());
        
        Element item4 = getElementById(basicHtml, "li4");
        assertNull (item4);

        Element item5 = getElementById(basicHtml, "li5");
        assertNull (item5);
        
        assertEquals("2016-01-02T07:30:00", item1.getAttribute("start"));
        assertEquals("2016-01-05T08:50:59", item1.getAttribute("stop"));
        
        assertEquals("2016-01-03T00:00:00", item2.getAttribute("start"));
        assertEquals("2016-01-04T23:59:59", item2.getAttribute("stop"));
        
        assertEquals("2016-01-04T13:45:00", item3.getAttribute("start"));
        assertEquals("2016-01-04T14:20:59", item3.getAttribute("stop"));
	}


	
	
	
	private org.w3c.dom.Document formatHTML(String[] htmlInput) 
			throws TransformerException, ParserConfigurationException, SAXException, IOException {
		org.w3c.dom.Document formattedDoc = null;
		Path formatConversionSheetFile = Paths.get("src", "main", "resources",
				"edu", "odu", "cs", "cowem", "templates", "md-" + FORMAT + ".xsl");

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
		
		lastTransformed = xmlToString(formattedDoc);
		
		return formattedDoc;			
	}


	private String xmlToString(Document inputDoc) {
		System.setProperty("javax.xml.transform.TransformerFactory", 
				"net.sf.saxon.TransformerFactoryImpl"); 
		TransformerFactory transFact = TransformerFactory.newInstance();
		try {
		Transformer xform = transFact.newTransformer();

		Source xmlIn = new DOMSource(inputDoc);
		StringWriter output = new StringWriter(); 
		Result htmlOut = new StreamResult(output);
		xform.transform(xmlIn, htmlOut);
		return output.toString();
		} catch (Exception e) {
			return "<error>Could not render result due to: " + e.toString() + "</error>\n";
		}
	}



}
