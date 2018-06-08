const puppeteer = require('puppeteer');

console.log('Node version is: ' + process.version);
(async () => {
  const browser = await puppeteer.launch({headless: true});
  const page = await browser.newPage();
  await page.goto('file://@scrollFile@');
  console.log('path: @pdfFile@, format: @pdfFormat@')
  await page.pdf({path: '@pdfFile@', format: '@pdfFormat@'});

  await browser.close();
})();
