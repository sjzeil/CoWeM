/**
 * 
 */
package edu.odu.cs.cowem.documents;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Paths;
import java.util.Properties;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author zeil
 *
 */
public class TestHtmlLiterals {
	
	
	


	
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
		proj = new WebsiteProject(Paths.get("src/test/data/htmlLiterals")
		        .toFile().getAbsoluteFile());
		source = 
		   Paths.get("src/test/data/htmlLiterals/Group1/DocSet1/DocSet1.md")
		   .toFile();
	}

	

	@Test
	public void testSingleLiteral() {
		String mdInput1 = "Title: Title of Document\n"
				+ "Author: John Doe\n"
				+ "Date: Jan 1, 2012\n\n"
				+ "opening paragraph\n\n"
				+ "<img src='foo.png'/>\n\n"
				+ "A paragraph\n\n";

		MarkdownDocument doc = new MarkdownDocument(source, proj,
		        properties, mdInput1);
		
		String htmlContent = doc.transform("scroll");

		assertTrue (htmlContent.contains("<img"));
		assertTrue (htmlContent.contains("src=\"foo.png\""));
	}

	@Test
	public void testTagPair() {
		String mdInput1 = "Title: Title of Document\n"
				+ "Author: John Doe\n"
				+ "Date: Jan 1, 2012\n\n"
				+ "opening paragraph\n\n"
				+ "<div class='foo'>bar</div>'/>\n\n"
				+ "A paragraph\n\n";

		MarkdownDocument doc = new MarkdownDocument(source, proj,
		        properties, mdInput1);
		
		String htmlContent = doc.transform("scroll");

		
		assertTrue (htmlContent.contains("<div"));
		assertTrue (htmlContent.contains("class=\"foo\""));
		assertTrue (htmlContent.contains(">bar</div>"));
	}

	@Test
	public void testMessyURL() {
		String mdInput1 = "Title: Title of Document\n"
				+ "Author: John Doe\n"
				+ "Date: Jan 1, 2012\n\n"
				+ "opening paragraph\n\n"
				+ "<a href='https://www.bogus.com/foo.cgi?param1=bar@amp@param2=baz'>link</a>'/>\n\n"
				+ "A paragraph\n\n";

		MarkdownDocument doc = new MarkdownDocument(source, proj,
		        properties, mdInput1);
		
		String htmlContent = doc.transform("scroll");

        System.out.println(htmlContent);
		
		assertTrue (htmlContent.contains("<a"));
		assertTrue (htmlContent.contains("href=\"https://www.bogus.com/foo.cgi?param1=bar&param2=baz\""));
	}

	
}