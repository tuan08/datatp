define([
  'jquery', 
  'underscore'
], function($, _) {
  var __getFlatIndexFieldMappings = function(indexConfigs) {
    var flatIndexFieldMappings = {};
    for(var i = 0; i < indexConfigs.length; i++) {
      var indexConfig = indexConfigs[i];
      for(var key in indexConfig) {
        var mappings = indexConfig[key].mappings;
        for(var mappingKey in mappings) {
          __extractFlatFieldMapping(flatIndexFieldMappings, "_source.", mappings[mappingKey].properties);
        }
      }
    }
    return flatIndexFieldMappings;
  };

  var __extractFlatFieldMapping = function(holder, keyPrefix, properties) {
    for(var key in properties) {
      var field = properties[key];
      if(field.properties) {
        __extractFlatFieldMapping(holder, keyPrefix + key + ".", field.properties)
      } else {
        var fullKey = keyPrefix + key;
        holder[fullKey] = field;
      }
    }
  };

  var hitCreateModel = function(indexConfigs) {
    var model = {
      label: 'Search Hit Model',
      fields: {
        "_index": { label: "Index", required: true  },
        "_id":    { label: "Id",    required: true  },
        "_score": { label: "Score", required: true  }
      }
    };

    var flatIndexFieldMappings = __getFlatIndexFieldMappings(indexConfigs);
    for(var flatFieldName in flatIndexFieldMappings) {
      var label = flatFieldName.replace("_source.", "");
      var fieldConfig = {label: label, mapping: flatIndexFieldMappings[flatFieldName] }
      model.fields[flatFieldName] = fieldConfig;
    }
    return model;
  };

  var model = {
    search: {
      hit: {
        createModel: hitCreateModel
      }
    }
  };

  return model ;
});
