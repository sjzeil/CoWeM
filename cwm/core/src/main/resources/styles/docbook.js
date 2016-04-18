function newThread()
   {
   var pageURL = "" + location.href;
   var pageTitle=document.title;
   pageTitle = pageTitle.replace(/[^- !-%(-;?-~]/g, ' ');
   document.getElementById("createThreadURL").value = pageURL;
   document.getElementById("createThreadTitle").value = pageTitle;
 //alert ("pageTitle= " + document.getElementById("createThreadTitle").value);
    document.getElementById("createThread").submit();

}

function message (encodedAddr, title, pathToBase)
{
    var addr = encodedAddr.replace(/#/g, '@');
    addr = addr.replace(/!/g, 'a');
    addr = addr.replace(/%/g, 'e');
    addr = addr.replace(/:/g, 'i');
    addr = addr.replace(/;/g, 'o');
    addr = addr.replace(/=/g, 'u');

    var pageURL = location.href;
    var pageTitle=document.title;

    if (addr.indexOf("moodle@") > -1) {
	// Use a Moodle forum. Address format is
	// moodle@courseID@forumID
        var pieces = addr.split('@');
	var url = pathToBase + "/moodleCreateThread.html?"
	    + "forum1=" + pieces[1]
	    + "&forum2=" + pieces[2]
	    + "&pageURL=" + pageURL
	    + "&title=" + encodeURIComponent(pageTitle)
	    ;
	win = window.open(url, 'messageWindow');
    } else {
	// Use email
	var subject = escape(title + ": <your subject line>");

	var openingLines = encodeURIComponent
	    ('With regard to: "' + pageTitle
	     + '"\r\n  at ' + pageURL + "\r\n\r\n<your message>");

	var mailToURL = "mailto:" + addr + "?subject=" + subject
	    + "&body=" + openingLines;

	win = window.open(mailToURL,'messageWindow');
    }
    //if (win && win.open &&!win.closed) win.close();

}


function loadComments (comments, iframeID)
{
   var iframe = document.getElementById(iframeID);
   var pageURL = "" + location.href;
   var pageTitle=document.title;
   pageTitle = pageTitle.replace(/[^- !-%(-;?-~]/g, ' ');
   document.getElementById("createThreadURL").value = pageURL;
   document.getElementById("createThreadTitle").value = pageTitle;
   pageURL = pageURL.replace(/%7E/g, '~');
   //alert ("pageURL= " + pageURL);
   pageURL = pageURL.replace(/#.*$/,'');
   //alert ("pageURL= " + pageURL);
   pageURL = pageURL.replace(/[^-0-9A-Za-z._]/g, '_');
   iframe.src = comments + "/Comments/" + pageURL + ".html";
}

function commentsLoaded (iframeContent) {
   document.getElementById("commentary").innerHTML=iframeContent;
}

function syncWith (specifier) {
    parent.tracker.syncWithPage(specifier);
}
