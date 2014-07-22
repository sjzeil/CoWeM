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
