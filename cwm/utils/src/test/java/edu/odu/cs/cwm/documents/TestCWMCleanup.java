package edu.odu.cs.cwm.documents;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
