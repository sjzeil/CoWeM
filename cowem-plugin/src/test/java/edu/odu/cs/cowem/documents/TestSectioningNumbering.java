/**
 * 
 */
package edu.odu.cs.cowem.documents;

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
public class TestSectioningNumbering {

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
    public void testSectioningWithHRs2() 
            throws XPathExpressionException, TransformerException,
            ParserConfigurationException, SAXException, IOException {
        
        String[] htmlInput = {
			"<html test='sectioningNumbering'>",
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
        //assertTrue ("checking for presentation", htmlContent.contains("Events"));
        
        // Is 1st section numbered correctly?
        assertTrue (htmlContent.contains("1 Software for Courses"));
        assertTrue (htmlContent.contains("1.1 Course Websites"));

    }   
	
    
    
    @Test
    public void testPreambleOrdering() 
            throws XPathExpressionException, TransformerException,
            ParserConfigurationException, SAXException, IOException {
        
        String[] htmlInput = {
            "<html test='sectioningNumbering'>",
            "<head>",
            "<title>@Title@</title>",
            "</head>",
            "<body>",
            "<h1><a href='#preamble' name='preamble'></a>Preamble</h1>",
            "<p>preamble text</p>",
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
        //assertTrue ("checking for presentation", htmlContent.contains("Events"));
        
        // Is 1st section numbered correctly?
        assertTrue (htmlContent.contains("1 Software for Courses"));
        assertTrue (htmlContent.contains("1.1 Course Websites"));

        
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
