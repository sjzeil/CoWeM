package edu.odu.cs.macroproc;

import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;

public class TestMacroProc {

	@Test
	public void testMacros() {
		Macro m1 = new Macro("A", "a");
		String[] params = {"a", "b"};
		Macro m2 = new Macro("B", Arrays.asList(params), "B:a.b");
		
		MacroProcessor proc = new MacroProcessor();
		proc.defineMacro(m1);
		proc.defineMacro(m2);
		
		assertEquals ("1a2B:y.z", proc.process("1 A 2 B(y,z)").trim());
	}
	
	@Test
	public void testDefine() {
		Macro m1 = new Macro("A", "a");
		
		MacroProcessor proc = new MacroProcessor();
		assertEquals ("1 A 2 B(y,z)", proc.process("1 A 2 B(y,z)").trim());
		proc.defineMacro(m1);
		assertEquals ("1a2 B(y,z)", proc.process("1 A 2 B(y,z)").trim());
		String s = proc.process("#define (B)(a,b)<B:a.b>");
		assertEquals("", s);
		assertEquals ("1a2B:y.z", proc.process("1 A 2 B(y,z)").trim());
	}

	@Test
	public void testMultiLineDefine() {
		Macro m1 = new Macro("A", "a");
		
		MacroProcessor proc = new MacroProcessor();
		assertEquals ("1 A 2 B(y,z)", proc.process("1 A 2 B(y,z)").trim());
		proc.defineMacro(m1);
		assertEquals ("1a2 B(y,z)", proc.process("1 A 2 B(y,z)").trim());
		String s = proc.process("#define {B}(a,b){a");
		assertEquals("", s);
		s = proc.process("bQ}");
		assertEquals("", s);
		String result = proc.process("B(y,z)");
		assertNotNull (result);
		assertTrue (result.contains("y"));
		assertTrue (result.contains("z"));
		assertTrue (result.contains("Q"));
	}

	
	
	@Test
	public void testIfTrue() {
		Macro m1 = new Macro("A", "a");
		String[] params = {"a", "b"};
		Macro m2 = new Macro("B", Arrays.asList(params), "B:a.b");
		
		MacroProcessor proc = new MacroProcessor();
		proc.defineMacro(m1);
		proc.defineMacro(m2);
		
		assertEquals ("", proc.process("#ifdef A"));
		assertEquals ("abc", proc.process("abc").trim());
		assertEquals ("", proc.process("#endif"));
		assertEquals ("abc", proc.process("abc").trim());
		
		assertEquals ("", proc.process("#ifdef B"));
		assertEquals ("abc", proc.process("abc").trim());
		assertEquals ("", proc.process("#endif"));
		assertEquals ("abc", proc.process("abc").trim());
		
	}
	
	
	@Test
	public void testInclude() {
		MacroProcessor proc = new MacroProcessor();
		
		String s = proc.process("#include <code2html.css>");
		assertNotNull(s);
		assertTrue (s.contains("}"));
		assertTrue (s.contains("{"));
	}
		

	@Test
	public void testIfFalse() {
		Macro m1 = new Macro("A", "a");
		String[] params = {"a", "b"};
		Macro m2 = new Macro("B", Arrays.asList(params), "B:a.b");
		
		MacroProcessor proc = new MacroProcessor();
		proc.defineMacro(m1);
		proc.defineMacro(m2);
		
		assertEquals ("", proc.process("#ifdef C"));
		assertEquals ("", proc.process("abc"));
		assertEquals ("", proc.process("#endif"));
		assertEquals ("abc", proc.process("abc").trim());
	}

	
	@Test
	public void testElseTrue() {
		Macro m1 = new Macro("A", "a");
		String[] params = {"a", "b"};
		Macro m2 = new Macro("B", Arrays.asList(params), "B:a.b");
		
		MacroProcessor proc = new MacroProcessor();
		proc.defineMacro(m1);
		proc.defineMacro(m2);
		
		assertEquals ("", proc.process("#ifdef A"));
		assertEquals ("abc", proc.process("abc").trim());
		assertEquals ("", proc.process("#else"));
		assertEquals ("", proc.process("abc").trim());
		assertEquals ("", proc.process("#endif"));
		assertEquals ("abc", proc.process("abc").trim());		
	}
	
