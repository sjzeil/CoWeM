/**
 * 
 */
package edu.odu.cs.cowem.documents;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
public class TestLMSFormatting {

	private static final String FORMAT = "LMS";

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
	@BeforeEach
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
		XPath xPath = new net.sf.saxon.xpath.XPathFactoryImpl().newXPath();
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
	public void testSimpleDoc() throws XPathExpressionException, TransformerException, ParserConfigurationException, SAXException, IOException {

		String[] htmlInput = {
				"<html>",
				"<head>",
				"<title>A Title</title>",
				"</head>",
				"<body>",
				"<h1 id='h11'>Section 1</h1>",
				"<ul>",
				"<li id='li1'>item1</li>",
				"<li id='li2'>item2</li>",
				"</ul>",
				"<h1 id='h12'>Section 2</h1>",
				"<h2 id='h21'>Subsection 1</h2>",
				"<ul>",
				"<li id='li3'>item3</li>",
				"<li id='li4'>item4</li>",
				"</ul>",
				"<h2 id='h22'>Subsection 2</h2>",
				"<ul>",
				"<li id='li5'>item5</li>",
				"</ul>",
				"</body>",
				"</html>"	
		};


		org.w3c.dom.Document basicHtml = formatHTML (htmlInput); 
		Element root = basicHtml.getDocumentElement();
		String htmlContent = root.getTextContent();

		assertFalse (htmlContent.contains("Section 1"));
		
		XPath xPath = new net.sf.saxon.xpath.XPathFactoryImpl().newXPath();

		assertEquals ("html", root.getLocalName());

		
		Element item1 = getElementById(basicHtml, "li1");
		assertNotNull (item1);
		Element item2 = getElementById(basicHtml, "li2");
		assertNotNull (item2);
		Element item3 = getElementById(basicHtml, "li3");
		assertNotNull (item3);
		Element item4 = getElementById(basicHtml, "li4");
		assertNotNull (item4);
		Element item5 = getElementById(basicHtml, "li5");
		assertNotNull (item5);
		
		Element moduleOf1 = (Element)xPath.evaluate("./ancestor::div[@class='moduleActivities']", item1, XPathConstants.NODE);
		assertNotNull (moduleOf1);
		assertEquals("activities1", moduleOf1.getAttribute("id"));
		Element moduleOf2 = (Element)xPath.evaluate("./ancestor::div[@class='moduleActivities']", item2, XPathConstants.NODE);
		assertNotNull (moduleOf2);
        assertEquals("activities1", moduleOf2.getAttribute("id"));
		Element moduleOf3 = (Element)xPath.evaluate("./ancestor::div[@class='moduleActivities']", item3, XPathConstants.NODE);
		assertNotNull (moduleOf3);
        assertEquals("activities2", moduleOf3.getAttribute("id"));
		Element moduleOf4 = (Element)xPath.evaluate("./ancestor::div[@class='moduleActivities']", item4, XPathConstants.NODE);
		assertNotNull (moduleOf4);
        assertEquals("activities2", moduleOf4.getAttribute("id"));
		Element moduleOf5 = (Element)xPath.evaluate("./ancestor::div[@class='moduleActivities']", item5, XPathConstants.NODE);
		assertNotNull (moduleOf5);
        assertEquals("activities3", moduleOf5.getAttribute("id"));

		assertSame(moduleOf1, moduleOf2);
		assertNotSame(moduleOf2, moduleOf3);
		assertSame(moduleOf3, moduleOf4);
		assertNotSame(moduleOf4, moduleOf5);
		
	}


