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


var modify_toolbar = function () {
    var toolbar = w3c_slidy.toolbar;
    var counter, page;

    while (toolbar.hasChildNodes()) {
	toolbar.removeChild(toolbar.lastChild);
    }
    var right = w3c_slidy.create_element("div");
    right.setAttribute("style", "float: right; text-align: right");

    counter = w3c_slidy.create_element("span")
    counter.innerHTML = w3c_slidy.localize("slide") + " n/m";
    right.appendChild(counter);
    toolbar.appendChild(right);

    var left = w3c_slidy.create_element("div");
    left.setAttribute("style", "text-align: left");


    var help = w3c_slidy.create_element("a");
    help.setAttribute("href", w3c_slidy.help_page);
    help.setAttribute("title", w3c_slidy.localize(w3c_slidy.help_text));
    help.setAttribute("class", "button");
    help.innerHTML = w3c_slidy.localize("help?");
    left.appendChild(help);


    var gap1 = document.createTextNode(" ");
    left.appendChild(gap1);

    var discuss = w3c_slidy.create_element("a");
    discuss.setAttribute("href", "#(" + w3c_slidy.slides.length + ")");
    discuss.setAttribute("title", "Discuss");
    discuss.setAttribute("class", "button");
    discuss.innerHTML = "Discuss";
    left.appendChild(discuss);

    var gap2 = document.createTextNode(" ");
    left.appendChild(gap2);

    var copyright = w3c_slidy.find_copyright();

    if (copyright)
    {
        var span = w3c_slidy.create_element("span");
        span.className = "copyright";
        span.innerHTML = copyright;
        left.appendChild(span);
    }
    
    w3c_slidy.toolbar.setAttribute("tabindex", "0");
    w3c_slidy.toolbar.appendChild(left);

    counter = w3c_slidy.create_element("div")
    counter.style.position = "absolute";
    counter.style.width = "auto"; //"20%";
    counter.style.height = "1.2em";
    counter.style.top = "auto";
    counter.style.bottom = 0;
    counter.style.right = "0";
    counter.style.textAlign = "right";
    counter.style.color = "red";
    counter.style.background = "rgb(240,240,240)";

    counter.innerHTML = "slide" + " n/m";
    w3c_slidy.toolbar.appendChild(counter);
    w3c_slidy.slide_number_element = counter;
}

if (typeof window.addEventListener != "undefined")
    window.addEventListener("load", modify_toolbar, false);
else
    window.attachEvent("onload", modify_toolbar);
