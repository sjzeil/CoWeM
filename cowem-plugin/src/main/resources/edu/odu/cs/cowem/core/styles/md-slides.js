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


function gotoSlide (control, slideNumber) {
   var oldDisplayed = control.counter;
   var newDisplayed = slideNumber;

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
    //alert (oldSlideID + " => " + newSlideID);
    var oldSlide = document.getElementById(oldSlideID);
    var newSlide = document.getElementById(newSlideID);
    oldSlide.style.display = "none";
    newSlide.style.display = "block";

    var positionIndicatorID = "slideshowposition"  + control.showNumber;
    var posTR = document.getElementById(positionIndicatorID);
    if (posTR != null) {
        posTR.textContent = displayText;
    }
    positionIndicatorID = "slideshowpositionA"  + control.showNumber;
    posTR = document.getElementById(positionIndicatorID);
    if (posTR != null) {
        posTR.textContent = displayText;
    }

 }

function sshow2start (control) {
    gotoSlide(control, 1);
};

function sshow2end (control) {
    gotoSlide(control, control.max);
};

function sshowforward (control) {
    var oldDisplayed = control.counter;
    var newDisplayed = control.counter + 1;
    gotoSlide (control, newDisplayed);
};

function sshowback (control) {
    var oldDisplayed = control.counter;
    var newDisplayed = control.counter - 1;
    gotoSlide (control, newDisplayed);
};

var hashTargets = [];

function collectID (hashTarget, page) {
   hashTargets["#" + hashTarget] = page;
}

function gotoHash (control, hashTarget) {
    page = hashTargets[hashTarget];
    if (typeof page != 'undefined') {
        gotoSlide (control, page+1);
    }
}


function hashHasChanged() {
    var hashPart = location.hash;
    gotoHash (sshowControl0, hashPart);
}



function doKeyPressed (e) {
   // alert ("key pressed " + e.keyCode);
   if (e.keyCode == 39 || e.keyCode == 40 || e.keyCode == 0) {
       sshowforward(sshowControl0);
   } else if (e.keyCode == 37 || e.keyCode == 38 || e.keyCode == 8) {
       sshowback(sshowControl0);
   }
}

document.addEventListener("keydown", doKeyPressed, false);

function slidesStart() {
    hashHasChanged();
}

window.addEventListener("load", slidesStart, false);