	@Test
	public void testPreambleAndPostScript() throws XPathExpressionException, TransformerException, ParserConfigurationException, SAXException, IOException {

		String[] htmlInput = {
				"<html>",
				"<head>",
				"<title>A Title</title>",
				"</head>",
				"<body>",
				"<h1 id='h11'>Section 1</h1>",
				"<ul>",
				"<li id='li1'>item1</li>",
				"<li id='li2'>item2</li>",
				"</ul>",
				"<h1 id='h12'>Section 2</h1>",
				"<h2 id='h21'>Subsection 1</h2>",
				"<ul>",
				"<li id='li3'>item3</li>",
				"<li id='li4'>item4</li>",
				"</ul>",
				"<h2 id='h22'>Subsection 2</h2>",
				"<ul>",
				"<li id='li5'>item5</li>",
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
		assertFalse (htmlContent.contains("presentation text"));
		assertFalse (htmlContent.contains("preamble text"));
		assertFalse (htmlContent.contains("Preamble"));
		assertFalse (htmlContent.contains("postscript text"));
		assertFalse (htmlContent.contains("Postscript"));

	}

	
    @Test
    public void testSectioning2() 
            throws XPathExpressionException, TransformerException,
            ParserConfigurationException, SAXException, IOException {
        
        String[] htmlInput = {
                "<html>",
                "<head>",
                "<title>Document Title</title>",
                "</head>",
                "<body>",
                "<p id='par1'>A preamble</p>",           // section 0, depth 1
                "<h1 id='h11'>Title 1</h1>",             // section 1, depth 1
                "   <h2 id='h21'>Subtitle 1</h2>",        // section 2, depth 2
                "      <p id='par2'>Some text</p>",
                "      <p id='par3'>Some text</p>",
                "      <hr/>",
                "      <p id='par4'>Some text</p>",      
                "   <h2 id='h22'>Subtitle 2</h2>",        // section 3, depth 2    
                "      <h3 id='h31'>Subsubtitle 1</h3>", // section 4, depth 3
                "         <p id='par5'>Some text</p>",
                "      <h3 id='h32'>Subsubtitle 2</h3>", // section 5, depth 3
                "         <p id='par6'>Some text</p>",
                "<h1 id='h12'>Title 2</h1>",             // section 6, depth 1
                "   <p id='par7'>Closing text</p>",
                "</body>",
                "</html>"   
        };


        org.w3c.dom.Document basicHtml = formatHTML (htmlInput); 
        Element root = basicHtml.getDocumentElement();
        String htmlContent = root.getTextContent();

        assertFalse (htmlContent.contains("A preamble"));
        assertFalse (htmlContent.contains("Subsubtitle 2"));
        assertTrue (htmlContent.contains("Closing text"));

        XPath xPath = new net.sf.saxon.xpath.XPathFactoryImpl().newXPath();
        assertEquals ("html", root.getLocalName());
        

        Node par1 = getElementById(basicHtml, "par1");
        assertNull (par1);
        
        Node par5 = getElementById(basicHtml, "par5");
        Element moduleOf5 = (Element)xPath.evaluate("./ancestor::div[@class='moduleActivities']",par5, XPathConstants.NODE);
        assertNotNull (moduleOf5);
        assertEquals("activities2", moduleOf5.getAttribute("id"));

        
        Node par3 = getElementById(basicHtml, "par3");
        Element moduleOf3 = (Element)xPath.evaluate("./ancestor::div[@class='moduleDescription']",par3, XPathConstants.NODE);
        assertNotNull (moduleOf3);
        assertEquals("overview1", moduleOf3.getAttribute("id"));

        Node par4 = getElementById(basicHtml, "par4");
        Element moduleOf4 = (Element)xPath.evaluate("./ancestor::div[@class='moduleActivities']",par4, XPathConstants.NODE);
        assertNotNull (moduleOf4);
        assertEquals("activities1", moduleOf4.getAttribute("id"));

    }
	

	@Test
	public void testItemtagging() throws XPathExpressionException, TransformerException, ParserConfigurationException, SAXException, IOException {

		String[] htmlInput = {
				"<html>",
				"<head>",
				"<title>A Title</title>",
				"</head>",
				"<body>",
				"<h1 id='h11'>Section 1</h1>",
				"<ol>",
				"<li id='li1'><a href='itemKind'> </a> <a href='link-to-item-1'>item1</a></li>",
				"<li id='li2'>item2</li>",
				"</ol>",
				"<h1 id='h12'>Section 2</h1>",
				"<h2 id='h21'>Subsection 1</h2>",
				"<ol>",
				"<li id='li3'>item3</li>",
				"<li id='li4'>item4</li>",
				"</ol>",
				"<h2 id='h22'>Subsection 2</h2>",
				"<ol>",
				"<li id='li5'>item5</li>",
				"</ol>",
				"<h1 id='h13'>Organization</h1>",
				"<h2 id='h23'>Preamble</h2>",
				"<p id='p1'>preamble text</p>",
				"<h2 id='h24'>Postscript</h2>",
				"<p id='p2'>postscript text</p>",
				"<h2 id='h24'>Presentation</h2>",
				"<p id='p4'>presentation text</p>",
				"<table>",
				"</table>",
				"<table>",
				"<tr>",
				"<td>asst</td>",
				"<td>Do the</td>",				
				"</tr>",
				"<tr>",
				"<td>itemKind</td>",
				"<td>item kind prefix</td>",				
				"</tr>",
				"</table>",
				"</body>",
				"</html>"	
		};


		org.w3c.dom.Document basicHtml = formatHTML (htmlInput); 
		Element root = basicHtml.getDocumentElement();
		String htmlContent = root.getTextContent();

		assertTrue (htmlContent.contains("itemKind")); // alt content

		Element item1 = getElementById(basicHtml, "li1");
		assertNotNull (item1);
		
		XPath xPath = new net.sf.saxon.xpath.XPathFactoryImpl().newXPath();

		Element img = (Element)xPath.evaluate(".//img", item1, XPathConstants.NODE);
		assertNotNull (img);
		String location = img.getAttribute("src");
		assertTrue(location.contains("itemKind-kind.png"));
		
		String itemText = item1.getTextContent();
		assertTrue(itemText.contains("item1"));
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
