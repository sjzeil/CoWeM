/**
 * 
 */
package edu.odu.cs.cowem.documents;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import edu.odu.cs.cowem.documents.MarkdownDocument;
import edu.odu.cs.cowem.documents.WebsiteProject;

/**
 * @author zeil
 *
 */
public class TestMarkdownDocument {
	
	
	private String mdInput1 = "Title: Title of Document\n"
			+ "Author: John Doe\n"
			+ "Date: Jan 1, 2012\n\n"
			+ "# Section 1\n\n"
			+ "%if _includeThis\n"
			+ "## Section 1.1\n\n"
			+ "A paragraph in\nsection 1.1\n\n"
			+ "%endif\n\n"
			+ "## Section 1.2\n\n"
			+ "A paragraph in\nsection 1.2\n\n"
			+ "Something in _italics_ and\n"
			+ "something else in **bold**\n"
			+ "and \\emph{even\nemphasized}.\n";
	
    private String mdInput2 = "Title: Title of Document\n"
            + "Author: John Doe\n"
            + "Date: Jan 1, 2012\n\n"
            + "# Section 1\n\n"
            + "%if _includeThis\n"
            + "## Section 1.1\n\n"
            + "A paragraph in\nsection 1.1\n\n"
            + "%endif\n\n"
            + "## Section 1.2\n\n"
            + "A paragraph in\nsection 1.2\n\n"
            + "Something in _italics_ and\n"
            + "something else in **bold**\n";

    
    private String preProcessed1 =
			"\n# Section 1\n\n\n"
			+ "## Section 1.1\n\n"
			+ "A paragraph in\nsection 1.1\n\n"
			+ "\n\n"
			+ "## Section 1.2\n\n"
			+ "A paragraph in\nsection 1.2\n\n"
			+ "Something in _italics_ and\n"
			+ "something else in **bold**\n";
	
