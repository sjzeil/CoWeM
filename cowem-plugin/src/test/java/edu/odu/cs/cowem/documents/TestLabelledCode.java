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
public class TestLabelledCode {
	
	
	


	
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

	

	@Test
	public void testCppCode() {
		String mdInput1 = "Title: Title of Document\n"
				+ "Author: John Doe\n"
				+ "Date: Jan 1, 2012\n\n"
				+ "opening paragraph\n\n"
				+ "```cpp\n"
				+ "std::string s = \"abc\";\n"
				+ "```\n\n"
				+ "A paragraph\n\n";

		MarkdownDocument doc = new MarkdownDocument(source, proj,
		        properties, mdInput1);
		
		String htmlContent = doc.transform("scroll");
		
		assertTrue (htmlContent.contains("<code"));
		assertTrue (htmlContent.contains("class=\"cpp\""));
	}

	@Test
	public void testMermaidCode() {
		String mdInput2 = "Title: Title of Document\n"
				+ "Author: John Doe\n"
				+ "Date: Jan 1, 2012\n\n"
				+ "```mermaid\n"
				+ "graph LR\n"
				+ "A ==> B\n"
				+ "```\n\n"
				+ "A paragraph\n\n";

				MarkdownDocument doc = new MarkdownDocument(source, proj,
		        properties, mdInput2);
		
		String htmlContent = doc.transform("scroll");
		
		assertTrue (htmlContent.contains("<code class=\"mermaid\""));
		assertTrue (htmlContent.contains("mermaid.min.js"));
	}

	@Test
	public void testBasicTicks() {
		String mdInput3 = "Title: Title of Document\n"
				+ "Author: John Doe\n"
				+ "Date: Jan 1, 2012\n\n"
				+ "```\n"
				+ "sh -x ./myScript.sh"
				+ "```\n\n"
				+ "A paragraph\n\n";

		MarkdownDocument doc = new MarkdownDocument(source, proj,
		        properties, mdInput3);
		
		String htmlContent = doc.transform("scroll");
		
		assertTrue (htmlContent.contains("<code"));
	}

	@Test
	public void testIndentedListing() {
		String mdInput4 = "Title: Title of Document\n"
				+ "Author: John Doe\n"
				+ "Date: Jan 1, 2012\n\n"
				+ "opening paragraph\n"
				+ "\n"
				+ "    indentedCode(x);"
				+ "\n\n"
				+ "A paragraph\n\n";

		MarkdownDocument doc = new MarkdownDocument(source, proj,
		        properties, mdInput4);
		
		String htmlContent = doc.transform("scroll");
		
		assertTrue (htmlContent.matches("(?s).*[<]code.*indentedCode.*[<]/code.*"));
	}

	
}