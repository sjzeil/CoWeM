package edu.odu.cs.cwm.documents;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.odu.cs.cwm.macroproc.MacroProcessor;

/**
 * Implements URL rewriting in course documents. Special forms of URL are:
 * 
 *  doc:foo  where foo contains no '/' or '.' characters and matches a
 *           document set name in a group DIR
 *           baseURL/DIR/foo/index.html
 *  doc:foo  where foo contains one or more '/' or '.' characters and
 *           matches a file (presumably a secondary document or listing)
 *           in a group DIR,
 *           baseURL/DIR/foo.html
 *  docx:foo  same as doc:foo, except during epub package generation when such
 *           links are ignored when choosing files to include in the e-book
 *           (useful for excluding "private" documents such as assignments)
 *           
 *  graphics:foo baseURL/graphics/foo
 *  
 *  styles:foo baseURL/styles/foo
 *  
 *  bb:foo   A link to an internal page of a Blackboard course (requires
 *           a legitimate URL for bbURL in the constructor below)
 * 
 *  The doc: and docx: URLs can also be combined with automatic title
 *  extraction. If the entire text of the link is "TBD", then the
 *  Title: metadata field of the source document is retrieved and
 *  inserted into the link in place of the "TBD". (Currently this
 *  works with primary documents only if the option to change the
 *  name of the primary document source file has not been used.)
 *  
 * @author zeil
 *
 */
public class URLRewriting {
    
    /**
     * Name used for Gradle files in document set directories.
     */
    private static final String GRADLE_FILE_NAME = "website.gradle";

    /**
     * relative URL/directory to base of website.
     */
    private String baseURL;
    
    /**
     * URL of a Blackboard course.
     */
    private String bbURL;
    
    
    /**
     * List of paths to document sets that can be reached from the baseURL. 
     */
    private List<File> documentSets;
    
    
    /**
     * For logging error messages.
     */
    private static Logger logger 
       = LoggerFactory.getLogger(MacroProcessor.class);


    /**
     * Create a URL rewriter.
     * 
     * @param baseURL0 relative URL/directory to base of website.
     * @param bbURL0  URL of a Blackboard course
     */
    public URLRewriting(final String baseURL0, final String bbURL0) {
        baseURL = baseURL0;
        bbURL = bbURL0;
        
        // Look for all possible document sets - directories 2 levels below
        // the base that contain an appropriately named gradle file.
        documentSets = new ArrayList<>();
        File baseDir = new File(baseURL.replace('/', File.separatorChar));
        if (baseDir.canRead()) {
            try {
                for (String groupName: baseDir.list()) {
                    File group = new File(baseDir, groupName);
                    if (!group.canRead() || !group.isDirectory()) {
                        continue;
                    }
                    try {
                        for (String docSetName: group.list()) {
                            File docSet = new File(group, docSetName);
                            if (!docSet.canRead() || !docSet.isDirectory()) {
                                continue;
                            }
                            File groupGradle = 
                                    new File(docSet, GRADLE_FILE_NAME);
                            if (groupGradle.exists()) {
                                documentSets.add(docSet);
                            }
                        }
                    } catch (SecurityException ex) {
                        logger.warn("Could not list contents of " + group);
                    } 
                }
            } catch (SecurityException ex) {
                logger.warn("Could not list contents of " + baseDir);
            }
        } else {
            logger.warn("Could not find directory " + baseURL);
        }
    }

    /**
     * Rewrite href attributes of "a" elements and src attributes of "img" 
     * and script elements that match the patterns listed above. Attributes 
     * not matching any such pattern are left unchanged.
     *  
     * @param htmlDoc the DOM of an HTML document.
     */
    public final void rewrite(final org.w3c.dom.Document htmlDoc) {
       NodeList allAElements = htmlDoc.getElementsByTagName("a");
       rewrite (allAElements, "href");
       NodeList allImgElements = htmlDoc.getElementsByTagName("img");
       rewrite (allImgElements, "src");
       NodeList allScriptElements = htmlDoc.getElementsByTagName("script");
       rewrite (allScriptElements, "src");
    }

    /**
     * Search a list of elements for link attributes written in one of
     * the special forms listed above, rewriting that attribute when
     * found.  "doc:"-style links also get a "class='doc'" attribute
     * added to facilitate later epub packaging.
     *   
     * @param elements  elements to be examined and, possibly, rewritten
     * @param linkAttribute name of the attribute containing a link URL
     */
    private void rewrite(final NodeList elements, final String linkAttribute) {
        for (int i = 0; i < elements.getLength(); ++i) {
            rewrite ((Element) elements.item(i), linkAttribute);
        }
    }

    /**
     * Examine an element for a link attribute written in one of
     * the special forms listed above, rewriting that attribute if
     * found.  "doc:"-style links also get a "class='doc'" attribute
     * added to facilitate later epub packaging.
     *   
     * @param element  element to be examined and, possibly, rewritten
     * @param linkAttribute name of the attribute containing a link URL
     */

