package edu.odu.cs.cowem.documents;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Properties;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestDocumentDating {

    
    
    private String mdSource = 
            "Title: A Title\nAuthor: An Author\n\nSome text.\n"; 
    
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
    public void testDateViaGit() {
        MarkdownDocument doc = new MarkdownDocument(source, proj,
                properties, mdSource);
        
        String modDate = doc.getModificationDate(source);

        assertThat (modDate, containsString("2021"));
        assertThat (modDate, containsString("Jul"));
    }
    
    @Test
    public void testDateViaFileMod() throws IOException {
        Path testDataFile = Paths.get("build", "testData", "newDoc.md");
        testDataFile.getParent().toFile().mkdirs();
        BufferedWriter testOut = new BufferedWriter(new FileWriter(testDataFile.toFile()));
        testOut.write(mdSource);
        testOut.close();
        
        MarkdownDocument doc = new MarkdownDocument(testDataFile.toFile(), proj,
                properties);
        
        String modDate = doc.getModificationDate(testDataFile.toFile());

        Calendar now = new GregorianCalendar();
        String year = "" + (now.get(Calendar.YEAR));
        String month = new SimpleDateFormat("MMM").format(now.getTime());
        
        assertThat(modDate, containsString(year));
        assertThat(modDate, containsString(month));
    }

    
    
}
