package edu.odu.cs.cowem.documents;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.api.Test;

import edu.odu.cs.cowem.documents.ListingInjector;

public class TestListingInjector {

    
    @Test
    public void testApply() {
       
        ListingInjector cleaner = new ListingInjector(new File("."));
        String input = 
                "some html " 
                + "<longlisting file='src/test/data/hello.cpp' id='anID'>"
                + "This should disappear\n"
                + "</longlisting>some more html";
        String output = cleaner.apply(input);
        
        assertTrue (output.contains("some html"));
        assertTrue (output.contains("<longlisting"));
        assertTrue (output.contains("file="));
        assertTrue (output.contains("id="));
        assertTrue (output.contains("anID"));
        assertFalse (output.contains("disappear"));
        assertTrue (output.contains("<iostream>"));
        assertTrue (output.contains("CDATA"));
    }

}
