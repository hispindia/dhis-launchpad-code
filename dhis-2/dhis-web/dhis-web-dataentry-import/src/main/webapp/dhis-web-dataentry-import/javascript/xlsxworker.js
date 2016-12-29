/* xlsx.js (C) 2013-2014 SheetJS -- http://sheetjs.com */
/* uncomment the next line for encoding support */
//importScripts('dist/cpexcel.js');
importScripts('/dhis-web-dataentry-import/javascript/jszip.js');
importScripts('/dhis-web-dataentry-import/javascript/xlsx.js');
/* uncomment the next line for ODS support */
importScripts('/dhis-web-dataentry-import/javascript/ods.js');
postMessage({t:"ready"});

onmessage = function (oEvent) {
  var v;
  try {
    v = XLSX.read(oEvent.data.d, {type: oEvent.data.b ? 'binary' : 'base64'});
  } catch(e) { postMessage({t:"e",d:e.stack||e}); }
  postMessage({t:"xlsx", d:JSON.stringify(v)});
};
