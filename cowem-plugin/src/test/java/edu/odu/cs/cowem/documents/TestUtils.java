package edu.odu.cs.cowem.documents;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.odu.cs.cowem.documents.Utils;

public class TestUtils {

	String message ="abc\ndef\nghi\njkl\nmno\npqr\n";
	
	@Test
	public void testExtractContext() {
		assertEquals("*** abc\n    def\n    ghi\n",
				Utils.extractContext(message, 0, -1));
		assertEquals("    abc\n*** def\n    ghi\n    jkl\n",
				Utils.extractContext(message, 1, -1));
		assertEquals("    abc\n    def\n*** ghi\n    jkl\n    mno\n",
				Utils.extractContext(message, 2, -1));
		assertEquals("    abc\n    def\n*** ghi\n*** ^\n    jkl\n    mno\n",
				Utils.extractContext(message, 2, 0));
		assertEquals("    abc\n    def\n*** ghi\n***  ^\n    jkl\n    mno\n",
				Utils.extractContext(message, 2, 1));
		assertEquals("    abc\n    def\n*** ghi\n***   ^\n    jkl\n    mno\n",
				Utils.extractContext(message, 2, 2));
		assertEquals("    abc\n    def\n*** ghi\n    jkl\n    mno\n",
				Utils.extractContext(message, 2, 3));
		assertEquals("    def\n    ghi\n*** jkl\n    mno\n    pqr\n",
				Utils.extractContext(message, 3, -1));
		assertEquals("    ghi\n    jkl\n*** mno\n    pqr\n",
				Utils.extractContext(message, 4, -1));
		assertEquals("    jkl\n    mno\n*** pqr\n",
				Utils.extractContext(message, 5, -1));
	}

}
