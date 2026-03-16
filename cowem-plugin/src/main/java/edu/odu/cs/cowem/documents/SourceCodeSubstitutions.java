/**
 * 
 */
package edu.odu.cs.cowem.documents;

/**
 * A collection of text substitutions, used primarily for
 * mark up of source code with highlighting and callouts.
 * 
 * @author zeil
 *
 */
public class SourceCodeSubstitutions implements TextSubstitutions {
    
    /**
     * HTML code to close a span.
     */
    private static final String CLOSE_SPAN = "</span>";

    
    /**
     * Special substitutions defined for this website processing. These deal
     * mainly with highlighting and inserting callouts into code listings.
     * 
     * This class also implements a "delayed ampersand substitution" in which
     * "@amp@" is replaced by "&".  This is a workaround for PegDown's tendency
     * to step on ampersands that appear within URLs.

     */
    private static final String[] SUBSTITUTION_VALUES = {
            "/*...*/", "&#x22ee;",
            "/*1*/", "&#x2780;",
            "/*2*/", "&#x2781;",
            "/*3*/", "&#x2782;",
            "/*4*/", "&#x2783;",
            "/*5*/", "&#x2784;",
            "/*6*/", "&#x2785;",
            "/*7*/", "&#x2786;",
            "/*8*/", "&#x2787;",
            "/*9*/", "&#x2788;",
            "/*+*/", "<span class='visually-hidden'>[begin highlight]</span><span class='hli'>",
            "/*-*/", "</span><span class='visually-hidden'>[end highlight]</span>",
            "/*+1*/", "<span class='visually-hidden'>[begin highlight]</span><span class='hli'>",
            "/*-1*/", "</span><span class='visually-hidden'>[end highlight]</span>",
            "/*+2*/", "<span class='visually-hidden'>[begin highlight 2]</span><span class='hlii'>",
            "/*-2*/", "</span><span class='visually-hidden'>[end highlight 2]</span>",
            "/*+3*/", "<span class='visually-hidden'>[begin highlight 3]</span><span class='hliii'>",
            "/*-3*/", "</span><span class='visually-hidden'>[end highlight 3]</span>",
            "/*+4*/", "<span class='visually-hidden'>[begin highlight 4]</span><span class='hliv'>",
            "/*-4*/", "</span><span class='visually-hidden'>[end highlight 4]</span>",
            "/*+i*/", "<i>",
            "/*-i*/", "</i>",
            "/*+=*/", "<span class='strike'>",
            "/*-=*/", CLOSE_SPAN,
            "\\%", "%",
            "[_", "<span class='userinput'>",
            "_]", CLOSE_SPAN,
            "@amp@", "&"
    };

    /**
     * Create a substitution set.
     */
    public SourceCodeSubstitutions() {
    }
    
    
    /**
     * Apply substitutions.
     * 
     * @param toString  original string
     * @return original after applying substitutions.
     */
    @Override
    public final String apply(final String toString) {
        String result = toString;
        for (int i = 0; i < SUBSTITUTION_VALUES.length; i += 2) {
            String target = SUBSTITUTION_VALUES[i];
            String value = SUBSTITUTION_VALUES[i + 1];
            result = result.replace(target, value);
        }
        return result;
    }

}
