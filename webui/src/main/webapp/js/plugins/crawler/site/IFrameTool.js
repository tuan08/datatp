define([
  'jquery',
], function($) {

  var IFrameTool = function(iframe) {
    this.iframe = iframe;
 
    this.getHighlightText = function() {
      var iframeWin = this.iframe && this.iframe.contentWindow; 
      var iframeDoc = iframeWin && iframeWin.document; 
      var text = "???";
      if (iframeDoc) { 
        if (iframeDoc.getSelection) { 
          // Most browsers 
          text = String(iframeDoc.getSelection()); 
        } else if (iframeDoc.selection) { 
          // Internet Explorer 8 and below 
          text = iframeDoc.selection.createRange().text; 
        } else if (iframeWin.getSelection) { 
          // Safari 3 
          text = String(iframeWin.getSelection()); 
        } 
      }
      return text; 
    };

    this.getIFrameWindow = function() {
      return this.iframe.contentWindow ;
    };

    this.getIFrameDocument = function() {
      var iframe = this.iframe; 
      var iframeDoc = null;
      if(iframe.contentWindow) iframeDoc = iframe.contentWindow.document ;
      else iframeDoc =  iframe.contentDocument;
      return iframeDoc;
    };
    
     
    this.on = function(name, func) {
      var iframeWin = this.getIFrameWindow();
      iframeWin[name] = func ;
    };

    this.setDomain = function(domain) {
      var iframeDoc = this.getIFrameDocument();
      iframeDoc.domain = domain;
    };

    this.html = function(xhtml) {
      $(this.iframe).contents().find('html').html(xhtml)
    };
  };
  
  return IFrameTool ;
});
