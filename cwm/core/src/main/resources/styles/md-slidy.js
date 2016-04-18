function toggleDisplay (sectionName)
{
    var theDiv = document.getElementById(sectionName);
    var theButton = document.getElementById("but" + sectionName);
  if (theDiv) {
    if (theDiv.style.display == 'none') {
        theDiv.style.display = "block";
	theButton.value = "-";
     } else {
        theDiv.style.display = "none";
	theButton.value = "+";
     }
  }
};

function sshowforward (control) {
    var oldDisplayed = control.counter;
    var newDisplayed = control.counter + 1;
	if (newDisplayed >= control.max) {
        newDisplayed = control.max;
    } else if (newDisplayed < 1) {
        newDisplayed = 1;
    }
	control.counter = newDisplayed;
    var displayText = "" + newDisplayed + " of " + control.max;

	--oldDisplayed;
	--newDisplayed;
    var oldSlideID = "slide-" + control.showNumber + "-" + oldDisplayed;
    var newSlideID = "slide-" + control.showNumber + "-" + newDisplayed;
	var oldSlide = document.getElementById(oldSlideID);
	var newSlide = document.getElementById(newSlideID);
    oldSlide.style.display = "none";
    newSlide.style.display = "block";

	var positionIndicatorID = "islideshowposition"  + control.showNumber;
	var posTR = document.getElementById(positionIndicatorID);
    posTR.textContent = displayText;
};

function sshowback (control) {
    var oldDisplayed = control.counter;
    var newDisplayed = control.counter - 1;
	if (newDisplayed >= control.max) {
        newDisplayed = control.max;
    } else if (newDisplayed < 1) {
        newDisplayed = 1;
    }
	control.counter = newDisplayed;
    var displayText = "" + newDisplayed + " of " + control.max;

	--oldDisplayed;
	--newDisplayed;
    var oldSlideID = "slide-" + control.showNumber + "-" + oldDisplayed;
    var newSlideID = "slide-" + control.showNumber + "-" + newDisplayed;
	var oldSlide = document.getElementById(oldSlideID);
	var newSlide = document.getElementById(newSlideID);
    oldSlide.style.display = "none";
    newSlide.style.display = "block";

	var positionIndicatorID = "islideshowposition"  + control.showNumber;
	var posTR = document.getElementById(positionIndicatorID);
    posTR.textContent = displayText;
};
