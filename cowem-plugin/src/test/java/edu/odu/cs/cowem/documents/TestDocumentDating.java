package edu.odu.cs.cowem.documents;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.errors.CorruptObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.util.io.DisabledOutputStream;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
        
        Git git = new Git(repo);
        String existingFile = "TestDocumentDating.java";
        long lastUpdatedOn = 0;

        Iterable<RevCommit> log = git.log().call();
        for (RevCommit commit : log) {
            int commitTime = commit.getCommitTime();
            if (commitTime > lastUpdatedOn) {
                System.err.println(commit.getShortMessage() + " " + commitTime);
                if (commitContains(commit, existingFile, repo)) {
                    lastUpdatedOn = commitTime;
                }
            }
        }
        assertTrue (lastUpdatedOn > 0);
        Calendar dateChanged = new GregorianCalendar();
        dateChanged.setTimeInMillis(1000L * lastUpdatedOn);
        assertEquals(2017, dateChanged.get(Calendar.YEAR));
        assertEquals (4, dateChanged.get(Calendar.MONTH));
        git.close();
    }


    private boolean commitContains(RevCommit commit, 
            String existingFile, 
            Repository repo) 
                    throws MissingObjectException, 
                    IncorrectObjectTypeException, CorruptObjectException, 
                    IOException
    {
        if (commit.getParentCount() == 0) {
            // No parent. (First commit?)
            RevTree tree = commit.getTree(); 
            TreeWalk treeWalk = new TreeWalk(repo);
            treeWalk.addTree(tree);
            treeWalk.setRecursive(true);
            while (treeWalk.next()) {
                String path = treeWalk.getPathString();
                System.out.println("no parent: " + path);
                if (path.endsWith(existingFile)) {
                    treeWalk.close();
                    return true;
                }
            }
            treeWalk.close();
            return false;
        } else {
            RevWalk rw = new RevWalk(repo);
            RevCommit parent = rw.parseCommit(commit.getParent(0).getId());
            DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE);
            df.setRepository(repo);
            df.setDiffComparator(RawTextComparator.DEFAULT);
            df.setDetectRenames(true);
            List<DiffEntry> diffs = df.scan(parent.getTree(), commit.getTree());
            for (DiffEntry diff : diffs) {
                String path = diff.getNewPath();
                System.out.println("parent: " + path);
                if (path.endsWith(existingFile)) {
                    rw.close();
                    df.close();
                    return true;
                }
            }
            rw.close();
            df.close();
            return false;
        }
    }

}
