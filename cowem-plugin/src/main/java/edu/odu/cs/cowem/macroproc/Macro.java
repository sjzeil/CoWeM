/**
 * 
 */
package edu.odu.cs.cowem.macroproc;

import java.util.ArrayList;
import java.util.List;

/**
 * A text substitution macro
 * 
 * Macros can be called as name or name(arg1, arg2, ...)
 * 
 * The ( ) in a macro call can be replaced by [ ] or { } or &lt; &gt; 
 *  
 * Macro calls are replaced by the macro body, with substitution for the
 * macro parameters. A macro substitution also swallows a single blank
 * preceding the macro name if the macro name starts with an alphanumeric
 * character and if the call is not at the beginning of the string)
 * and a single blank following the macro call if the
 * macro call has no ( ), [ ], { }, or &lt; &gt;.
 * 
 * @author zeil
 *
 */
public class Macro {
	
    /**
     * Name of this macro.
     */
	private String name;
	
	/**
	 * List of parameter names for this macro.
	 */
	private List<String> formalParams;
	
	/**
	 * The body (substitution part) of this macro.
	 */
	private String body;
	
	/**
	 * Create a new macro.
	 * @param name0  name of the macro - can be any string of visible characters
	 * @param params list of formal parameter names. Names should be limited
	 *               to alphanumerics and - _
	 * @param body0  body in which to replace params as a replacement for
	 *                the macro call
	 */
	public Macro (final String name0,  final List<String> params,  
	              final String body0) {
		this.name = name0;
		this.formalParams = new ArrayList<String>();
		this.formalParams.addAll(params);
		this.body = body0;
	}

	/**
	 * Create a new macro with zero parameters.
	 * @param name0  name of the macro - can be any string of visible characters
	 * @param body0  body to use as a replacement for the macro call
	 */
	public Macro (final String name0,  final String body0) {
		this.name = name0;
		this.formalParams = new ArrayList<String>();
		this.body = body0;
		
	}
	
	/**
	 * Apply the macro to a string .
	 * @param target0 string to be processed via this macro. 
	 * @return target0 with macro substitutions, unchanged if macro call
	 *           could not be matched
	 */
	public final String apply (final String target0) {
	    String target = target0;
		int start = 0;
		while (start >= 0 && start < target.length()) {
			int pos = target.indexOf(name, start);
			if (pos < 0) {
				break;
			}
			char firstCharInName = name.charAt(0);
			if (pos > start && Character.isLetterOrDigit(firstCharInName) 
					&& target.charAt(pos - 1) != ' ') {
				start = pos + 1;
				continue;
			}

			char opener = ' ';
			if (pos + name.length() < target.length()) {
				opener = target.charAt(pos + name.length());
			}
			char closer = ' ';
			switch (opener) {
			case '(' : closer = ')'; break;
			case '[' : closer = ']'; break;
			case '{' : closer = '}'; break;
			case '<' : closer = '>'; break;
			default:
			}
			// If closer == ' ', we must be looking at a zero-parmaeter
			// macro.
			if (closer == ' ' && formalParams.size() > 0) {
				// Not a valid macro call
				start = pos + 1;
				continue;
			}
			String[] actualParams;
			int callStart = 0;
			if (pos == start) {
				callStart = start;
            } else if (Character.isLetterOrDigit(firstCharInName)) {
				callStart = pos - 1;
			} else {
				callStart = pos;
			}
			String preMatch = target.substring(0, Math.max(0, callStart));
			String postMatch;
			if (closer == ' ') {
				actualParams = new String[0];
				postMatch = target.substring(Math.min(pos + name.length(),
				                             target.length()));
			} else {
				int closePos = target.indexOf(closer, pos + name.length() + 1);
				if (closePos < 0) {
					start = pos + 1;
					continue;
				}
				postMatch = target.substring(closePos + 1);
				String args = target.substring(pos + name.length() + 1,
				                               closePos);
				if (formalParams.size() > 1) {
				    final int splitLimit = 20;
					actualParams = args.split(",", splitLimit);
				} else {
					actualParams = new String[1];
					actualParams[0] = args;
				}
			}
			if (formalParams.size() == 0 && actualParams.length > 1) {
                start = pos + 1;
                continue;			    
			}
            if (formalParams.size() == 0 && actualParams.length == 1
                    && !actualParams[0].equals("")) {
                start = pos + 1;
                continue;               
            }
			if (formalParams.size() > 0 
			        && actualParams.length != formalParams.size()) {
				start = pos + 1;
				continue;
			}
			String replacement = applySubstitutions(actualParams);
			target = preMatch + replacement + postMatch;
			start = preMatch.length() + replacement.length();
		}
		return target;
	}

	/**
	 * Generate a version of the macro body with substitutions for all formal
	 * parameters.
	 * 
	 * @param actualParams list of values to substitute for the corresponding
	 *                     formal parameters.
	 * @return  Macro body with substitutions.
	 */
	private String applySubstitutions(final String[] actualParams) {
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
			if (matched < 0) {
			    break;
			}
			replacement = replacement.substring(0, pos)
					+ actualParams[matched]
					+ replacement.substring(pos 
					        + formalParams.get(matched).length());
			start = pos + actualParams[matched].length() + 1;
		}
		return replacement;
	}
	
	/**
	 * Render the macro as a string.
	 * @return a readable version of the macro
	 */
	public final String toString() {
		return name + "{" + formalParams + "}=>" + body;
	}

    /**
     * @return the name
     */
    public final String getName() {
        return name;
    }

    /**
     * @param name0 the name to set
     */
    public final void setName(final String name0) {
        this.name = name0;
    }

    /**
     * @return the formalParams
     */
    public final List<String> getFormalParams() {
        return formalParams;
    }

    /**
     * @param formalParams0 the formalParams to set
     */
    public final void setFormalParams(final List<String> formalParams0) {
        this.formalParams = new ArrayList<String>(formalParams0);
    }

    /**
     * @return the body
     */
    public final String getBody() {
        return body;
    }

    /**
     * @param body0 the body to set
     */
    public final void setBody(final String body0) {
        this.body = body0;
    }

}
