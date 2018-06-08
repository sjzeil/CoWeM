const puppeteer = require('puppeteer');

console.log('Node version is: ' + process.version);
(async () => {
  const browser = await puppeteer.launch({headless: true});
  const page = await browser.newPage();
	//await page.goto('file://@scrollFile@');
	await page.goto('file://@testFile@');
	await page.waitFor("#mathJaxHasCompleted",
					  {visible: true, timeout: 60000});
    console.log('path: @pdfFile@, format: @pdfFormat@');
	
	await page.pdf({path: '@pdfDir@' + 'letter.pdf' , format: 'letter',
				    margin: {top: '0.75in', bottom: '0.5in',
							 left: '0.5in', right: '0.5in'} });
	await page.pdf({path: '@pdfDir@' + '10in-4x3.pdf' ,
					width: "7.75in", height: "5.8in",
				    margin: {top: '0.25in', bottom: '0.25in',
							 left: '0.25in', right: '0.25in'} });
	await page.pdf({path: '@pdfDir@' + '7in-8x5.pdf' ,
					width: "6.0in", height: "3.5in",
				    margin: {top: '0.2in', bottom: '0.2in',
							 left: '0.25in', right: '0.25in'} });

  await browser.close();
})();
