package edu.odu.cs.cowem.documents;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.TreeWalk;

public class TestDocumentDating {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }
    
   

    @Test
    public void testGitAccess() throws IOException, NoHeadException, GitAPIException {
        File thisDir = new File(".").getAbsoluteFile();
        final String repoName = ".git";
        File repoDir = new File(thisDir, repoName); 
        boolean found = false;
        while (!(found = repoDir.isDirectory())) {
            thisDir = thisDir.getParentFile();
            if (thisDir == null) {
                break;
            }
            repoDir = new File(thisDir, repoName);
        }
        assertTrue (found);
        Repository repo = new FileRepositoryBuilder()
                .setGitDir(repoDir)
                .build();
        assertTrue (repoDir.exists());
        Ref master = repo.findRef("master");
        
        Git git = new Git(repo);
        String existingFile = "TestMarkdownDocument.java";

        ArrayList<RevCommit> commits = new ArrayList<RevCommit>();
        Iterable<RevCommit> log = git.log().call();
        for (RevCommit commit : log) {
            //System.err.println(commit.getShortMessage() + " " + commit.getCommitTime());
            RevTree tree = commit.getTree(); 
            TreeWalk treeWalk = new TreeWalk(repo);
            treeWalk.addTree(tree);
            treeWalk.setRecursive(true);
            CanonicalTreeParser canonicalTreeParser = treeWalk.getTree(0, CanonicalTreeParser.class);
            while (!canonicalTreeParser.eof()) {
                // if the filename matches, we have a match, so set the byte array to return
                String committedFile = canonicalTreeParser.getEntryPathString();  
                if (committedFile.endsWith(existingFile)) {
                    System.err.println("Found " + committedFile + " at " + commit.getCommitTime());
                }
                canonicalTreeParser.next(1);
            }

            commits.add(commit);

        }
        assertTrue(commits.size() > 0);
        
    }

}
