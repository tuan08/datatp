"trict";
define( [
  'jquery', 
  'QUnit',
  'util/XPath'
], function($, QUnit, XPath) {
  var run = function() {
    QUnit.test("util.XPath", function(assert) {
      var tableXPath = new XPath($('#table')[0]);
      assert.ok('HTML > BODY > DIV.UIData.container > TABLE#table' == tableXPath.getFullJSoupXPathSelectorExp(), "full xpath expression" );
      var thXPath = new XPath($('#table th')[0]);
      console.log(thXPath.getJSoupXPathSelectorExp());
      assert.equal('HTML > BODY > DIV.UIData.container > TABLE#table', tableXPath.getFullJSoupXPathSelectorExp(), "Passed!" );
    });
  };
  return {run: run}
});

