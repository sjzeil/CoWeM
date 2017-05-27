/**
 * 
 */
package edu.odu.cs.cowem.documents;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
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
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author zeil
 *
 */
public class TestSectioningTransforms {

	private static final String FORMAT = "modules";
	
	
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



	public Element getElementByName (org.w3c.dom.Document doc, String name) {
	    Element root = doc.getDocumentElement();
	    XPath xPath = XPathFactory.newInstance().newXPath();
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

		assertFalse (htmlContent.contains("A preamble"));
		assertTrue (htmlContent.contains("Subsubtitle 2"));
		assertTrue (htmlContent.contains("Closing text"));

		XPath xPath = XPathFactory.newInstance().newXPath();
		assertEquals ("html", root.getLocalName());
		
		String actualTitle = (String)xPath.evaluate("/html/head/title", root);
		assertEquals ("Document Title", actualTitle);

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

        assertFalse (htmlContent.contains("A preamble"));
        assertTrue (htmlContent.contains("Subsubtitle 2"));
        assertTrue (htmlContent.contains("Closing text"));

        XPath xPath = XPathFactory.newInstance().newXPath();
        assertEquals ("html", root.getLocalName());
        
        String actualTitle = (String)xPath.evaluate("/html/head/title", root);
        assertEquals ("Document Title", actualTitle);

        Node par1 = getElementById(basicHtml, "par1");
        assertNull (par1);
        
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
	

    
    @Test
    public void testSectioningWithHRs() 
            throws XPathExpressionException, TransformerException,
            ParserConfigurationException, SAXException, IOException {
        
        String[] htmlInput = {
                "<html test='sectioning'>",
                "<head>",
                "<title>Document Title</title>",
                "</head>",
                "<body>",
                "<h1>Orientation</h1>",
                "<hr/>",
                "<p id='activities1'>activities1</p>",
                "<h1>Part I</h1>",
                "<h2>Section 1</h2>",
                "<hr/>",
                "<p id='activities2'>activities2</p>",
                "<h2>Section 2</h2>",
                "<h3>Subsection 1</h3>",
                "<hr/>",
                "<p id='activities3'>activities3</p>",
                "<h3>Subsection 2</h3>",
                "<hr/>",
                "<p id='activities4'>activities4</p>",
                "<h1>PartII</h1>",
                "<h2>Section 3</h2>",
                "<hr/>",
                "<p id='activities5'>activities5</p>",
                "<h2>Section 4</h2>",
                "<h3>Subsection 3</h3>",
                "<hr/>",
                "<p id='activities6'>activities6</p>",
                "<h3>Subsection 4</h3>",
                "<hr/>",
                "<p id='activities7'>activities7</p>",
                "</body>",
                "</html>"   
        };


        org.w3c.dom.Document basicHtml = formatHTML (htmlInput); 
        Element root = basicHtml.getDocumentElement();
        String htmlContent = root.getTextContent();

        XPath xPath = XPathFactory.newInstance().newXPath();
        assertEquals ("html", root.getLocalName());

        NodeList sections = (NodeList)xPath.evaluate(
                "/html/body//section[@depth='1']", root,
                XPathConstants.NODESET);
        assertEquals (3, sections.getLength());
        
        Node orientation = sections.item(0);
        Node part1 = sections.item(1);
        Node part2 = sections.item(2);
        
        NodeList subsections = (NodeList)xPath.evaluate(
                ".//section[@depth='2']", orientation,
                XPathConstants.NODESET);
        assertEquals (0, subsections.getLength());
        
        subsections = (NodeList)xPath.evaluate(
                ".//section[@depth='2']", part1,
                XPathConstants.NODESET);
        assertEquals (2, subsections.getLength());
        
        subsections = (NodeList)xPath.evaluate(
                ".//section[@depth='2']", part2,
                XPathConstants.NODESET);
        assertEquals (2, subsections.getLength());
        
        for (int i = 1; i <= 7; ++i) {
        	NodeList nl = (NodeList)xPath.evaluate("//*[@id='" 
        			+ "activities" + Integer.toString(i) + "']",
        			root, XPathConstants.NODESET);
        	assertEquals ("Checking for activities" + i, 1, nl.getLength());
        }
        
        
        
    }

    
    @Test
    public void testSectioningWithHRs2() 
            throws XPathExpressionException, TransformerException,
            ParserConfigurationException, SAXException, IOException {
        
        String[] htmlInput = {
			"<html test='sectioning'>",
			"<head>",
			"<title>@Title@</title>",
			"</head>",
			"<body>",
			"<h1><a href='#software-for-courses' name='software-for-courses'></a>Software for Courses</h1>",
			"<h2><a href='#course-websites-documents' name='course-websites-documents'></a>Course Websites &amp; Documents</h2>",
			"<p><strong>Overview</strong></p>",
			"<p>A general discussion.</p>",
			"<hr/>",
			"<p><strong>Subject <a href='note1'>Note</a></strong></p>",
			"<ol>",
			"<li>Look at the <a href='item1'>Course Websites</a></li>",
			"<li><a href='lecture'>Old:</a> <a href='item2'>TBD</a></li>",
			"</ol>",
			"<h2><a href='#assignment-submission-and-grading' name='assignment-submission-and-grading'></a>Assignment Submission and Grading</h2>",
			"<p>Support for submitting assignments via the web and triggering automatic grading.</p>",
			"<ol>",
			"<li><a href='lecture'> </a> <a href='item3'>Web-based Assignment Submission</a></li>",
			"<li><a href='lab'> </a> <a href='item4'>Programming Assignments</a></li>",
			"</ol>",
			"<h1><a href='#preamble' name='preamble'></a>Preamble</h1>",
			"<p>preamble text</p>",
			"<h1><a href='#postscript' name='postscript'></a>Postscript</h1>",
			"<p>All times in this schedule are given in Eastern Time.</p>",
			"<h1><a href='#presentation' name='presentation'></a>Presentation</h1>",
			"<table>",
			"<thead>",
			"<tr>",
			"<th>Topics </th>",
			"<th>Lecture Notes </th>",
			"<th>Readings </th>",
			"<th>Assignments &amp; Other Events </th>",
			"</tr>",
			"</thead>",
			"<tbody>",
			"<tr>",
			"<td>topics </td>",
			"<td>slides video lecturenotes construct </td>",
			"<td>text </td>",
			"<td>quiz asst selfassess lecture exam event </td>",
			"</tr>",
			"</tbody>",
			"</table>",
			"<table>",
			"<thead>",
			"<tr>",
			"<th>Document Kind </th>",
			"<th>Prefix </th>",
			"</tr>",
			"</thead>",
			"<tbody>",
			"<tr>",
			"<td>lecture </td>",
			"<td>Read chapters </td>",
			"</tr>",
			"</tbody>",
			"</table></body>",
			"</html>"
		};    
    
        org.w3c.dom.Document basicHtml = formatHTML (htmlInput); 
        Element root = basicHtml.getDocumentElement();
        String htmlContent = root.getTextContent();
        
        assertTrue ("checking for preamble", htmlContent.contains("preamble text"));
        assertTrue ("checking for postscript", htmlContent.contains("Eastern Time"));
        assertTrue ("checking for presentation", htmlContent.contains("event"));

        XPath xPath = XPathFactory.newInstance().newXPath();
        assertEquals ("html", root.getLocalName());

        NodeList sections = (NodeList)xPath.evaluate(
                "/html/body//section[@depth='1']", root,
                XPathConstants.NODESET);
        assertEquals (1, sections.getLength());
                
        NodeList subsections = (NodeList)xPath.evaluate(
                ".//section[@depth='2']", root,
                XPathConstants.NODESET);
        assertEquals (2, subsections.getLength());
                
        for (int i = 1; i <= 4; ++i) {
        	NodeList nl = (NodeList)xPath.evaluate("//*[@href='" 
        			+ "item" + Integer.toString(i) + "']",
        			root, XPathConstants.NODESET);
        	assertEquals ("Checking for item" + i, 1, nl.getLength());
        }
        
        
        
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
