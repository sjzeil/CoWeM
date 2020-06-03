/**
 * 
 */
package edu.odu.cs.cowem.documents;

import static org.junit.jupiter.api.Assertions.assertFalse;
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

	private String mdInput4 = "Title: Title of Document\n"
			+ "Author: John Doe\n"
			+ "Date: Jan 1, 2012\n\n"
			+ "* Start a list\n\n"
			+ "    ```plantuml classes='folde rol'\n"
			+ "    Building 0- Room\n"
			+ "    ```\n\n"
			+ "* A list item\n\n";
	
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
	
	/**
	 * Test method for {@link edu.odu.cs.cowem.documents.MarkdownDocument#transform(java.lang.String, java.util.Properties)}.
	 */
	@Test
	public void testImageInjectionNested() {
		MarkdownDocument doc = new MarkdownDocument(source, proj,
		        properties, mdInput4);
		
		String htmlContent = doc.transform("scroll");
		
		assertTrue (htmlContent.contains("<img"));
		assertTrue (htmlContent.contains("plantuml("));
		assertFalse (htmlContent.contains("$idValue"));
		
	}
	
}