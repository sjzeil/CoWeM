package edu.odu.cs.cowem.documents;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Before;
import org.junit.Test;

import edu.odu.cs.cowem.documents.WebsiteProject;

public class TestWebsiteProject {

    WebsiteProject proj;
    Path projRoot;
    File g1d1;
    File g1d1b;
    File g1d2;
    File g2d3;
    File g2dx;
    
    @Before
    public void setUp() throws Exception {
        projRoot = Paths.get("src/test/data/urlShortcuts");
        g1d1 = projRoot.resolve("Group1/DocSet1/DocSet1.md").toFile();
        g1d1b = projRoot.resolve("Group1/DocSet1/build.gradle").toFile();
        g1d2 = projRoot.resolve("Group1/DocSet2/secondaryDoc1.mmd").toFile();
        g2d3 = projRoot.resolve("Group2/DocSet3/secondaryDoc3.mmd").toFile();
        g2dx = projRoot.resolve("Group1/NotADocSet/secondaryDoc2.mmd").toFile();
                
        proj = new WebsiteProject(projRoot.toFile());
    }

    @Test
    public void testRelativePathToRoot()  {
        Path grandparent = Paths.get("../../");
        Path p = proj.relativePathToRoot(g1d1);
        assertFalse(p.isAbsolute());
        assertEquals(grandparent, p);
        assertEquals(grandparent, proj.relativePathToRoot(g1d1b));
        assertEquals(grandparent, proj.relativePathToRoot(g1d2));
        assertEquals(grandparent, proj.relativePathToRoot(g2d3));
        assertEquals(grandparent, proj.relativePathToRoot(g2dx));

        assertEquals(Paths.get(".."), 
                proj.relativePathToRoot(g2dx.getParentFile()));
    }

    @Test
    public void testRelativePathToDocumentSet() {
        Path p = proj.relativePathToDocumentSet(g1d1b, "DocSet1");
        assertFalse(p.isAbsolute());
        assertEquals (Paths.get("../../Group1/DocSet1"), p);
        p = proj.relativePathToDocumentSet(g1d1b, "DocSet2");
        assertFalse(p.isAbsolute());
        assertEquals (Paths.get("../../Group1/DocSet2"), p);
        p = proj.relativePathToDocumentSet(g1d1b, "DocSet3");
        assertFalse(p.isAbsolute());
        assertEquals (Paths.get("../../Group2/DocSet3"), p);
    }

    @Test
    public void testDocumentSetLocation() {
        assertEquals (g1d1.getParentFile().getAbsoluteFile(), 
                proj.documentSetLocation("DocSet1"));
        assertEquals (g1d2.getParentFile().getAbsoluteFile(), 
                proj.documentSetLocation("DocSet2"));
        assertEquals (g2d3.getParentFile().getAbsoluteFile(), 
                proj.documentSetLocation("DocSet3"));
        assertEquals (null, 
                proj.documentSetLocation("NotADocSet"));        
    }

    @Test
    public void testDocumentSetGroup() {
        assertEquals ("Group1", proj.documentSetGroup("DocSet1"));
        assertEquals ("Group1", proj.documentSetGroup("DocSet2"));
        assertEquals ("Group2", proj.documentSetGroup("DocSet3"));
        assertEquals (null, proj.documentSetGroup("NotADocSet"));
    }

    @Test
    public void testIterator() {
        Set<String> s = new TreeSet<String>();
        for (String docSet: proj) {
            s.add(docSet);
        }
        assertEquals (3, s.size());
        assertTrue (s.contains("DocSet1"));
        assertTrue (s.contains("DocSet2"));
        assertTrue (s.contains("DocSet3"));
    }

    @Test
    public void testGetRootDir() {
        assertEquals (projRoot.toFile().getAbsoluteFile(), proj.getRootDir());
    }

}
