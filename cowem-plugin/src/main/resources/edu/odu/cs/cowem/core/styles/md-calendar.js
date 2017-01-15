var openCloseMap = new Map();

function showOCMap ()
{
    var str = "";
    
    for (var key of openCloseMap.keys()) {
	if (str != "") {
	    str = str + " ";
	}
	str = str + key;
    }
    return str;
}


function setCookie(cname, cvalue) {
    var d = new Date();
    var expirationInDays = 7;
    var cookiePath = document.href;
    d.setTime(d.getTime() + (expirationInDays*24*60*60*1000));
    var expires = "expires="+ d.toUTCString();
    document.cookie = cname + "=" + cvalue + ";" + expires + ";path=" + cookiePath;
}

function getCookie(cname) {
    // From http://www.w3schools.com/js/js_cookies.asp
    var name = cname + "=";
    var decodedCookie = decodeURIComponent(document.cookie);
    var ca = decodedCookie.split(';');
    for(var i = 0; i <ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0) == ' ') {
            c = c.substring(1);
        }
        if (c.indexOf(name) == 0) {
            return c.substring(name.length, c.length);
        }
    }
    return "";
}


function toggleDisplay (sectionName)
{
    var theDiv = document.getElementById(sectionName);
    var theButton = document.getElementById("but" + sectionName);
    if (theDiv) {
	if (theDiv.style.display == 'block') {
            theDiv.style.display = "none";
	    theButton.value = "+";
	    openCloseMap.delete(sectionName);
	} else {
            theDiv.style.display = "block";
	    theButton.value = "-";
	    openCloseMap.set(sectionName, 1);
	}
    }
    setCookie ("openCloseMap", showOCMap());
};

function collapseAll()
{
    var allButtons = document.getElementsByTagName("input");
    for (var i = 0; i < allButtons.length; ++i) {
        var button = allButtons[i];
	if (button.type == "button"
	    && button.getAttribute("class")
	    && button.getAttribute("class") === "expandButton") {
	    var buttonID = button.getAttribute("id");
	    var sectionName = buttonID.substr(3);
	    var theDiv = document.getElementById(sectionName);
	    if (button.value === '-') {
		toggleDisplay(sectionName);
	    }
	}
    }
};

function expandAll()
{
    var allButtons = document.getElementsByTagName("input");
    for (var i = 0; i < allButtons.length; ++i) {
        var button = allButtons[i];
	if (button.type == "button"
	    && button.getAttribute("class")
	    && button.getAttribute("class") === "expandButton") {
	    var buttonID = button.getAttribute("id");
	    var sectionName = buttonID.substr(3);
	    var theDiv = document.getElementById(sectionName);
	    if (button.value === '+') {
		toggleDisplay(sectionName);
	    }
	}
    }
};

function visitPage (url)
{
    location.href = url;
}

function padNum (n) {
    if (n < 10) {
	return '0' + n;
    } else {
	return n;
    }
}


function calendarPageLoad()
{
    var now = new Date();
    var m = padNum(1 + now.getMonth());
    var d = padNum(now.getDate());
    var nowDT = "" + now.getFullYear() + '-' + m + '-' + d;
    var h = padNum(now.getHours());
    var min = padNum(now.getMinutes());
    var s = padNum(now.getSeconds());
    nowDT = nowDT + "T" + h + ":" + min + ":" + s;
    //alert ("nowDT: " + nowDT);
    var allDivs = document.getElementsByTagName("div");
    var lastDiv = null;
    for (var div of allDivs) {
	if (div.getAttribute("class") === "calendarEvent") {
	    var start = div.getAttribute("start");
	    var stop = div.getAttribute("stop");
	    var firstCurrent = -1;
	    var newClass = "";
	    if (nowDT < start) {
		newClass = "futureEvent";
		if (lastDiv != null) {
		    lastDiv.setAttribute('class', 'recentEvent');
		    lastDiv = null;
		}
	    } else if (nowDT > stop) {
		newClass = "pastEvent";
		lastDiv = div;
	    } else {
		newClass = "currentEvent";
		if (lastDiv != null) {
		    lastDiv.setAttribute('class', 'recentEvent');
		    lastDiv = null;
		}
	    }
	    div.setAttribute('class', newClass);
	}
    }
}
