define([
  'jquery',
], function($) {
  var reflect = {
    getFieldValue: function(obj, prop) {
      prop = prop.replace(/\[(\w+)\]/g, '.$1'); // convert indexes to properties
      prop = prop.replace(/^\./, '');           // strip a leading dot
      var fields = prop.split('.');
      for (var i = 0; i < fields.length; i++) {
        var field = fields[i];
        if (field in obj) obj = obj[field];
        else return null;
      }
      return obj;
    },

    setFieldValue: function(obj, prop, val) {
      prop = prop.replace(/\[(\w+)\]/g, '.$1'); // convert indexes to properties
      prop = prop.replace(/^\./, '');           // strip a leading dot
      var fields = prop.split('.');
      for (var i = 0; i < fields.length; i++) {
        var field = fields[i];
        if (field in obj) {
          if(i == fields.length - 1) {
            obj[field] = val;
          } else {
            obj = obj[field];
          }
        } else {
          throw new Error("cannot set field " + field + ", obj is null");
        }
      }
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

