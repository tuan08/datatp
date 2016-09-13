define([
  'jquery',
], function($) {
  var reflect = {
    getFieldValue: function(o, s) {
      s = s.replace(/\[(\w+)\]/g, '.$1'); // convert indexes to properties
      s = s.replace(/^\./, '');           // strip a leading dot
      var a = s.split('.');
      for (var i = 0, n = a.length; i < n; ++i) {
        var k = a[i];
        if (k in o) {
          o = o[k];
        } else {
          return;
        }
      }
      return o;
    },

    flatten: function(data) {
      var result = {};
      function recurse (cur, prop) {
        if (Object(cur) !== cur) {
          result[prop] = cur;
        } else if (Array.isArray(cur)) {
          result[prop] = cur;
        } else {
          var isEmpty = true;
          for (var p in cur) {
            isEmpty = false;
            recurse(cur[p], prop ? prop+"."+p : p);
          }
          if(isEmpty && prop)
            result[prop] = {};
        }
      }
      recurse(data, "");
      return result;
    },

    deepFlatten: function(data) {
      var result = {};
      function recurse (cur, prop) {
        if (Object(cur) !== cur) {
          result[prop] = cur;
        } else if (Array.isArray(cur)) {
          for(var i=0, l=cur.length; i<l; i++)
            recurse(cur[i], prop + "[" + i + "]");
          if (l == 0) result[prop] = [];
        } else {
          var isEmpty = true;
          for (var p in cur) {
            isEmpty = false;
            recurse(cur[p], prop ? prop+"."+p : p);
          }
          if(isEmpty && prop)
            result[prop] = {};
        }
      }
      recurse(data, "");
      return result;
    }
  };

  var util = {
    reflect: reflect
  };

  return util  ;
});

