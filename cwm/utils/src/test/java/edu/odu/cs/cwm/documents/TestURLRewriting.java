/**
 * 
 */
package edu.odu.cs.cwm.documents;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
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
public class TestURLRewriting {


	private static final String BB_URL = "https://blackboard.site/webapps/blackboard/execute/modulepage/view?course_id=_284623_1";
    private static final String THE_BASE_URL = "src/test/data/urlShortcuts/";
    public String lastTransformed;
	



	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
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
	public void testDocOnPrimary() 
	        throws XPathExpressionException, TransformerException, 
	        ParserConfigurationException, SAXException, IOException {

		String[] htmlInput = {
				"<html>",
				"<head>",
				"<title>A Title</title>",
				"</head>",
				"<body>",
				"<p>Some text leading to ",
				"<a id='a1' href='doc:DocSet1'/>",
				" but this does not work: ",
				"<a id='a2' href='doc:NotADocSet'>not a link</a>",
				"and this doesn't make much sense, but",
                "<img id='img1' src='doc:DocSet3'/>",
		        "</p>",
				"</body>",
				"</html>"	
		};


		org.w3c.dom.Document basicHtml = parseHTML (htmlInput); 
		
		URLRewriting rewriter = new URLRewriting(THE_BASE_URL, BB_URL);
		rewriter.rewrite(basicHtml);
		
		Element root = basicHtml.getDocumentElement();
		String htmlContent = root.getTextContent();
		
		Element link1 = getElementById(basicHtml, "a1");
		assertNotNull (link1);
		String href = link1.getAttribute("href");
		assertEquals (THE_BASE_URL + "Group1/DocSet1/index.html", href);
		
		Element link2 = getElementById(basicHtml, "a2");
        assertNotNull (link2);
        href = link2.getAttribute("href");
        assertEquals ("doc:NotADocSet", href);
		
        Element img1 = getElementById(basicHtml, "img1");
        assertNotNull (img1);
        String src = img1.getAttribute("src");
        assertEquals (THE_BASE_URL + "Group2/DocSet3/index.html", src);
	}


    @Test
    public void testDocOnSecondary() 
            throws XPathExpressionException, TransformerException, 
            ParserConfigurationException, SAXException, IOException {

        String[] htmlInput = {
                "<html>",
                "<head>",
                "<title>A Title</title>",
                "</head>",
                "<body>",
                "<p>Some text leading to ",
                "<a id='a1' href='doc:secondaryDoc1.mmd'/>",
                " but this does not work: ",
                "<a id='a2' href='doc:secondaryDoc2.mmd'>not a link</a>",
                "and this doesn't make much sense, but",
                "<img id='img1' src='doc:secondaryDoc1.mmd'/>",
                "</p>",
                "</body>",
                "</html>"   
        };


        org.w3c.dom.Document basicHtml = parseHTML (htmlInput); 
        
        URLRewriting rewriter = new URLRewriting(THE_BASE_URL, BB_URL);
        rewriter.rewrite(basicHtml);
        
        Element root = basicHtml.getDocumentElement();
        String htmlContent = root.getTextContent();
        
        Element link1 = getElementById(basicHtml, "a1");
        assertNotNull (link1);
        String href = link1.getAttribute("href");
        assertEquals (THE_BASE_URL + "Group1/DocSet2/secondaryDoc1.mmd.html", href);
        
        Element link2 = getElementById(basicHtml, "a2");
        assertNotNull (link2);
        href = link2.getAttribute("href");
        assertEquals ("doc:secondaryDoc2.mmd", href);
        
        Element img1 = getElementById(basicHtml, "img1");
        assertNotNull (img1);
        String src = img1.getAttribute("src");
        assertEquals (THE_BASE_URL + "Group1/DocSet2/secondaryDoc1.mmd.html", src);
    }

    
    @Test
    public void testDocxOnPrimary() 
            throws XPathExpressionException, TransformerException, 
            ParserConfigurationException, SAXException, IOException {

        String[] htmlInput = {
                "<html>",
                "<head>",
                "<title>A Title</title>",
                "</head>",
                "<body>",
                "<p>Some text leading to ",
                "<a id='a1' href='docx:DocSet1'/>",
                " but this does not work: ",
                "<a id='a2' href='docx:NotADocSet'>not a link</a>",
                "and this doesn't make much sense, but",
                "<img id='img1' src='docx:DocSet3'/>",
                "</p>",
                "</body>",
                "</html>"   
        };


        org.w3c.dom.Document basicHtml = parseHTML (htmlInput); 
        
        URLRewriting rewriter = new URLRewriting(THE_BASE_URL, BB_URL);
        rewriter.rewrite(basicHtml);
        
        Element root = basicHtml.getDocumentElement();
        String htmlContent = root.getTextContent();
        
        Element link1 = getElementById(basicHtml, "a1");
        assertNotNull (link1);
        String href = link1.getAttribute("href");
        assertEquals (THE_BASE_URL + "Group1/DocSet1/index.html", href);
        
        Element link2 = getElementById(basicHtml, "a2");
        assertNotNull (link2);
        href = link2.getAttribute("href");
        assertEquals ("docx:NotADocSet", href);
        
        Element img1 = getElementById(basicHtml, "img1");
        assertNotNull (img1);
        String src = img1.getAttribute("src");
        assertEquals (THE_BASE_URL + "Group2/DocSet3/index.html", src);
    }


