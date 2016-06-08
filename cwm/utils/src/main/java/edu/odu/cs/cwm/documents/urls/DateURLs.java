package edu.odu.cs.cwm.documents.urls;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import edu.odu.cs.cwm.documents.MarkdownDocument;

/**
 * Implements URL rewriting in course documents.
 * 
 *  [text](date:) where text is a date/time in ISO 8601 format
 *           YYYY-MM-DDThh:mm, is formatted so a span of class
 *           date with a more conventional format.
 * 
 *  No support at the moment for time zones. May add that later.
 *  
 * @author zeil
 *
 */
public class DateURLs implements SpecialURL {
    
     
    /**
     * For logging error messages.
     */
    private static Logger logger 
       = LoggerFactory.getLogger(DateURLs.class);
    
    /**
     * Format used for rendering combined date-time items.
     */
    private static final DateTimeFormatter DT_OUTPUT_FORMAT 
        = DateTimeFormatter.ofPattern("MM/dd/yyyy, h:mma");

    /**
     * Format used for rendering combined dates.
     */
    private static final DateTimeFormatter D_OUTPUT_FORMAT 
        = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    /**
     * Format used for rendering combined times.
     */
    private static final DateTimeFormatter T_OUTPUT_FORMAT 
        = DateTimeFormatter.ofPattern("h:mma");
    

