package edu.odu.cs.cowem.documents;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import edu.odu.cs.cowem.documents.CWMcleaner;

public class TestCWMCleanup {

    
    @Test
    public void testApplyOpener() {
        CWMcleaner cleaner = new CWMcleaner();
        assertEquals("<sidebar width='60'>", 
                cleaner.apply("<cwm tag='sidebar' width='60'/>"));
        assertEquals("<sidebar width='60'>", 
                cleaner.apply("<p><cwm tag='sidebar' width='60'/>"));
        assertEquals("<sidebar width='60'>", 
                cleaner.apply("<cwm tag='sidebar' width='60'/></p>"));
        assertEquals("<sidebar width='60'>", 
                cleaner.apply("<p><cwm tag='sidebar' width='60'/></p>"));
    }

    @Test
    public void testApplyCloser() {
        CWMcleaner cleaner = new CWMcleaner();
        assertEquals("</sidebar>", 
                cleaner.apply("<cwm tag='/sidebar'/>"));
        assertEquals("</sidebar>", 
                cleaner.apply("<p><cwm tag='/sidebar'/>"));
        assertEquals("</sidebar>", 
                cleaner.apply("<cwm tag='/sidebar'/></p>"));
        assertEquals("</sidebar>", 
                cleaner.apply("<p><cwm tag='/sidebar'/></p>"));
    }
}