	private String preProcessed2 = 
			"\n# Section 1\n\n"
			+ "\n\n"
			+ "## Section 1.2\n\n"
			+ "A paragraph in\nsection 1.2\n\n"
			+ "Something in _italics_ and\n"
			+ "something else in **bold**\n";
	
	
	private Properties properties;
	private WebsiteProject proj;
	private File source;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		properties = new Properties();
		properties.put("Title", "Title of Document");
		proj = new WebsiteProject(Paths.get("src/test/data/urlShortcuts")
		        .toFile().getAbsoluteFile());
		source = 
		   Paths.get("src/test/data/urlShortcuts/Group1/DocSet1/DocSet1.md")
		   .toFile();
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
	 * Test method for {@link edu.odu.cs.cowem.documents.MarkdownDocument#transform(java.lang.String, java.util.Properties)}.
	 */
	@Test
	public void testTransform() {
        properties.put("_includeThis", "1");
		MarkdownDocument doc = new MarkdownDocument(source, proj,
		        properties, mdInput1);
		
		String htmlContent = doc.transform("scroll");
		
		assertTrue (htmlContent.contains("John Doe"));
		assertTrue (htmlContent.contains("something else in"));
		assertTrue (htmlContent.contains(">even"));
	}
	
	/**
	 * Test method for {@link edu.odu.cs.cowem.documents.MarkdownDocument#transform(java.lang.String, java.util.Properties)}.
	 */
	//@Test
	public void testRepeatTransform() {
        properties.put("_includeThis", "1");
        MarkdownDocument doc = new MarkdownDocument(source, proj,
                properties, mdInput1);
		
		String htmlContent = doc.transform("html");
		
		assertTrue (htmlContent.contains("John Doe"));
		assertTrue (htmlContent.contains("something else in"));
		assertTrue (htmlContent.contains(">even"));
		
		
        htmlContent = doc.transform("directory");
		
		assertTrue (htmlContent.contains("John Doe"));
		assertTrue (htmlContent.contains("something else in"));
		assertTrue (htmlContent.contains(">even"));
	}

	
	@Test
	public void testConstructor_File() {
		Path mdInPath = Paths.get("src", "test", "data", "sample.md");
        MarkdownDocument doc = new MarkdownDocument(source, proj,
                properties);

		String htmlContent = doc.transform("scroll");
		
		assertTrue (htmlContent.contains("John Doe"));
		assertTrue (htmlContent.contains("something else in"));
		assertTrue (htmlContent.contains(">even"));
	}

	
	@Test
	public void testPreprocess1() {
        properties.put("_includeThis", "1");
        MarkdownDocument doc = new MarkdownDocument(source, proj,
                properties, mdInput2);
		
		String preprocessed = doc.preprocess("html");
		
		assertEquals (preProcessed1, preprocessed);
		
		assertEquals ("Title of Document", doc.getMetadata("Title"));
		assertEquals ("John Doe", doc.getMetadata("Author"));
		assertEquals ("Jan 1, 2012", doc.getMetadata("Date"));
	}

	
	@Test
	public void testPreprocess2() {
        MarkdownDocument doc = new MarkdownDocument(source, proj,
                properties, mdInput2);
		
		String preprocessed = doc.preprocess("html");
		
		assertEquals (preProcessed2, preprocessed);
		
		assertEquals ("Title of Document", doc.getMetadata("Title"));
		assertEquals ("John Doe", doc.getMetadata("Author"));
		assertEquals ("Jan 1, 2012", doc.getMetadata("Date"));
	}


	@Test
	public void testPreprocess3() {
        MarkdownDocument doc = new MarkdownDocument(source, proj,
                properties, mdInput2);
		
		// Check to be sure that metadata queries do not depend upon
		// nor interfere with pre-processing.
		assertEquals ("Title of Document", doc.getMetadata("Title"));
		assertEquals ("John Doe", doc.getMetadata("Author"));
		assertEquals ("Jan 1, 2012", doc.getMetadata("Date"));
		
		String preprocessed = doc.preprocess("html");
		
		assertEquals (preProcessed2, preprocessed);		
	}

	@Test
	public void testMetadata() {
        MarkdownDocument doc = new MarkdownDocument(source, proj,
                properties, mdInput2);
		
		assertEquals ("Title of Document", doc.getMetadata("Title"));
		assertEquals ("John Doe", doc.getMetadata("Author"));
		assertEquals ("Jan 1, 2012", doc.getMetadata("Date"));
	}

	
	@Test
	public void testCSSMetadata() throws XPathExpressionException {
	    String mdInput = "Title: Title of Document\n"
	            + "Author: John Doe\n"
	            + "CSS: file1.css\n"
	            + "CSS: file2.css\n\n"
	            + "# Section 1\n\n"
	            + "%if _includeThis\n"
	            + "## Section 1.1\n\n"
	            + "A paragraph in\nsection 1.1\n\n"
	            + "%endif\n\n"
	            + "## Section 1.2\n\n"
	            + "A paragraph in\nsection 1.2\n\n"
	            + "Something in _italics_ and\n"
	            + "something else in **bold**\n";

        MarkdownDocument doc = new MarkdownDocument(source, proj,
                properties, mdInput);
		
		assertEquals ("Title of Document", doc.getMetadata("Title"));
		assertEquals ("John Doe", doc.getMetadata("Author"));
		assertEquals ("file1.css\tfile2.css", doc.getMetadata("CSS"));
		
	}

	
	
	
	@Test
	public void testProcess() throws XPathExpressionException {
        MarkdownDocument doc = new MarkdownDocument(source, proj,
                properties, mdInput2);
		
		org.w3c.dom.Document basicHtml = doc.process(preProcessed1);
		Element root = basicHtml.getDocumentElement();
		XPath xPath = XPathFactory.newInstance().newXPath();
		
		String actualTitle = (String)xPath.evaluate("/html/head/title", root);
		assertEquals ("@Title@", actualTitle);

		String actualSection = (String)xPath.evaluate("/html/body/h1", root);
		assertEquals ("Section 1", actualSection);
		
		
		NodeList paragraphs = (NodeList)xPath.evaluate("/html/body//p",
				root, XPathConstants.NODESET);
		assertEquals(3, paragraphs.getLength());
		
		String p1 = paragraphs.item(0).getTextContent();
		assertTrue(p1.contains("A paragraph in"));
		assertTrue(p1.contains("section 1.1"));
	}

	
	@Test
	public void testPassthrough1() throws XPathExpressionException {
        MarkdownDocument doc = new MarkdownDocument(source, proj,
                properties, mdInput2);
		
		String passThrough1 =
				"# Section 1\n\n"
				+ "A paragraph with <br id='br0'/> a forced line break.\n";
		
		
		
		org.w3c.dom.Document basicHtml = doc.process(passThrough1);
		
		Element brEl= getElementById(basicHtml, "br0");
		assertNotNull (brEl);
		assertEquals ("br", brEl.getTagName());
	}

	
	@Test
	public void testPassthrough2() throws XPathExpressionException {
        MarkdownDocument doc = new MarkdownDocument(source, proj,
                properties, mdInput2);
		
		String passThrough2 =
				"# Section 1\n\n"
				+ "A paragraph with <span id='span0'>an internal span</span>.\n";
		
		org.w3c.dom.Document basicHtml = doc.process(passThrough2);
		
		Element spanEl= getElementById(basicHtml, "span0");
		assertNotNull (spanEl);
		assertEquals ("span", spanEl.getTagName());
		assertEquals ("an internal span", spanEl.getTextContent());
	}

	
	@Test
	public void testPassthrough3() throws XPathExpressionException {
        MarkdownDocument doc = new MarkdownDocument(source, proj,
                properties, mdInput2);
		
		String passThrough3 =
				"# Section 1\n\n"
				+ "A paragraph with <arbitraryXML id='xml0' markdown='0'>an\n"
						+ "  xml element</arbitraryXML>.\n";
		
		org.w3c.dom.Document basicHtml = doc.process(passThrough3);
		
		Element xmlEl= getElementById(basicHtml, "xml0");
		assertNotNull (xmlEl);
		assertEquals ("arbitraryXML", xmlEl.getTagName());
		assertEquals ("an  xml element", xmlEl.getTextContent());
	}

	
	@Test
	public void testPassthrough4() throws XPathExpressionException {
        MarkdownDocument doc = new MarkdownDocument(source, proj,
                properties, mdInput2);

		String passThrough4 =
				"# Section 1\n\n"
				+ "A paragraph with <span id='span0'>a _marked up_ span</span>.\n";
		
		org.w3c.dom.Document basicHtml = doc.process(passThrough4);
		Element root = basicHtml.getDocumentElement();
		
		Element spanEl= getElementById(basicHtml, "span0");
		assertNotNull (spanEl);
		assertEquals ("span", spanEl.getTagName());
		
		XPath xPath = XPathFactory.newInstance().newXPath();
		Node italicsNode = (Node)xPath.evaluate("/html/body/p/span/em",
				root, XPathConstants.NODE);
		assertNotNull (italicsNode);
		assertEquals ("marked up", italicsNode.getTextContent());
	}

	
	@Test
	public void testPassthrough5() throws XPathExpressionException {
        MarkdownDocument doc = new MarkdownDocument(source, proj,
                properties, mdInput2);

		String passThrough =
				"# Section 1\n\n"
				+ "A paragraph\n\n"
				+ "* list element 1\n\n"
				+ "    A nested paragraph\n\n"
				+ "    <div id='div0'>\n"
				+ "    content within div\n"
				+ "    </div>\n\n"
				+ "    Another <span id='span0'>nested</span> paragraph\n\n"
				+ "* list element 2";
		
		org.w3c.dom.Document basicHtml = doc.process(passThrough);
		@SuppressWarnings("unused")
        Element root = basicHtml.getDocumentElement();
		
		Element spanEl= getElementById(basicHtml, "span0");
		assertNotNull (spanEl);
		assertEquals ("span", spanEl.getTagName());
		
		Element parEl = (Element)spanEl.getParentNode();
		assertEquals ("p", parEl.getTagName());
		
		Element liEl = (Element)parEl.getParentNode();
		assertEquals ("li", liEl.getTagName());

	
		Element divEl= getElementById(basicHtml, "div0");
		assertNotNull (divEl);
		assertEquals ("div", divEl.getTagName());
		
		Element liEl2 = (Element)divEl.getParentNode();
		assertEquals ("li", liEl2.getTagName());

	}

	
	// @Test
	// Not supported by PegDown processor 
	public void testSubSuperscripts() throws XPathExpressionException {
        MarkdownDocument doc = new MarkdownDocument(source, proj,
                properties, mdInput2);
		
		String passThrough1 =
				"# Section 1\n\n"
				+ "A paragraph with subscripts x~i,j and superscripts 2^k \n\n"
				+ "A paragraph with subscripts x~i,j~ and superscripts 2^k+1^ \n\n";	
		
		org.w3c.dom.Document basicHtml = doc.process(passThrough1);
		Element root = basicHtml.getDocumentElement();
		
		XPath xPath = XPathFactory.newInstance().newXPath();
		Node n = (Node)xPath.evaluate("/head/body/p[1]/sub",
					root, XPathConstants.NODE);
		assertNotNull(n);
		assertEquals ("i", n.getTextContent());		
	}


	@Test
	public void testInlineMath() throws XPathExpressionException {
        MarkdownDocument doc = new MarkdownDocument(source, proj,
                properties, mdInput2);
		
		String passThrough1 =
				"# Section 1\n\n"
				+ "A paragraph with inline math $k+1$.\n\n"
				+ "A paragraph with inline math \\\\( k < m \\\\).\n\n"
				+ "Math does not suppress ordinary markup, so spacing around\n"
				+ "any * and _ characters can be critical.\n"
				+ " \\\\( x * i + y * j \\\\) is not the same as\n"	
				+ " x *i + y* j.\n";	
		
		org.w3c.dom.Document basicHtml = doc.process(passThrough1);
		Element root = basicHtml.getDocumentElement();
		
		XPath xPath = XPathFactory.newInstance().newXPath();
		NodeList nl = (NodeList)xPath.evaluate("//em",
					root, XPathConstants.NODESET);
		assertEquals (1, nl.getLength());
		
		nl = (NodeList)xPath.evaluate("//p",
				root, XPathConstants.NODESET);
		assertEquals (3, nl.getLength());

		Node p = nl.item(0);
		assertTrue (p.getTextContent().contains("$"));
		assertTrue (p.getTextContent().contains("$"));

		p = nl.item(1);
        assertTrue (p.getTextContent().contains("\\("));
		assertTrue (p.getTextContent().contains("\\)"));
		
		p = nl.item(2);
		assertTrue (p.getTextContent().contains("\\("));
		assertTrue (p.getTextContent().contains("\\)"));
	}


	@Test
	public void testDisplayMath() throws XPathExpressionException {
        MarkdownDocument doc = new MarkdownDocument(source, proj,
                properties, mdInput2);
		
		String passThrough1 =
				"# Section 1\n\n"
				+ "$$ k+1 $$\n\n"
				+ "\\\\[ x *i + y* j \\\\]\n\n"	
				+ " x *i + y* j.\n";	
		
		org.w3c.dom.Document basicHtml = doc.process(passThrough1);
		Element root = basicHtml.getDocumentElement();
		
		XPath xPath = XPathFactory.newInstance().newXPath();
		NodeList nl = (NodeList)xPath.evaluate("//p",
				root, XPathConstants.NODESET);
		assertEquals (3, nl.getLength());

		Node p = nl.item(0);
		assertTrue (p.getTextContent().contains("$"));
		assertTrue (p.getTextContent().contains("$"));

		p = nl.item(1);
		assertTrue (p.getTextContent().contains("\\["));
		assertTrue (p.getTextContent().contains("\\]"));
		
	}


	   @Test
	    public void testArrayMath() throws XPathExpressionException {
	        MarkdownDocument doc = new MarkdownDocument(source, proj,
	                properties, mdInput2);
	        
	        String passThrough1 =
	                "# Section 1\n\n"
	                + "$$ k+1 $$\n\n"
	                + "\\\\[ \\begin{array} a & b \\\\ c & d \\\\ \\end{array} \\\\]\n\n" 
	                + "Done.\n";    
	        
	        org.w3c.dom.Document basicHtml = doc.process(passThrough1);
	        Element root = basicHtml.getDocumentElement();
	        
	        XPath xPath = XPathFactory.newInstance().newXPath();
	        NodeList nl = (NodeList)xPath.evaluate("//p",
	                root, XPathConstants.NODESET);
	        assertEquals (3, nl.getLength());

	        Node p = nl.item(0);
	        assertTrue (p.getTextContent().contains("$"));
	        assertTrue (p.getTextContent().contains("$"));

	        p = nl.item(1);
	        assertTrue (p.getTextContent().contains("\\["));
	        assertTrue (p.getTextContent().contains("\\]"));
	        
	    }

	
	
	
	
	@Test
	public void testPostprocess() throws XPathExpressionException, ParserConfigurationException, SAXException, IOException {
		String html0 = "<html><head><title>@Title@</title></head><body>\n"
				+ "<p id='p1'>paragraph 1</p>\n"
				+ "<h1>Section 1</h1>\n"
				+ "<p>paragraph 1</p>\n"
				+ "<img src='something.png'/>\n"
				+ "<hr/>\n"
				+ "<p id='p2'>paragraph 2</p>\n"
				+ "<h2>Section 1.1</h2>"
				+ "<p id='p3'>paragraph 3</p>\n"
				+ "<p id='p4'>paragraph 4</p>\n"
				+ "</body></html>";
		DocumentBuilder b = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		org.w3c.dom.Document basicHtml = b.parse(new InputSource(new StringReader(html0)));
		
        MarkdownDocument doc = new MarkdownDocument(source, proj,
                properties, mdInput2);
		String htmlResult = doc.postprocess(basicHtml, "scroll");
		
		assertTrue (htmlResult.contains("paragraph 1"));
		assertTrue (htmlResult.contains("paragraph 4"));
		assertTrue (htmlResult.contains("Section 1.1"));
		assertTrue (htmlResult.contains("Title of Document"));
		
		org.w3c.dom.Document finalHtml = b.parse(new InputSource(new StringReader(htmlResult)));
		Element root = finalHtml.getDocumentElement();
		XPath xPath = XPathFactory.newInstance().newXPath();
		
		String actualTitle = (String)xPath.evaluate("/html/head/title", root);
		assertEquals ("Title of Document", actualTitle);
		
		NodeList paragraphs = (NodeList)xPath.evaluate("/html/body/p",
				root, XPathConstants.NODESET);
		assertEquals(5, paragraphs.getLength());
		
		String actualPar3 = (String)xPath.evaluate("/html/body/p[@id = 'p3']", root);
		assertEquals ("paragraph 3", actualPar3);
		
		NodeList pages = (NodeList)xPath.evaluate("/html/body/div[@class='page']",
				root, XPathConstants.NODESET);
		assertEquals(0, pages.getLength());
	}
	

	
	@Test
	public void testPostprocessCSS() throws XPathExpressionException, ParserConfigurationException, SAXException, IOException {
		String html0 = "<html><head><title>@Title@</title></head><body>\n"
				+ "<p id='p1'>paragraph 1</p>\n"
				+ "<h1>Section 1</h1>\n"
				+ "<p>paragraph 1</p>\n"
				+ "<img src='something.png'/>\n"
				+ "<hr/>\n"
				+ "<p id='p2'>paragraph 2</p>\n"
				+ "<h2>Section 1.1</h2>"
				+ "<p id='p3'>paragraph 3</p>\n"
				+ "<p id='p4'>paragraph 4</p>\n"
				+ "</body></html>";
		DocumentBuilder b = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		org.w3c.dom.Document basicHtml = b.parse(new InputSource(new StringReader(html0)));
		properties.setProperty("CSS", "file1.css\tfile2.css");
		
        MarkdownDocument doc = new MarkdownDocument(source, proj,
                properties, mdInput2);
		String htmlResult = doc.postprocess(basicHtml, "scroll");
		
		assertTrue(htmlResult.contains("file1.css"));
		assertTrue(htmlResult.contains("file2.css"));
		
	}

	
	
	@Test
	public void testPostprocessWithPaging() 
			throws XPathExpressionException, ParserConfigurationException, SAXException, IOException {
		String html0 = "<html><head><title>@Title@</title></head><body>\n"
				+ "<p id='p0'>paragraph 0</p>\n"
				+ "<h1>Section 1</h1>"
				+ "<p id='p1'>paragraph 1</p>\n"
				+ "<hr/>\n"
				+ "<p id='p2'>paragraph 2</p>\n"
				+ "<h2>Section 1.1</h2>"
				+ "<p id='p3'>paragraph 3</p>\n"
				+ "<p id='p4'>paragraph 4</p>\n"
				+ "</body></html>";
		DocumentBuilder b = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		org.w3c.dom.Document basicHtml = b.parse(new InputSource(new StringReader(html0)));
		
        MarkdownDocument doc = new MarkdownDocument(source, proj,
                properties, mdInput2);
		doc.setDebugMode(true);
		String htmlResult = doc.postprocess(basicHtml, "pages");
		
		assertTrue (htmlResult.contains("paragraph 1"));
		assertTrue (htmlResult.contains("paragraph 4"));
		assertTrue (htmlResult.contains("Section 1.1"));
		assertTrue (htmlResult.contains("Title of Document"));
		
		org.w3c.dom.Document finalHtml = b.parse(new InputSource(new StringReader(htmlResult)));
		Element root = finalHtml.getDocumentElement();
		XPath xPath = XPathFactory.newInstance().newXPath();
		
		String actualTitle = (String)xPath.evaluate("/html/head/title", root);
		assertEquals ("Title of Document", actualTitle);
		
		NodeList paragraphs = (NodeList)xPath.evaluate("/html/body/p",
				root, XPathConstants.NODESET);
		assertEquals(0, paragraphs.getLength());
		
		String actualPar3 = (String)xPath.evaluate("/html/body//p[@id = 'p3']", root);
		assertEquals ("paragraph 3", actualPar3);
		
		NodeList pages = (NodeList)xPath.evaluate("/html/body/div[@class='page']",
				root, XPathConstants.NODESET);
		assertEquals(5, pages.getLength());
		
		Element[] p = new Element[5];
		Element[] par = new Element[5];
		for (int i = 0; i < 5; ++i) {
			p[i] = getElementById(finalHtml, "p" + i);
			assertNotNull(p[i]);
			par[i] = (Element)p[i].getParentNode();
			assertEquals("page", par[i].getAttribute("class"));
		}
		assertNotSame(par[0],  par[1]);
		assertNotSame(par[1],  par[2]);
		assertNotSame(par[2],  par[3]);
		assertSame(par[3],  par[4]);

	}
	
	
	
}