    /**
     * Create a URL rewriter.
     * 
     */
    public DateURLs() {
    }

 
	/**
	 * Checks to see if a linking element (a or img) uses a special
	 * protocol label and, if so, attempts to rewrite the element.
	 * 
     * @param link an element containing a URL
     * @param linkAttr name of the attribute containing the URL 
     * @return true if the element has been rewritten.
	 */
	@Override
	public final boolean applyTo(final Element link, final String linkAttr) {
	    String url = link.getAttribute(linkAttr);
	    if ("date:".equals(url)) {
	        String rawDateTime = link.getTextContent();
	        
	        String formattedDateTime = "";
	        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
	        try {
	            LocalDateTime dateTime = LocalDateTime.parse(rawDateTime, 
	                    formatter);
	            formattedDateTime = DT_OUTPUT_FORMAT.format(dateTime);
	             
	            TemporalAccessor endDT = processEndDate(link, linkAttr);
	            if (endDT != null) {
	                if (endDT instanceof LocalDateTime) {
	                    LocalDateTime ldt = (LocalDateTime) endDT;
                        LocalDate dt0 = dateTime.toLocalDate();
                        LocalDate dt1 = ldt.toLocalDate();
                        String formattedEndTime;
                        if (dt0.equals(dt1)) {
                            formattedEndTime =  T_OUTPUT_FORMAT.format(ldt);
                        } else {
                            formattedEndTime = DT_OUTPUT_FORMAT.format(ldt);
                        }
                        formattedDateTime = formattedDateTime + " - " 
                            + formattedEndTime;
                    } else if (endDT instanceof LocalDate) {
                        String formattedEndTime = D_OUTPUT_FORMAT.format(endDT);
                        formattedDateTime = formattedDateTime + " - " 
                            + formattedEndTime;
                    } else if (endDT instanceof LocalTime) {
                        String formattedEndTime = T_OUTPUT_FORMAT.format(endDT);
                        formattedDateTime = formattedDateTime + "-" 
                            + formattedEndTime;
                    } else {
                        logger.error("Obtained endDate of unhandled type " 
                                       + endDT.getClass().getName());
                    }
	            }
	        } catch (DateTimeParseException ex) {
	            formattedDateTime = "";
	        }
            if (formattedDateTime.length() == 0) {
                formatter = DateTimeFormatter.ISO_LOCAL_DATE;
                try {
                    LocalDate date = LocalDate.parse(rawDateTime, 
                            formatter);
                    formattedDateTime = D_OUTPUT_FORMAT.format(date);
                
                    TemporalAccessor endDT = processEndDate(link, linkAttr);
                    if (endDT != null) {
                        if (endDT instanceof LocalDateTime) {
                            LocalDateTime ldt = (LocalDateTime) endDT;
                            String formattedEndTime =  
                                    D_OUTPUT_FORMAT.format(ldt);
                            formattedDateTime = formattedDateTime + " - " 
                                + formattedEndTime;
                        } else if (endDT instanceof LocalDate) {
                            String formattedEndTime = 
                                    D_OUTPUT_FORMAT.format(endDT);
                            formattedDateTime = formattedDateTime + " - " 
                                + formattedEndTime;
                        } else if (endDT instanceof LocalTime) {
                            logger.warn
                                ("Cannot combine a pure starting date ("
                                    + formattedDateTime
                                    + ") with an ending time.");
                        } else {
                            logger.error("Obtained endDate of unhandled type " 
                                           + endDT.getClass().getName());
                        }
                    }
                } catch (DateTimeParseException ex) {
                    formattedDateTime = "";
                }
            }
	        if (formattedDateTime.length() == 0) {
	            formatter = DateTimeFormatter.ISO_LOCAL_TIME;
	            try {
	                LocalTime time = LocalTime.parse(rawDateTime, 
	                        formatter);
	                formattedDateTime = T_OUTPUT_FORMAT.format(time);
	                        
                    TemporalAccessor endDT = processEndDate(link, linkAttr);
                    if (endDT != null) {
                        if (endDT instanceof LocalDateTime) {
                            logger.warn
                            ("Cannot combine a pure starting time ("
                                + formattedDateTime
                                + ") with an ending date.");
                        } else if (endDT instanceof LocalDate) {
                            logger.warn
                            ("Cannot combine a pure starting time ("
                                + formattedDateTime
                                + ") with an ending date.");
                        } else if (endDT instanceof LocalTime) {
                            String formattedEndTime 
                                = T_OUTPUT_FORMAT.format(endDT);
                            formattedDateTime = formattedDateTime + "-" 
                                + formattedEndTime;
                        } else {
                            logger.error("Obtained endDate of unhandled type " 
                                           + endDT.getClass().getName());
                        }
                    }
	            } catch (DateTimeParseException ex) {
	                formattedDateTime = "";
	            }
	        }
	        if (formattedDateTime.length() > 0) {
	            Document doc = link.getOwnerDocument();
	            Element span = doc.createElement("span");
	            NamedNodeMap attrs = link.getAttributes();
	            for (int i = 0; i < attrs.getLength(); i++) {
	              Attr attr2 = (Attr) doc.importNode(attrs.item(i), true);
	              span.getAttributes().setNamedItem(attr2);
	            }
	            span.setAttribute("class", "date");
	            span.appendChild(doc.createTextNode(formattedDateTime));
	            link.getParentNode().replaceChild(span, link);
	            return true;
	        } else {
	            logger.warn("Could not interpret date: " + url);
	        }
	    }
	    return false;
	}
     	    
     	    
    /**
     * Checks to see if an endDate: linking element follows the dateElement
     * and, if so, attempts to rewrite the element.
     * 
     * @param dateElement an element containing a date: URL
     * @param linkAttr name of the attribute containing the URL 
     * @return Date/Time associated with the endDate item if found, null if
     *          no endDate: link was found
     */
    private TemporalAccessor processEndDate(
            final Element dateElement, 
            final String linkAttr) {

        TemporalAccessor result = null;
        
        Node n = dateElement.getNextSibling();
        while (n != null) {
            if ("a".equals(n.getNodeName())) {
                String url = ((Element) n).getAttribute(linkAttr);
                if ("endDate:".equals(url) || "enddate:".equals(url)) {
                    break;
                }
            }
            n = n.getNextSibling();
        }
        if (n == null) {
            return null;
        }
        
        Element link = (Element) n;
        String url = link.getAttribute(linkAttr);

        String rawDateTime = link.getTextContent();
            
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        try {
            result = LocalDateTime.parse(rawDateTime, 
                    formatter);                        
        } catch (DateTimeParseException ex) {
            result = null;
        }
        if (result == null) {
            formatter = DateTimeFormatter.ISO_LOCAL_DATE;
            try {
                result = LocalDate.parse(rawDateTime, 
                        formatter);
            } catch (DateTimeParseException ex) {
                result = null;
            }
        }
        if (result == null) {
            formatter = DateTimeFormatter.ISO_LOCAL_TIME;
            try {
                result = LocalTime.parse(rawDateTime, formatter);                            
            } catch (DateTimeParseException ex) {
                result = null;
            }
        }
        if (result != null) {
            Document doc = link.getOwnerDocument();
            Node parent = link.getParentNode();
            parent.removeChild(link);
        } else {
            logger.warn("Could not interpret end date: " + url);
        }
        return result;
    }

    
    
    
     	    

}
