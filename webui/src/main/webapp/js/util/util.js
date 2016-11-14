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
        } else if(i == fields.length - 1) {
          obj[field] = val;
        } else {
          throw new Error("cannot set field " + field + ", obj is null");
        }
      }
    },

    deleteField: function(obj, prop) {
      prop = prop.replace(/\[(\w+)\]/g, '.$1'); // convert indexes to properties
      prop = prop.replace(/^\./, '');           // strip a leading dot
      var fields = prop.split('.');
      for (var i = 0; i < fields.length; i++) {
        var field = fields[i];
        if (field in obj) {
          if(i == fields.length - 1) delete obj[field] 
          else obj = obj[field];
        } else {
          throw new Error("cannot delete field " + field + ", obj is null");
        }
      }
    },

    addValueToArray: function(obj, prop, val) {
      var array = reflect.getFieldValue(obj, prop);
      if(array == null) {
        array = [val],
        reflect.setFieldValue(obj, prop, array);
        return;
      }
      if(array.constructor !== Array) throw new Error(prop + " is not an array");
      array.push(val);
    },

    removeValueFromArray: function(obj, prop, val) {
      var array = reflect.getFieldValue(obj, prop);
      if(array == null) return;
      if(array.constructor !== Array) throw new Error(prop + " is not an array");
      var idx = array.indexOf(val);
      if(idx >= 0) array.splice(index, 1);
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

  function patch0(val) { return val > 9 ? val : '0' + val; }

  var formater = {
    time: {
      ddMMyyyyWithTime: function(value) {
        var date  = new Date(value);
        var fVal  = patch0(date.getDate()) + "/" + patch0((date.getMonth() + 1)) + "/" + date.getFullYear() ;
        fVal      = fVal + " " + patch0(date.getHours()) + ":" + patch0(date.getMinutes()) + ":" + patch0(date.getSeconds());
        return fVal;
      }
    }
  };

  formater.time.locale = formater.time.ddMMyyyyWithTime;

  var util = {
    reflect:  reflect,
    formater: formater
  };

  return util  ;
});
