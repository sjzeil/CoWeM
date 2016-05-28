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
    private static final String THE_BASE_URL = "theBaseURL/";
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
	public void testTargetDoc() throws XPathExpressionException, TransformerException, ParserConfigurationException, SAXException, IOException {

		String[] htmlInput = {
				"<html>",
				"<head>",
				"<title>A Title</title>",
				"</head>",
				"<body>",
				"<p>Some text leading to ",
				"<a id='11' href='targetDoc:whatever'/>",
				" but this is not a link: ",
				"<a id='a2' name='targetDoc:whatever'>not a link</a>",
				"and this doesn't make much sense, but",
                "<img id='img1' src='targetDoc:whatever'/>",
		        "</p>",
				"</body>",
				"</html>"	
		};


		org.w3c.dom.Document basicHtml = parseHTML (htmlInput); 
		
		new URLRewriting(THE_BASE_URL, BB_URL).rewrite(basicHtml);
		
		Element root = basicHtml.getDocumentElement();
		String htmlContent = root.getTextContent();
		
		Element link1 = getElementById(basicHtml, "a1");
		assertNotNull (link1);
		String href = link1.getAttribute("href");
		assertEquals (THE_BASE_URL + "../Public/whatever/index.html", href);
		
		Element link2 = getElementById(basicHtml, "a2");
        assertNotNull (link2);
        href = link2.getAttribute("href");
        assertEquals ("", href);
        String name = link2.getAttribute("name");
        assertEquals ("targetDoc:whatever", name);
		
        Element img1 = getElementById(basicHtml, "img1");
        assertNotNull (img1);
        String src = img1.getAttribute("src");
        assertEquals (THE_BASE_URL + "../Public/whatever/index.html", src);
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
