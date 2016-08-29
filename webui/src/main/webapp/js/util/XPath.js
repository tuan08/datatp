define([
  'jquery',
], function($) {
  var XPathSegment = function(nodeName, id, cssClass) {
    this.nodeName = nodeName;
    this.id       = id;
    this.cssClass = cssClass;

    this.getJSoupXPathExp = function() {
      var exp = this.nodeName;
      if(this.id) {
        exp = exp + '#' + this.id  ;
      } else if(this.cssClass) {
        var cssClassExp = this.cssClass.replace(' ', '.');
        exp = exp + '.' + cssClassExp  ;
      }
      return exp;
    }
  }; 
 
  var XPath = function(node) {
    this.node = node;
    
    this.xpathSegments = [];
    var currNode = this.node;
    while(currNode != null && currNode.nodeName != '#document') {
      var xpathSeg = new XPathSegment(currNode.nodeName, currNode.id, currNode.className);
      this.xpathSegments.push(xpathSeg);
      currNode = currNode.parentNode;
    }
    
    this.getXPathSegments = function() { return this.xpathSegments ; }

    this.getFullJSoupXPathSelectorExp = function() {
      var xpath = null;
      for(var i = 0; i < this.xpathSegments.length; i++) {
        var xpathSeg = this.xpathSegments[i];
        if(xpath == null) xpath = xpathSeg.getJSoupXPathExp();
        else              xpath =  xpathSeg.getJSoupXPathExp() + " > " + xpath; 
      }
      return xpath;
    };

    this.getJSoupXPathSelectorExp = function(limit) {
      if(limit == null) limit = 5;
      if(limit > this.xpathSegments.length) limit = this.xpathSegments.length;
      var xpath = null;
      var idCount = 0, cssClassCount = 0;
      for(var i = 0; i < limit; i++) {
        var xpathSeg = this.xpathSegments[i];

        if(xpathSeg.id) idCount++;
        else if(xpathSeg.cssClass) cssClassCount++;

        if(xpath == null) xpath = xpathSeg.getJSoupXPathExp();
        else              xpath =  xpathSeg.getJSoupXPathExp() + " > " + xpath; 

        if(idCount > 0 || cssClassCount > 3) break;
      }
      return xpath;
    };
  };
  
  return XPath ;
});
