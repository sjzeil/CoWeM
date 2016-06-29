package edu.odu.cs.cwm.documents.urls;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

/**
 * Implements URL rewriting in course documents.
 * 
 *  [text](due:) where text is a date/time in ISO 8601 format
 *           YYYY-MM-DDThh:mm, is formatted so a span of class
 *           date with a more conventional format, preceded by "Due: ".
 * 
 *  No support at the moment for time zones. May add that later.
 *  
 * @author zeil
 *
 */
public class DueDateURLs implements SpecialURL {
    
     
    /**
     * For logging error messages.
     */
    private static Logger logger 
       = LoggerFactory.getLogger(DueDateURLs.class);
    
    /**
     * Format used for rendering combined date-time attributes.
     */
    private static final DateTimeFormatter DT_ATTR_FORMAT 
        = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");


    /**
     * Create a URL rewriter.
     * 
     */
    public DueDateURLs() {
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
	    if ("due:".equals(url)) {
	        String rawDateTime = link.getTextContent();
	        
	        String formattedDateTime = "";
	        String startTimeAttr = "";
	        String endTimeAttr = "";
	        ZoneId timeZone = ZoneId.systemDefault();

	        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
	        try {
	            LocalDateTime dateTime = LocalDateTime.parse(rawDateTime, 
	                    formatter);
	            DateTimeFormatter outputFormat = DateTimeFormatter.ofPattern(
	                    "MM/dd/yyyy, h:mma z");
	            ZonedDateTime zdt  = ZonedDateTime.of(dateTime, timeZone);
	            formattedDateTime = "Due: " + outputFormat.format(zdt);
                startTimeAttr = DT_ATTR_FORMAT.format(zdt);
	            ZonedDateTime zedt = zdt.plusMinutes(1);
	            endTimeAttr = DT_ATTR_FORMAT.format(zedt);
                
	        } catch (DateTimeParseException ex) {
	            formattedDateTime = "";
	        }
            if (formattedDateTime.length() == 0) {
                formatter = DateTimeFormatter.ISO_LOCAL_DATE;
                try {
                    LocalDate date = LocalDate.parse(rawDateTime, 
                            formatter);
                    DateTimeFormatter outputFormat = 
                            DateTimeFormatter.ofPattern(
                               "MM/dd/yyyy");
                    formattedDateTime = "Due: " + outputFormat.format(date);
                    LocalDateTime dateTime = date.atTime(23,58,59);
                    ZonedDateTime zdt  = ZonedDateTime.of(dateTime, timeZone);
                    startTimeAttr = DT_ATTR_FORMAT.format(zdt);
                    ZonedDateTime zedt = zdt.plusMinutes(1);
                    endTimeAttr = DT_ATTR_FORMAT.format(zedt);
                            
                } catch (DateTimeParseException ex) {
                    formattedDateTime = "";
                }
            }
	        if (formattedDateTime.length() == 0) {
	            formatter = DateTimeFormatter.ISO_LOCAL_TIME;
	            try {
	                LocalTime time = LocalTime.parse(rawDateTime, 
	                        formatter);
	                DateTimeFormatter outputFormat = 
	                        DateTimeFormatter.ofPattern(
	                           "h:mma");
	                formattedDateTime = "Due: " + outputFormat.format(time);
	                        
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
                  if (!attr2.getName().equals("href")) {
                      span.getAttributes().setNamedItem(attr2);
                  }
                }
                span.setAttribute("class", "date");
                if (startTimeAttr.length() > 0) {
                    span.setAttribute("startsAt", startTimeAttr);
                }
                if (endTimeAttr.length() > 0) {
                    span.setAttribute("endsAt", endTimeAttr);
                }
	            span.appendChild(doc.createTextNode(formattedDateTime));
	            link.getParentNode().replaceChild(span, link);
	            return true;
	        } else {
	            logger.warn("Unable to parse due date: " + rawDateTime);
	        }
	    }
	    return false;
	}
     	   	
     	    

}
