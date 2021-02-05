const puppeteer = require('puppeteer');
const formats = require("./website/styles/pdfFormats.js").formats;

console.log('Node version is: ' + process.version);
(async () => {
	const browser = await puppeteer.launch({headless: true, args: ['--no-sandbox']});
	const page = await browser.newPage();
	var pagePath = '@scrollFile@';
	if (pagePath.charAt(1) == ':') {
		pagePath = '/' + pagePath.replace('\\', '/'); // Correction for Windows file URLs
	}
	try {
	    await page.goto('file://' + pagePath);
		try {
	        await page.waitFor("#mathJaxHasCompleted",
					           {visible: true, timeout: 180000});
		} catch (err) {
			console.log ("Error waiting for mathJax signal\n" + err);
		}
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
	try {
		await browser.close();
	} catch (err) {
		console.log("trying to close browser: " + err);
	}
})();