    private void rewrite(final Element element, final String linkAttribute) {
        String link = element.getAttribute(linkAttribute);
        if ("".equals(link)) {
            return;
        }
        int dividerPos = link.indexOf(':');
        if (link.startsWith("doc:") || link.startsWith("docx:")) {
            String documentSpec = link.substring(dividerPos + 1);
            if (documentSpec.contains(".") || documentSpec.contains("/")) {
                documentSpec = documentSpec.replace('/', File.separatorChar);
                List<File> candidates = new ArrayList<>();
                for (File documentSet: documentSets) {
                    File document = new File(documentSet, documentSpec);
                    if (document.exists()) {
                        candidates.add(document);
                    }
                }
                if (!candidates.isEmpty()) {
                    if (candidates.size() > 1) {
                        logger.warn("Ambiguous URL shorthand: " + link
                                + "\n  matched " + candidates.size() 
                                + " documents. Using " + candidates.get(0));
                    }
                    File selected = candidates.get(0);
                    String newLink = selected.toString()
                            .replace(File.separatorChar, '/')
                            + ".html";
                    element.setAttribute(linkAttribute, newLink);
                    if (link.startsWith("doc:")) {
                        element.setAttribute("class", "doc");
                    }
                    attemptTBDReplacement(element, selected);
                } else {
                    logger.warn(
                        "Could not find a replacement for URL shorthand: "
                        + link);
                }
            } else {
                List<File> candidates = new ArrayList<>();
                for (File documentSet: documentSets) {
                    String documentSetName = documentSet.getName();
                    if (documentSpec.equals(documentSetName)) {
                        candidates.add(documentSet);
                    }
                }
                if (!candidates.isEmpty()) {
                    if (candidates.size() > 1) {
                        logger.warn("Ambiguous URL shorthand: " + link
                                + "\n  matched " + candidates.size() 
                                + " documents. Using " + candidates.get(0));
                    }
                    File selected = candidates.get(0);
                    String newLink = selected.toString()
                            .replace(File.separatorChar, '/')
                            + "/index.html";
                    element.setAttribute(linkAttribute, newLink);
                    if (link.startsWith("doc:")) {
                        element.setAttribute("class", "doc");
                    }
                    File sourceFile = new File(selected, 
                            selected.getName() + ".md");
                    attemptTBDReplacement(element, sourceFile);
                } else {
                    logger.warn(
                        "Could not find a replacement for URL shorthand: "
                        + link);
                }                
            }
        } else if (link.startsWith("graphics:")) {
            String documentSpec = link.substring(dividerPos + 1);
            File baseDir = new File(baseURL.replace('/', File.separatorChar));
            File graphicsDir = new File(baseDir, "graphics");
            File selected = new File(graphicsDir, documentSpec); 
            String newLink = selected.toString()
                    .replace(File.separatorChar, '/');
            element.setAttribute(linkAttribute, newLink);
        } else if (link.startsWith("styles:")) {
            String documentSpec = link.substring(dividerPos + 1);
            File baseDir = new File(baseURL.replace('/', File.separatorChar));
            File graphicsDir = new File(baseDir, "styles");
            File selected = new File(graphicsDir, documentSpec); 
            String newLink = selected.toString()
                    .replace(File.separatorChar, '/');
            element.setAttribute(linkAttribute, newLink);
        } else if (link.startsWith("bb:")) {
            String documentSpec = link.substring(dividerPos + 1);
            if ("".equals(bbURL)) {
                logger.warn("Could not resolve URL shorthand " + link
                        + "\n  because base Blackboard URL has not been"
                        + " specified as part of website properties.");
            } else {
                String urlSpec = "url=";
                String urlStart = bbURL.substring(0, bbURL.indexOf(urlSpec)) 
                        + urlSpec;
                documentSpec = link.substring(dividerPos + 1);
                String webAppsSpec = "/webapps";
                int k = documentSpec.indexOf(webAppsSpec);
                if (k < 0) {
                    logger.warn ("Could not resolve URL shorthand " + link);
                    return;
                }
                documentSpec = documentSpec.substring(k);
                documentSpec = documentSpec.replaceAll("/", "%2f");
                documentSpec = documentSpec.replaceAll("=", "%36");
                documentSpec = documentSpec.replaceAll("&", "%26");
                documentSpec = documentSpec.replaceAll("[?]", "%3f");
                String newLink = urlStart + documentSpec;
                element.setAttribute(linkAttribute, newLink);
            }
        }
    }

    /**
     * Checks to see if the entire text of an element is "TBD".
     * If so, attempts to replace that text by the Title metadata field
     * from the source document.
     * 
     * @param element link element to examine
     * @param sourceFile  Markdown source document 
     */
    private void attemptTBDReplacement(final Element element, 
            final File sourceFile) {
        String textContent = element.getTextContent().trim();
        if ("TBD".equals(textContent) && sourceFile.canRead()) {
            MarkdownDocument sourceDoc = new MarkdownDocument(sourceFile,
                    new Properties());
            String title = (String) sourceDoc.getMetadata("Title");
            if (title != null && title.length() > 0) {
                while (element.hasChildNodes()) {
                    element.removeChild(element.getFirstChild());
                }
                Node newTitle = element.getOwnerDocument()
                        .createTextNode(title);
                element.appendChild(newTitle);
            }
        }
    }

}