    @Test
    public void testDocxOnSecondary() 
            throws XPathExpressionException, TransformerException, 
            ParserConfigurationException, SAXException, IOException {

        String[] htmlInput = {
                "<html>",
                "<head>",
                "<title>A Title</title>",
                "</head>",
                "<body>",
                "<p>Some text leading to ",
                "<a id='a1' href='docx:secondaryDoc1.mmd'/>",
                " but this does not work: ",
                "<a id='a2' href='docx:secondaryDoc2.mmd'>not a link</a>",
                "and this doesn't make much sense, but",
                "<img id='img1' src='docx:secondaryDoc1.mmd'/>",
                "</p>",
                "</body>",
                "</html>"   
        };


        org.w3c.dom.Document basicHtml = parseHTML (htmlInput); 
        
        URLRewriting rewriter = new URLRewriting(THE_BASE_URL, BB_URL);
        rewriter.rewrite(basicHtml);
        
        Element root = basicHtml.getDocumentElement();
        String htmlContent = root.getTextContent();
        
        Element link1 = getElementById(basicHtml, "a1");
        assertNotNull (link1);
        String href = link1.getAttribute("href");
        assertEquals (THE_BASE_URL + "Group1/DocSet2/secondaryDoc1.mmd.html", href);
        
        Element link2 = getElementById(basicHtml, "a2");
        assertNotNull (link2);
        href = link2.getAttribute("href");
        assertEquals ("docx:secondaryDoc2.mmd", href);
        
        Element img1 = getElementById(basicHtml, "img1");
        assertNotNull (img1);
        String src = img1.getAttribute("src");
        assertEquals (THE_BASE_URL + "Group1/DocSet2/secondaryDoc1.mmd.html", src);
    }
    