	@Test
	public void testElseFalse() {
		Macro m1 = new Macro("A", "a");
		String[] params = {"a", "b"};
		Macro m2 = new Macro("B", Arrays.asList(params), "B:a.b");
		
		MacroProcessor proc = new MacroProcessor();
		proc.defineMacro(m1);
		proc.defineMacro(m2);
		
		assertEquals ("", proc.process("#ifdef C"));
		assertEquals ("", proc.process("abc"));
		assertEquals ("", proc.process("#else"));
		assertEquals ("abc", proc.process("abc").trim());
		assertEquals ("", proc.process("#endif"));
		assertEquals ("abc", proc.process("abc").trim());		
	}

	@Test
	public void testIfNesting() {
		Macro m1 = new Macro("A", "a");
		String[] params = {"a", "b"};
		Macro m2 = new Macro("B", Arrays.asList(params), "B:a.b");
		
		MacroProcessor proc = new MacroProcessor();
		proc.defineMacro(m1);
		proc.defineMacro(m2);
		
		assertEquals ("", proc.process("#ifdef C"));
		assertEquals ("", proc.process("abc"));
		assertEquals ("", proc.process("#ifdef A"));
		assertEquals ("", proc.process("abc"));
		assertEquals ("", proc.process("#endif"));
		assertEquals ("", proc.process("abc"));
		assertEquals ("", proc.process("#else"));
		assertEquals ("abc", proc.process("abc").trim());
		assertEquals ("", proc.process("#ifdef C"));
		assertEquals ("", proc.process("abc").trim());
		assertEquals ("", proc.process("#endif"));
		assertEquals ("abc", proc.process("abc").trim());		
		assertEquals ("", proc.process("#endif"));
		assertEquals ("abc", proc.process("abc").trim());		
	}
	
	@Test
	public void testCalloutDefine() {
		MacroProcessor proc = new MacroProcessor("%");
		proc.process("%define <\\co1> <> [<span>&#x278a;</span>]");
		assertEquals ("> - <span>&#x278a;</span> a callout", proc.process("> - \\co1  a callout").trim());

		proc = new MacroProcessor("%");
		proc.process("%define <\\bSlide> <title> <");
		proc.process("---");
		proc.process("");
		proc.process("### title {.unnumbered}");
		proc.process(">");
		String s = proc.process("\\bSlide{Implementing ADTs}").trim();
		assertTrue (s.contains("Implementing"));
		assertFalse (s.contains("bSlide"));
		
		proc = new MacroProcessor("%");
		proc.process("%define <\\eSlide> <> <>");
		s = proc.process("\\eSlide").trim();
		assertEquals (0, s.length());
	}
	
	
	@Test
	public void testMacrosOnMultiLines() {
		String[] params1 = {"q"};
		Macro m1 = new Macro("A", Arrays.asList(params1), "[q]");
		String[] params = {"a", "b"};
		Macro m2 = new Macro("B", Arrays.asList(params), "[a](b)");
		
		MacroProcessor proc = new MacroProcessor();
		proc.defineMacro(m1);
		proc.defineMacro(m2);
		
		assertEquals ("abc[d\ne]fg", proc.process("abc A(d\ne)fg").trim());
		assertEquals ("abc[d\ne](fg)h", proc.process("abc B(d\ne,fg)h").trim());
		assertEquals ("abc[de](f\ng)h", proc.process("abc B(de,f\ng)h").trim());
	}


	
	@Test
	public void testFileProcessing() throws IOException {
		File tempInput = File.createTempFile("testFileProcessing", ".txt");
		BufferedWriter testOut = new BufferedWriter(new FileWriter(tempInput));
		testOut.write("#define </firstterm> {newterm} {[[newterm]]}\n");
		testOut.write("Here is an /firstterm{example}.\n");
		testOut.write("Here is a /firstterm{compound term}.\n");
		testOut.write("Here is a /firstterm{split\nterm}.\n");
		testOut.close();
		
		MacroProcessor proc = new MacroProcessor();
		String results = "";
		try {
			results = proc.process(tempInput);
		} finally {
			tempInput.delete();
		}
		assertTrue (results.contains("Here is an"));
		assertTrue (results.contains("Here is an [[example]]."));
		assertTrue (results.contains("Here is a [[compound term]]."));
		assertTrue (results.contains("Here is a [[split\nterm]].")); 
	}
	
	@Test
	public void testIncidentalChar() {
		MacroProcessor proc = new MacroProcessor();
		
		String s = proc.process("#notACommand a b c");
		assertNotNull(s);
		assertEquals ("#notACommand a b c\n", s);
		
		String complexInput="MAINPROG=testpicture\n"
				+ "%.o: %.cpp\n"
				+ "something\n";
		proc = new MacroProcessor("%");
		s = proc.process (complexInput);
		assertEquals (complexInput, s);
	}
	
}
