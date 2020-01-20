/**
 * 
 */
package edu.odu.cs.cowem.documents;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
public class TestPlantUMLInjection {
	
	
	private String mdInput1 = "Title: Title of Document\n"
			+ "Author: John Doe\n"
			+ "Date: Jan 1, 2012\n\n"
			+ "```plantuml\n"
			+ "Building 0- Room\n"
			+ "```\n\n"
			+ "A paragraph\n\n";
	
	private String mdInput2 = "Title: Title of Document\n"
			+ "Author: John Doe\n"
			+ "Date: Jan 1, 2012\n\n"
			+ "```plantuml class=folderol\n"
			+ "Building 0- Room\n"
			+ "```\n\n"
			+ "A paragraph\n\n";

	private String mdInput3 = "Title: Title of Document\n"
			+ "Author: John Doe\n"
			+ "Date: Jan 1, 2012\n\n"
			+ "```plantuml classes='folde rol'\n"
			+ "Building 0- Room\n"
			+ "```\n\n"
			+ "A paragraph\n\n";

	
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

	

	/**
	 * Test method for {@link edu.odu.cs.cowem.documents.MarkdownDocument#transform(java.lang.String, java.util.Properties)}.
	 */
	@Test
	public void testImageInjection() {
		MarkdownDocument doc = new MarkdownDocument(source, proj,
		        properties, mdInput1);
		
		String htmlContent = doc.transform("scroll");
		
		assertTrue (htmlContent.contains("<img"));
		assertTrue (htmlContent.contains("plantuml("));
		assertFalse (htmlContent.contains("$idValue"));
		
	}

	/**
	 * Test method for {@link edu.odu.cs.cowem.documents.MarkdownDocument#transform(java.lang.String, java.util.Properties)}.
	 */
	@Test
	public void testImageInjectionWithClass() {
		MarkdownDocument doc = new MarkdownDocument(source, proj,
		        properties, mdInput2);
		
		String htmlContent = doc.transform("scroll");
		
		assertTrue (htmlContent.contains("<img"));
		assertTrue (htmlContent.contains("plantuml("));
		assertTrue (htmlContent.contains("class=\"folderol\"") || htmlContent.contains("class='folderol'"));
		assertFalse (htmlContent.contains("$idValue"));
		
	}

	/**
	 * Test method for {@link edu.odu.cs.cowem.documents.MarkdownDocument#transform(java.lang.String, java.util.Properties)}.
	 */
	@Test
	public void testImageInjectionWithClassed() {
		MarkdownDocument doc = new MarkdownDocument(source, proj,
		        properties, mdInput3);
		
		String htmlContent = doc.transform("scroll");
		
		assertTrue (htmlContent.contains("<img"));
		assertTrue (htmlContent.contains("plantuml("));
		assertTrue (htmlContent.contains("class=\"folde rol\"") || htmlContent.contains("class='folde rol'"));
		assertFalse (htmlContent.contains("$idValue"));
		
	}
	
	
}