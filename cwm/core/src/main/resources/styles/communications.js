/**
  * Functions facilitating communications about course web content
  */

// Pops up an email window pre-populated with info about the course
//  and web page.
function sendEmail(emailAddress, courseName) {
  var theTitle = document.title;
  var docURL = window.location.href.toString();
  var emailString = "mailto:" + emailAddress
    + "?subject=" + courseName + ": "
    + "[enter a descriptive subject]&body=In regards to " 
    + theTitle 
    + " (" + docURL + "):%0A%0A[enter your message]";
  window.open(emailString, "_self");
}

function footerEmail(emailAddress, courseName) {
    sendEmail (emailAddress, courseName);
}


/**
  * Replaces a <div> element by a listing of relevant
  * threads from a course forum.
  */
function loadCommentsInto (comments, divID, emailID)
{
    if (comments != "") {
	if (emailID != "") {
            var emailLink = document.getElementById(emailID);
            if (emailLink) {
		emailLink.style.display = "none";
            }
	}
	var commentDiv = document.getElementById(divID);
	commentDiv.innerHTML = "(no comments at this time)";
	
	var pageURL = "" + location.href;
	var pageTitle=document.title;
	var scrubbedTitle=pageTitle;
	scrubbedTitle = scrubbedTitle.replace(/\s+/g, ' ');
	scrubbedTitle = scrubbedTitle.replace(/^ /, '');
	scrubbedTitle = scrubbedTitle.replace(/ $/, '');
	scrubbedTitle = scrubbedTitle.replace(/[^A-Za-z0-9. ]/g, ' ');
	scrubbedTitle = scrubbedTitle.replace(/ +/g, ' ');
	
	var commentURL = comments + "listForumByTitle.cgi?";
	commentURL += "title=" + scrubbedTitle.replace(/ /g, '%20'); 

	pageURL = encodeURIComponent(pageURL);
	commentURL += "&url=" + pageURL;

	var xmlhttp;
	if (window.XMLHttpRequest) {
            // code for IE7+, Firefox, Chrome, Opera, Safari
            xmlhttp=new XMLHttpRequest();
	} else {// code for IE6, IE5
            xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
	}
	xmlhttp.onreadystatechange=function() {
	    if (xmlhttp.readyState==4 && xmlhttp.status==200) {
		var listingDoc = xmlhttp.responseXML;
		var listingBody = listingDoc.getElementsByTagName("body");
		listingBody = listingBody[0];
		
		while (commentDiv.lastChild) {
                    commentDiv.removeChild(commentDiv.lastChild);
		}
		
		var child = listingBody.firstChild;
		while (child != null) {
	            var child2 = child.cloneNode(true);
		    commentDiv.appendChild(child2); 
		    child = child.nextSibling;
		}
	    }
	};
	//alert ("commentURL " + commentURL);
	xmlhttp.open("GET", commentURL, true);
	xmlhttp.overrideMimeType("text/xml");
	xmlhttp.send();
    }
}

