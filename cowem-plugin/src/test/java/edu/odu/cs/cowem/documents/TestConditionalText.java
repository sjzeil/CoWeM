/**
 * 
 */
package edu.odu.cs.cowem.documents;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;

import java.io.File;
import java.nio.file.Paths;
import java.util.Properties;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author zeil
 *
 */
public class TestConditionalText {
	
	
	private String mdInput1 = "Title: Title of Document\n"
			+ "Author: John Doe\n"
			+ "Date: Jan 1, 2012\n\n"
			+ "# Section Title\n\n"
			+ "%if _includeThis\n"
			+ "conditional text 1\n\n"
			+ "%endif\n\n"
            + "%if _scroll\n"
            + "scroll text\n\n"
            + "%endif\n\n"
            + "%if _slides\n"
            + "slides text\n\n"
            + "%endif\n\n"
            + "final text\n"
			;
	
    private String mdInput2 = "Title: Title of Document\n"
            + "Author: John Doe\n"
            + "Date: Jan 1, 2012\n\n"
            + "# Section Title\n\n"
            + "%if _includeThis\n"
            + "conditional text 1\n\n"
            + "%endif\n\n"
            + "{{{\n"
            + "non-slides text\n\n"
            + "}}}\n\n"
            + "%if _slides\n"
            + "text for slides\n\n"
            + "%endif\n\n"
            + "final text\n"
            ;
    
	
	
	private Properties properties;
	private WebsiteProject proj;
	private File source;
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
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

	/**
	 * Test method for {@link edu.odu.cs.cowem.documents.MarkdownDocument#transform(java.lang.String, java.util.Properties)}.
	 */
	@Test
	public void testIf1() {
        properties.put("_includeThis", "1");
		MarkdownDocument doc = new MarkdownDocument(source, proj,
		        properties, mdInput1);
		
		String htmlContent = doc.transform("scroll");
		
		assertThat (htmlContent, containsString("John Doe"));
		assertThat (htmlContent, containsString("Section Title"));
        assertThat (htmlContent, containsString("conditional text"));
        assertThat (htmlContent, containsString("final text"));
	}

	/**
     * Test method for {@link edu.odu.cs.cowem.documents.MarkdownDocument#transform(java.lang.String, java.util.Properties)}.
     */
    @Test
    public void testIf2() {
        //properties.put("_includeThis", "1");
        MarkdownDocument doc = new MarkdownDocument(source, proj,
                properties, mdInput1);
        
        String htmlContent = doc.transform("scroll");
        
        assertThat (htmlContent, containsString("John Doe"));
        assertThat (htmlContent, containsString("Section Title"));
        assertThat (htmlContent, not(containsString("conditional text")));
        assertThat (htmlContent, containsString("final text"));
    }

    /**
     * Test method for {@link edu.odu.cs.cowem.documents.MarkdownDocument#transform(java.lang.String, java.util.Properties)}.
     */
    @Test
    public void testScrollCondition() {
        //properties.put("_includeThis", "1");
        MarkdownDocument doc = new MarkdownDocument(source, proj,
                properties, mdInput1);
        
        String htmlContent = doc.transform("scroll");
        
        assertThat (htmlContent, containsString("John Doe"));
        assertThat (htmlContent, containsString("Section Title"));
        assertThat (htmlContent, not(containsString("conditional text")));
        assertThat (htmlContent, containsString("scroll text"));
        assertThat (htmlContent, not(containsString("slides text")));
        assertThat (htmlContent, containsString("final text"));
    }

    /**
     * Test method for {@link edu.odu.cs.cowem.documents.MarkdownDocument#transform(java.lang.String, java.util.Properties)}.
     */
    @Test
    public void testSlidesCondition() {
        //properties.put("_includeThis", "1");
        MarkdownDocument doc = new MarkdownDocument(source, proj,
                properties, mdInput1);
        
        String htmlContent = doc.transform("slides");
        
        assertThat (htmlContent, containsString("John Doe"));
        assertThat (htmlContent, containsString("Section Title"));
        assertThat (htmlContent, not(containsString("conditional text")));
        assertThat (htmlContent, not(containsString("scroll text")));
        assertThat (htmlContent, containsString("slides text"));
        assertThat (htmlContent, containsString("final text"));
    }

    /**
     * Test method for {@link edu.odu.cs.cowem.documents.MarkdownDocument#transform(java.lang.String, java.util.Properties)}.
     */
    @Test
    public void testSlideExclusion1() {
        //properties.put("_includeThis", "1");
        MarkdownDocument doc = new MarkdownDocument(source, proj,
                properties, mdInput2);
        
        String htmlContent = doc.transform("scroll");
        
        assertThat (htmlContent, containsString("John Doe"));
        assertThat (htmlContent, containsString("Section Title"));
        assertThat (htmlContent, not(containsString("conditional text")));
        assertThat (htmlContent, containsString("non-slides text"));
        assertThat (htmlContent, not(containsString("text for slides")));
        assertThat (htmlContent, containsString("final text"));
    }
    
    /**
     * Test method for {@link edu.odu.cs.cowem.documents.MarkdownDocument#transform(java.lang.String, java.util.Properties)}.
     */
    @Test
    public void testSlideExclusion2() {
        //properties.put("_includeThis", "1");
        MarkdownDocument doc = new MarkdownDocument(source, proj,
                properties, mdInput2);
        
        String htmlContent = doc.transform("slides");
        
        assertThat (htmlContent, containsString("John Doe"));
        assertThat (htmlContent, containsString("Section Title"));
        assertThat (htmlContent, not(containsString("conditional text")));
        assertThat (htmlContent, not(containsString("non-slides text")));
        assertThat (htmlContent, containsString("text for slides"));
        assertThat (htmlContent, containsString("final text"));
    }
    
    
}