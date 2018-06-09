const puppeteer = require('puppeteer');
const formats = require("./website/styles/pdfFormats.js").formats;

console.log('Node version is: ' + process.version);
(async () => {
  const browser = await puppeteer.launch({headless: true});
  const page = await browser.newPage();
	await page.goto('file://@scrollFile@');
	//await page.goto('file://@testFile@');
	try {
	    await page.waitFor("#mathJaxHasCompleted",
					       {visible: true, timeout: 60000});
        for (formatNum in formats) {
           formatSpec = formats[formatNum];
           console.log ("PDF format: " + formatSpec.name);
           await page.pdf({
              path: 'build/website/' + formatSpec.name + ".pdf",
              width: formatSpec.width, height: formatSpec.height,
              margin: formatSpec.margin 
           });
        }
 	} catch (err) {
 	    console.log(err);    
 	}
	
    await browser.close();
})();