    private org.w3c.dom.Document parseHTML(String[] htmlInput) 
            throws TransformerException, ParserConfigurationException, SAXException, IOException {

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        org.w3c.dom.Document inputDoc = dBuilder.parse(
                new InputSource(
                        new StringReader(
                                String.join(System.getProperty("line.separator"),
                                        htmlInput))));
        return inputDoc;            
    }

	
    @Test
    public void testGraphics() 
            throws XPathExpressionException, TransformerException, 
            ParserConfigurationException, SAXException, IOException {

        String[] htmlInput = {
                "<html>",
                "<head>",
                "<title>A Title</title>",
                "</head>",
                "<body>",
                "<p>Some text leading to ",
                "<a id='a1' href='doc:DocSet1'/>",
                " but this does not work: ",
                "<a id='a2' href='doc:NotADocSet'>not a link</a>",
                "and this doesn't make much sense, but",
                "<img id='img1' src='graphics:something.png'/>",
                "</p>",
                "</body>",
                "</html>"   
        };


        org.w3c.dom.Document basicHtml = parseHTML (htmlInput); 
        
        URLRewriting rewriter = new URLRewriting(THE_BASE_URL, BB_URL);
        rewriter.rewrite(basicHtml);
        
        Element root = basicHtml.getDocumentElement();
        String htmlContent = root.getTextContent();
        
        
        Element img1 = getElementById(basicHtml, "img1");
        assertNotNull (img1);
        String src = img1.getAttribute("src");
        assertEquals (THE_BASE_URL + "graphics/something.png", src);
    }
	
    @Test
    public void testStyles() 
            throws XPathExpressionException, TransformerException, 
            ParserConfigurationException, SAXException, IOException {

        String[] htmlInput = {
                "<html>",
                "<head>",
                "<title>A Title</title>",
                "</head>",
                "<body>",
                "<p>Some text leading to ",
                "<a id='a1' href='doc:DocSet1'/>",
                " but this does not work: ",
                "<a id='a2' href='doc:NotADocSet'>not a link</a>",
                "and this doesn't make much sense, but",
                "<script id='img1' src='styles:something.js'/>",
                "</p>",
                "</body>",
                "</html>"   
        };


        org.w3c.dom.Document basicHtml = parseHTML (htmlInput); 
        
        URLRewriting rewriter = new URLRewriting(THE_BASE_URL, BB_URL);
        rewriter.rewrite(basicHtml);
        
        Element root = basicHtml.getDocumentElement();
        String htmlContent = root.getTextContent();
        
        
        Element img1 = getElementById(basicHtml, "img1");
        assertNotNull (img1);
        String src = img1.getAttribute("src");
        assertEquals (THE_BASE_URL + "styles/something.js", src);
    }

    
    
    @Test
    public void testTitleExtraction() 
            throws XPathExpressionException, TransformerException, 
            ParserConfigurationException, SAXException, IOException {

        String[] htmlInput = {
                "<html>",
                "<head>",
                "<title>A Title</title>",
                "</head>",
                "<body>",
                "<p>Some text leading to ",
                "<a id='a1' href='doc:DocSet1'>TBD</a>",
                "<a id='a2' href='doc:secondaryDoc1.mmd'>TBD</a>",
                "but the next document has no title",
                "<a id='a3' href='doc:secondaryDoc3.mmd'>TBD</a>", 
                "and this one remains to be written",
                "<a id='a4' href='doc:secondaryDoc4.mmd'>TBD</a>", 
                "</p>",
                "</body>",
                "</html>"   
        };


        org.w3c.dom.Document basicHtml = parseHTML (htmlInput); 
        
        URLRewriting rewriter = new URLRewriting(THE_BASE_URL, BB_URL);
        rewriter.rewrite(basicHtml);
        
        Element root = basicHtml.getDocumentElement();
        String htmlContent = root.getTextContent();
        
        Element link1 = getElementById(basicHtml, "a1");
        assertNotNull (link1);
        String linkText = link1.getTextContent();
        assertEquals ("Doc Set 1", linkText);
        
        Element link2 = getElementById(basicHtml, "a2");
        assertNotNull (link2);
        linkText = link2.getTextContent();
        assertEquals ("Secondary Doc 1", linkText);

        Element link3 = getElementById(basicHtml, "a3");
        assertNotNull (link3);
        linkText = link3.getTextContent();
        assertEquals ("TBD", linkText);
        
        Element link4 = getElementById(basicHtml, "a4");
        assertNotNull (link4);
        linkText = link4.getTextContent();
        assertEquals ("TBD", linkText);
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
