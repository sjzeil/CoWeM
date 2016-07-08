package edu.odu.cs.cowem.macroproc;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

import edu.odu.cs.cowem.macroproc.Macro;

public class TestMacro {

	@Test
	public void testMacro0() {
		Macro m = new Macro("mac", "x");
		assertEquals ("abcxdef", m.apply("abc mac def"));
		assertEquals ("abc x def", m.apply("abc  mac  def"));
		assertEquals ("abc macdef", m.apply("abc macdef"));
		assertEquals ("abcmac def", m.apply("abcmac def"));
		assertEquals ("abcxdefx", m.apply("abc mac def mac"));
		assertEquals ("abc mac() def mac[]", m.apply("abc mac() def mac[]"));
		assertEquals ("abc mac{} def mac<>", m.apply("abc mac{} def mac<>"));
	}

	@Test
	public void testMacro0a() {
		Macro m = new Macro("\\mac", "x");
		assertEquals ("abc xdef", m.apply("abc \\mac def"));
		assertEquals ("abcx def", m.apply("abc\\mac  def"));
	}

	@Test
	public void testMacro1() {
		String[] params = {"z1"};
		Macro m = new Macro("mac", Arrays.asList(params), "[z1]");
		assertEquals ("abc mac def", m.apply("abc mac def"));
		assertEquals ("abc[b]def", m.apply("abc mac(b)def"));
		assertEquals ("abcmac(bb) def", m.apply("abcmac(bb) def"));
		assertEquals ("abc[b,b] def", m.apply("abc mac(b,b) def"));
		assertEquals ("abc[b] def[c]", m.apply("abc mac(b) def mac(c)"));
		assertEquals ("abc[mac] def[mac]", m.apply("abc mac(mac) def mac[mac]"));
		assertEquals ("abc[12] def[()]", m.apply("abc mac{12} def mac<()>"));
	}
	
	@Test
	public void testMultiLine() {
		String[] params = {"z1"};
		Macro m = new Macro("mac", Arrays.asList(params), "[z1]");
		assertEquals ("abc[b]def", m.apply("abc mac(b)def"));
		assertEquals ("abc[b\nc]def", m.apply("abc mac(b\nc)def"));
	}

	@Test
	public void testMacro2() {
		String[] params = {"a", "b"};
		Macro m = new Macro("mac", Arrays.asList(params), "a.b");
		assertEquals ("abc mac def", m.apply("abc mac def"));
		assertEquals ("abc mac(x) def", m.apply("abc mac(x) def"));
		assertEquals ("abcx.y def", m.apply("abc mac(x,y) def"));
		assertEquals ("abc mac(x,y,z) def", m.apply("abc mac(x,y,z) def"));
	}
	
	
	@Test
	public void testMacro3() {
		String[] params = {"a", "b", "cc"};
		Macro m = new Macro("mac", Arrays.asList(params), "a.b.cc");
		assertEquals ("abc mac def", m.apply("abc mac def"));
		assertEquals ("abc mac(x) def", m.apply("abc mac(x) def"));
		assertEquals ("abc mac(x,y) def", m.apply("abc mac(x,y) def"));
		assertEquals ("abcx.y.z def", m.apply("abc mac(x,y,z) def"));
	}

	@Test
	public void testReplacement() {
		String[] params = {"a", "b", "c"};
		Macro m1 = new Macro("A", Arrays.asList(params), "z");
		assertEquals ("zzz", m1.apply("A(,,) A(,,) A(,,)"));
		assertEquals ("zzz", m1.apply("A(,,)A(,,)A(,,)"));
		Macro m2 = new Macro("A", "z");
		assertEquals ("zzz", m2.apply("A A A"));
		//assertEquals ("AAA", m2.apply("AAA"));
	}
	
	@Test
	public void testReplacement2() {
		Macro m1 = new Macro("\\co1", "z");
		assertEquals ("> - z a callout", m1.apply("> - \\co1  a callout"));
	}
	
	
}
