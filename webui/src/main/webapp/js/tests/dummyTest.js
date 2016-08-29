"use strict";
define( [
 'QUnit' 
], function(QUnit) {
  var run = function() {
    QUnit.test( "hello test", function(assert) {
      assert.ok(1 + 1 == 2, "ok assert passes!" );
      assert.equal(1 + 1, 2, "equal assert passes!" );
    });
  };
  return {run: run}
});

