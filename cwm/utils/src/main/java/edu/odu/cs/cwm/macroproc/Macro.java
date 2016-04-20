/**
 * 
 */
package edu.odu.cs.cwm.macroproc;

import java.util.ArrayList;
import java.util.List;

/**
 * A text substitution macro
 * 
 * Macros can be called as name or name(arg1, arg2, ...)
 * 
 * The ( ) in a macro call can be replaced by [ ] or { } or < > 
 *  
 * Macro calls are replaced by the macro body, with substitution for the macro parameters.
 * A macro substitution also swallows a single blank preceding the macro name if the macro name
 * starts with an alphanumeric character and if the call 
 * is not at the beginning of the string) and a single blank following the macro call if the
 * macro call has no ( ), [ ], { }, or < >.
 * 
 * @author zeil
 *
 */
public class Macro {
	
	public String name;
	public ArrayList<String> formalParams;
	public String body;
	
	/**
	 * Create a new macro
	 * @param name  name of the macro - can be any string of visible characters
	 * @param params list of formal parameter names. Names should be limited to alphanumerics and - _
	 * @param body  body in which to replace params as a replacement for the macro call
	 */
	public Macro (String name,  List<String> params,  String body) {
		this.name = name;
		this.formalParams = new ArrayList<String>();
		this.formalParams.addAll(params);
		this.body = body;
	}

	/**
	 * Create a new macro with zero parameters
	 * @param name  name of the macro - can be any string of visible characters
	 * @param body  body to use as a replacement for the macro call
	 */
	public Macro (String name,  String body) {
		this.name = name;
		this.formalParams = new ArrayList<String>();
		this.body = body;
		
	}
	
	/**
	 * Apply the macro to a string 
	 * @param target string to be processed via this macro. 
	 * @return target with macro substitutions, unchanged if macro call could not be matched
	 */
	public String apply (String target) {
		int start = 0;
		while (start >= 0 && start < target.length()) {
			int pos = target.indexOf(name, start);
			if (pos < 0) {
				break;
			}
			char firstCharInName = name.charAt(0);
			if (pos > start && Character.isLetterOrDigit(firstCharInName) && target.charAt(pos-1) != ' ') {
				start = pos + 1;
				continue;
			}

			char opener = ' ';
			if (pos+name.length() < target.length()) {
				opener = target.charAt(pos+name.length());
			}
			char closer = ' ';
			switch (opener) {
			case '(' : closer = ')'; break;
			case '[' : closer = ']'; break;
			case '{' : closer = '}'; break;
			case '<' : closer = '>'; break;
			}
			if (!Character.isWhitespace(opener) && closer == ' ') {
				// Not a valid macro call
				start = pos + 1;
				continue;
			}
			String[] actualParams;
			int callStart = 0;
			if (pos == start)
				callStart = start;
			else if (Character.isLetterOrDigit(firstCharInName)) {
				callStart = pos-1;
			} else {
				callStart = pos;
			}
			String preMatch = target.substring(0, Math.max(0, callStart));
			String postMatch;
			if (Character.isWhitespace(opener)) {
				actualParams = new String[0];
				postMatch = target.substring(Math.min(pos+name.length()+1, target.length()));
			} else {
				int closePos = target.indexOf(closer, pos+name.length()+1);
				if (closePos < 0) {
					start = pos + 1;
					continue;
				}
				postMatch = target.substring(closePos+1);
				String args = target.substring(pos+name.length()+1, closePos);
				if (formalParams.size() > 1) {
					actualParams = args.split(",",20);
				} else {
					actualParams = new String[1];
					actualParams[0] = args;
				}
			}
			if (actualParams.length != formalParams.size()) {
				start = pos + 1;
				continue;
			}
			String replacement = applySubstitutions(actualParams);
			target = preMatch + replacement + postMatch;
			start = preMatch.length() + replacement.length();
		}
		return target;
	}

	private String applySubstitutions(String[] actualParams) {
		String replacement = body;
		int start = 0;
		while (start < replacement.length()) {
			int pos = replacement.length();
			int matched = -1;
			for (int i = 0; i < formalParams.size(); ++i) {
				String formal = formalParams.get(i);
				int k = replacement.indexOf(formal, start);
				if (k >= 0 && k < pos) {
					pos = k;
					matched = i;
				}
			}
			if (matched < 0) break;
			replacement = replacement.substring(0, pos)
					+ actualParams[matched]
					+ replacement.substring(pos+formalParams.get(matched).length());
			start = pos + actualParams[matched].length() + 1;
		}
		return replacement;
	}
	
	public String toString()
	{
		return name + "{" + formalParams + "}=>" + body;
	}

}
