/**
 * 
 */
package edu.odu.cs.cwm.documents;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author zeil
 *
 */
public class TestPaginationTransforms {

	private static final String FORMAT = "html";
	
	
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

		XPath xPath = XPathFactory.newInstance().newXPath();
		assertEquals ("html", root.getLocalName());
		
		String actualTitle = (String)xPath.evaluate("/html/head/title", root);
		assertEquals ("Document Title", actualTitle);


		for (int i = 0; i < ids.length; ++i) {
			Element n = (Element)getElementById(basicHtml, ids[i]);
			assertNotNull(n);
			String sectionNumberAttribute = n.getAttribute("sectionNumber");
			assertEquals (shouldBeNumbered[i], sectionNumberAttribute);
		}
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

		XPath xPath = XPathFactory.newInstance().newXPath();
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

		XPath xPath = XPathFactory.newInstance().newXPath();
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
	public void testSectioning() throws XPathExpressionException, TransformerException, ParserConfigurationException, SAXException, IOException {
		
		String[] htmlInput = {
				"<html test='sectioning'>",
				"<head>",
				"<title>Document Title</title>",
				"</head>",
				"<body>",
				"<p id='par1'>A preamble</p>",           // section 0, depth 1
				"<h1 id='h11'>Title 1</h1>",             // section 1, depth 1
				"   <h2 id='h21'>Subitle 1</h2>",        // section 2, depth 2
				"      <p id='par2'>Some text</p>",
				"      <p id='par3'>Some text</p>",
				"      <p id='par4'>Some text</p>",      
				"   <h2 id='h22'>Subitle 2</h2>",        // section 3, depth 2    
				"      <h3 id='h31'>Subsubtitle 1</h3>", // section 4, depth 3
				"         <p id='par5'>Some text</p>",
				"      <h3 id='h32'>Subsubtitle 2</h3>", // section 5, depth 3
				"         <p id='par6'>Some text</p>",
				"<h1 id='h12'>Title 2</h1>",             // section 6, depth 1
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

		int[] shouldBeInSection = {
				0, 5, 
				1, 2,
				3, 4,
				1, 1, 1, 3, 4, 5
		};

		int[] shouldBeAtDepth = {
				1, 1, 
				2, 2,
				3, 3,
				2, 2, 2, 3, 3, 1
		};

		org.w3c.dom.Document basicHtml = formatHTML (htmlInput); 
		Element root = basicHtml.getDocumentElement();
		String htmlContent = root.getTextContent();

		assertTrue (htmlContent.contains("A preamble"));
		assertTrue (htmlContent.contains("Subsubtitle 2"));
		assertTrue (htmlContent.contains("Closing text"));

		XPath xPath = XPathFactory.newInstance().newXPath();
		assertEquals ("html", root.getLocalName());
		
		String actualTitle = (String)xPath.evaluate("/html/head/title", root);
		assertEquals ("Document Title", actualTitle);

		Node par1 = getElementById(basicHtml, "par1");
		assertEquals ("body", par1.getParentNode().getLocalName());
		
		NodeList sections = root.getElementsByTagName("section");

		for (int i = 0; i < ids.length; ++i) {
			//System.err.println("i=" + i + " id =" + ids[i]);
			Node n = getElementById(basicHtml, ids[i]);
			assertNotNull(n);
			Element parent = (Element)n.getParentNode();
			if ("sectionContent".equals(parent.getLocalName())) {
				parent = (Element)parent.getParentNode();	
			}
			assertEquals ("section", parent.getLocalName());
			assertSame (parent, sections.item(shouldBeInSection[i]));
			assertEquals (shouldBeAtDepth[i], Integer.parseInt(parent.getAttribute("depth"))); 
		}
		
		assertEquals (6, sections.getLength());
	}


	
    @Test
    public void testSectioning2() 
            throws XPathExpressionException, TransformerException,
            ParserConfigurationException, SAXException, IOException {
        
        String[] htmlInput = {
                "<html test='sectioning'>",
                "<head>",
                "<title>Document Title</title>",
                "</head>",
                "<body>",
                "<p id='par1'>A preamble</p>",           // section 0, depth 1
                "<h1 id='h11'>Title 1</h1>",             // section 1, depth 1
                "   <h2 id='h21'>Subitle 1</h2>",        // section 2, depth 2
                "      <p id='par2'>Some text</p>",
                "      <p id='par3'>Some text</p>",
                "      <hr/>",
                "      <p id='par4'>Some text</p>",      
                "   <h2 id='h22'>Subitle 2</h2>",        // section 3, depth 2    
                "      <h3 id='h31'>Subsubtitle 1</h3>", // section 4, depth 3
                "         <p id='par5'>Some text</p>",
                "      <h3 id='h32'>Subsubtitle 2</h3>", // section 5, depth 3
                "         <p id='par6'>Some text</p>",
                "<h1 id='h12'>Title 2</h1>",             // section 6, depth 1
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

        int[] shouldBeInSection = {
                0, 5, 
                1, 2,
                3, 4,
                1, 1, 1, 3, 4, 5
        };

        int[] shouldBeAtDepth = {
                1, 1, 
                2, 2,
                3, 3,
                2, 2, 2, 3, 3, 1
        };

        org.w3c.dom.Document basicHtml = formatHTML (htmlInput); 
        Element root = basicHtml.getDocumentElement();
        String htmlContent = root.getTextContent();

        assertTrue (htmlContent.contains("A preamble"));
        assertTrue (htmlContent.contains("Subsubtitle 2"));
        assertTrue (htmlContent.contains("Closing text"));

        XPath xPath = XPathFactory.newInstance().newXPath();
        assertEquals ("html", root.getLocalName());
        
        String actualTitle = (String)xPath.evaluate("/html/head/title", root);
        assertEquals ("Document Title", actualTitle);

        Node par1 = getElementById(basicHtml, "par1");
        assertEquals ("body", par1.getParentNode().getLocalName());
        
        Node par5 = getElementById(basicHtml, "par5");
        assertEquals ("sectionContent", par5.getParentNode().getLocalName());

        Node par3 = getElementById(basicHtml, "par3");
        assertEquals ("sectionDescription", par3.getParentNode().getLocalName());

        Node par4 = getElementById(basicHtml, "par4");
        assertEquals ("sectionContent", par4.getParentNode().getLocalName());

        NodeList sections = root.getElementsByTagName("section");

        for (int i = 0; i < ids.length; ++i) {
            //System.err.println("i=" + i + " id =" + ids[i]);
            Node n = getElementById(basicHtml, ids[i]);
            assertNotNull(n);
            Element parent = (Element)n.getParentNode();
            if ("sectionContent".equals(parent.getLocalName())) {
                parent = (Element)parent.getParentNode();   
            } else if ("sectionDescription".equals(parent.getLocalName())) {
                parent = (Element)parent.getParentNode();   
            } 
            assertSame (parent, sections.item(shouldBeInSection[i]));
            assertEquals (shouldBeAtDepth[i], Integer.parseInt(parent.getAttribute("depth"))); 
        }
        
        assertEquals (6, sections.getLength());
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