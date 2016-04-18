function toggleDisplay (sectionName)
{
    var theDiv = document.getElementById(sectionName);
    var theButton = document.getElementById("but" + sectionName);
  if (theDiv) {
    if (theDiv.style.display == 'block') {
        theDiv.style.display = "none";
	theButton.value = "+";
     } else {
        theDiv.style.display = "block";
	theButton.value = "-";
     }
  }
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
