/**
 * 
 */
package edu.odu.cs.cowem.documents;

import static org.junit.jupiter.api.Assertions.*;

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
public class TestPaginationTransforms {

	private static final String FORMAT = "scroll";
	
	
	private String lastTransformed;

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
			"formats", "html,pages,slides,epub,directory,topics,modules,navigation",
			"numberDepth", "3"
	};

	private String[] metadataProperties = {
			"TOC", "1",
			"Title", "The Title",
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



	public Element getElementByName (org.w3c.dom.Document doc, String name) {
	    Element root = doc.getDocumentElement();
	    XPath xPath = new net.sf.saxon.xpath.XPathFactoryImpl().newXPath();
	    Node n;
	    try {
	        n = (Node)xPath.evaluate("//*[@name='" + name + "']",
	                root, XPathConstants.NODE);
	    } catch (XPathExpressionException e) {
	        return null;
	    }
	    return (Element)n;
	}

	
	@Test
	public void testSectionNumbering() throws XPathExpressionException, TransformerException, ParserConfigurationException, SAXException, IOException {
		
		String[] htmlInput = {
				"<html test='sectionNumbering'>",
				"<head>",
				"<title>Document Title</title>",
				"</head>",
				"<body>",
				"<p id='par1'>A preamble</p>",           // page 0
				"<h1 id='h11'>Title 1</h1>",             // page 1
				"   <h2 id='h21'>Subitle 1</h2>",
				"      <p id='par2'>Some text</p>",
				"      <p id='par3'>Some text</p>",
				"      <div><h1 id='h13'>Nonstandard title</h1></div>",
				"      <hr/>",
				"      <p id='par4'>Some text</p>",      // page 2
				"   <h2 id='h22'>Subitle 2</h2>",        // page 3    
				"      <h3 id='h31'>Subsubtitle 1</h3>",
				"         <p id='par5'>Some text</p>",
				"      <h3 id='h32'>Subsubtitle 2</h3>", // page 4
				"         <p id='par6'>Some text</p>",
				"<h1 id='h12'>Title 2</h1>",             // page 5
				"   <p id='par7'>Closing text</p>",
				"</body>",
				"</html>"	
		};

		String[] ids = {
				"h11", "h12", "h13",
				"h21", "h22",
				"h31", "h32",
		};

		String[] shouldBeNumbered = {
				"1 ", "2 ", "",
				"1.1 ", "1.2 ",
				"1.2.1 ", "1.2.2 "
		};

		org.w3c.dom.Document basicHtml = formatHTML (htmlInput); 
		Element root = basicHtml.getDocumentElement();
		String htmlContent = root.getTextContent();

		assertTrue (htmlContent.contains("A preamble"));
		assertTrue (htmlContent.contains("Subsubtitle 2"));
		assertTrue (htmlContent.contains("Closing text"));

		XPath xPath = new net.sf.saxon.xpath.XPathFactoryImpl().newXPath();
		assertEquals ("html", root.getLocalName());
		
		String actualTitle = (String)xPath.evaluate("/html/head/title", root);
		assertEquals ("Document Title", actualTitle);


		for (int i = 0; i < ids.length; ++i) {
			Element n = (Element)getElementById(basicHtml, ids[i]);
			assertNotNull(n);
			String sectionNumberAttribute = n.getAttribute("sectionNumber");
			assertEquals (shouldBeNumbered[i], sectionNumberAttribute,
					"looking at " + ids[i]);
		}
	}



	@Test
	public void testHeaderNormalization() throws XPathExpressionException, TransformerException, ParserConfigurationException, SAXException, IOException {

	    String[] htmlInput = {
	            "<html test='normalizeHeaders'>",
	            "<head>",
	            "<title>Document Title</title>",
	            "</head>",
	            "<body>",
	            "<p id='par1'>A preamble</p>",
	            "<h1><a name='h11' href='h11'>Title 1</a></h1>",
	            "   <h2><a href='h21' name='h21'>Subtitle 1</a></h2>",
	            "      <p id='par2'>Some text</p>",
	            "   <h2><a name='h22'>Subtitle 2</a></h2>",
	            "<h1><a name='h12' href='h12'>Title 2</a>other stuff</h1>",
	            "      <p id='par3'>Some text</p>",
	            "</body>",
	            "</html>"   
	    };


	    org.w3c.dom.Document basicHtml = formatHTML (htmlInput); 
	    Element root = basicHtml.getDocumentElement();
	    String htmlContent = root.getTextContent();

	    assertTrue (htmlContent.contains("A preamble"));
	    assertTrue (htmlContent.contains("Title 1"));
	    assertTrue (htmlContent.contains("Title 2"));
	    assertTrue (htmlContent.contains("Subtitle 1"));
	    assertTrue (htmlContent.contains("Subtitle 2"));
	    assertTrue (htmlContent.contains("Some text"));

	    XPath xPath = new net.sf.saxon.xpath.XPathFactoryImpl().newXPath();
	    assertEquals ("html", root.getLocalName());

	    String actualTitle = (String)xPath.evaluate("/html/head/title", root);
	    assertEquals ("Document Title", actualTitle);

	    Element n = (Element)getElementById(basicHtml, "h11");
	    assertNotNull(n);
	    assertEquals ("h1", n.getNodeName());

	    n = (Element)getElementById(basicHtml, "h21");
	    assertNotNull(n);
	    assertEquals ("h2", n.getNodeName());

	    n = (Element)getElementById(basicHtml, "par1");
	    assertNotNull(n);
	    assertEquals ("p", n.getNodeName());

	    n = (Element)getElementById(basicHtml, "par3");
	    assertNotNull(n);
	    assertEquals ("p", n.getNodeName());

	    n = (Element)getElementById(basicHtml, "h22");
	    assertNull(n);

	    n = (Element)getElementById(basicHtml, "h12");
	    assertEquals("h1", n.getNodeName());

	    n = (Element)getElementByName(basicHtml, "h22");
	    assertNotNull(n);
	    assertEquals ("a", n.getNodeName());

	    n = (Element)getElementByName(basicHtml, "h12");
	    assertNull(n);
	}

	
	
	
	@Test
	public void testPagination() throws XPathExpressionException, TransformerException, ParserConfigurationException, SAXException, IOException {
		
		String[] htmlInput = {
				"<html test='pagination'>",
				"<head>",
				"<title>Document Title</title>",
				"</head>",
				"<body>",
				"<p id='par1'>A preamble</p>",           // page 0
				"<h1 id='h11'>Title 1</h1>",             // page 1
				"   <h2 id='h21'>Subitle 1</h2>",
				"      <p id='par2'>Some text</p>",
				"      <p id='par3'>Some text</p>",
				"      <hr/>",
				"      <p id='par4'>Some text</p>",      // page 2
				"   <h2 id='h22'>Subitle 2</h2>",        // page 3    
				"      <h3 id='h31'>Subsubtitle 1</h3>",
				"         <p id='par5'>Some text</p>",
				"      <h3 id='h32'>Subsubtitle 2</h3>", // page 4
				"         <p id='par6'>Some text</p>",
				"<h1 id='h12'>Title 2</h1>",             // page 5
				"   <p id='par7'>Closing text</p>",
				"</body>",
				"</html>"	
		};

		String[] ids = {
				"h11", "h12", 
				"h21", "h22",
				"h31", "h32",
				"par1", "par2", "par3", "par4", "par5", "par6", "par7"
		};

		int[] shouldBeOnPage = {
				1, 5, 
				1, 3,
				3, 4,
				0, 1, 1, 2, 3, 4, 5
		};

		org.w3c.dom.Document basicHtml = formatHTML (htmlInput); 
		Element root = basicHtml.getDocumentElement();
		String htmlContent = root.getTextContent();

		assertTrue (htmlContent.contains("A preamble"));
		assertTrue (htmlContent.contains("Subsubtitle 2"));
		assertTrue (htmlContent.contains("Closing text"));

		XPath xPath = new net.sf.saxon.xpath.XPathFactoryImpl().newXPath();
		assertEquals ("html", root.getLocalName());
		
		String actualTitle = (String)xPath.evaluate("/html/head/title", root);
		assertEquals ("Document Title", actualTitle);

		NodeList pages = root.getElementsByTagName("page");
		assertEquals (6, pages.getLength());

		for (int i = 0; i < pages.getLength(); ++i) {
			Node n = pages.item(i);
			Element parent = (Element)n.getParentNode();
			assertEquals ("body", parent.getLocalName());
		}

		for (int i = 0; i < ids.length; ++i) {
			Node n = getElementById(basicHtml, ids[i]);
			assertNotNull(n);
			assertSame (n.getParentNode(), pages.item(shouldBeOnPage[i]));
		}
	}

	@Test
	public void testPagination2() throws XPathExpressionException, TransformerException, ParserConfigurationException, SAXException, IOException {
		
		String[] htmlInput = {
				"<html  test='pagination'>",
				"<head>",
				"<title>Document Title</title>",
				"</head>",
				"<body>",
				"<h1 id='h11'>Title 1</h1>",             // page 0
				"   <h2 id='h21'>Subitle 1</h2>",
				"      <p id='par2'>Some text</p>",
				"      <p id='par3'>Some text</p>",
				"      <hr/>",
				"      <p id='par4'>Some text</p>",      // page 1
				"   <h2 id='h22'>Subitle 2</h2>",        // page 2    
				"      <h3 id='h31'>Subsubtitle 1</h3>",
				"         <p id='par5'>Some text</p>",
				"      <h3 id='h32'>Subsubtitle 2</h3>", // page 3
				"         <p id='par6'>Some text</p>",
				"<h1 id='h12'>Title 2</h1>",             // page 4
				"   <p id='par7'>Closing text</p>",
				"</body>",
				"</html>"	
		};

		String[] ids = {
				"h11", "h12", 
				"h21", "h22",
				"h31", "h32",
				"par2", "par3", "par4", "par5", "par6", "par7"
		};

		int[] shouldBeOnPage = {
				0, 4, 
				0, 2,
				2, 3,
				0, 0, 1, 2, 3, 4
		};

		org.w3c.dom.Document basicHtml = formatHTML (htmlInput); 
		Element root = basicHtml.getDocumentElement();
		String htmlContent = root.getTextContent();

		assertTrue (htmlContent.contains("Subsubtitle 2"));
		assertTrue (htmlContent.contains("Closing text"));

		XPath xPath = new net.sf.saxon.xpath.XPathFactoryImpl().newXPath();
		assertEquals ("html", root.getLocalName());
		
		String actualTitle = (String)xPath.evaluate("/html/head/title", root);
		assertEquals ("Document Title", actualTitle);

		NodeList pages = root.getElementsByTagName("page");
		assertEquals (5, pages.getLength());

		for (int i = 0; i < ids.length; ++i) {
			// System.err.println("i=" + i);
			Node n = getElementById(basicHtml, ids[i]);
			assertNotNull(n);
			assertSame (n.getParentNode(), pages.item(shouldBeOnPage[i]));
		}
	}


	
	@Test
	public void testIncremental1() throws XPathExpressionException, TransformerException, ParserConfigurationException, SAXException, IOException {

	    String[] htmlInput = {
	            "<html  test='pagination'>",
	            "<head>",
	            "<title>Document Title</title>",
	            "</head>",
	            "<body>",
	            "<h1 id='h11'>Title 1</h1>",             // page 0
	            "   <p id='par1'>par1</p>",
	            "   <ul>",
	            "     <li> <i>item1</i> <span class='incremental'> </span></li>",
	            "     <li> <i>item2</i></li>",
	            "     <li> <i>item3</i></li>",
	            "   </ul>",
	            "   <div id='div1'>Some text</div>",
	            "</body>",
	            "</html>"   
	    };


	    org.w3c.dom.Document basicHtml = formatHTML (htmlInput); 
	    Element root = basicHtml.getDocumentElement();
	    String htmlContent = root.getTextContent();

	    assertTrue (htmlContent.contains("par1"));
	    assertTrue (htmlContent.contains("item1"));
	    assertTrue (htmlContent.contains("item2"));
	    assertTrue (htmlContent.contains("item3"));
	    assertTrue (htmlContent.contains("Some text"));

	    XPath xPath = new net.sf.saxon.xpath.XPathFactoryImpl().newXPath();
	    assertEquals ("html", root.getLocalName());

	    NodeList pages = root.getElementsByTagName("page");
	    assertEquals (4, pages.getLength());
	    
	    Element page2 = (Element)pages.item(1);
	    assertEquals ("1", page2.getAttribute("increm"));

	    NodeList items = root.getElementsByTagName("li");
	    assertEquals (6, items.getLength());

	    NodeList divs = root.getElementsByTagName("div");
	    assertEquals (1, divs.getLength());

	    for (int i = 0; i < pages.getLength(); ++i) {
	        // System.err.println("i=" + i);
	        Node page = pages.item(i);
	        NodeList nl = (NodeList)xPath.evaluate(".//li",
	                page, XPathConstants.NODESET);
	        assertEquals (i, nl.getLength());
	    }

	    NodeList h11Nodes = (NodeList)xPath.evaluate("//*[@id='h11']",
	                    root, XPathConstants.NODESET);
	    assertEquals (1, h11Nodes.getLength());
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
        if (lastTransformed.length() == 0) {
            throw new IOException ("Empty transformation output.");
        }
        
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
