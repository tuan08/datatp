define([
  'jquery',
  'util/util',
], function($, util) {
  var postDslQuery = function(restPath, dslQuery) {
    var returnData = null ;
    $.ajax({ 
      url: restPath,
      type: "POST",
      data: JSON.stringify(dslQuery) ,
      async: false ,
      dataType: "json",

      error: function(data) { console.log("Error: \n" + data) ; },

      success: function(data) { returnData = data ; }
    });
    return returnData ;
  };
  
  var getDslQuery = function(restPath, dslQuery) {
    var returnData = null ;
    $.ajax({ 
      url: restPath,
      type: "GET",
      data: dslQuery ,
      async: false ,
      dataType: "json",

      error: function(data) { console.log("Error: \n" + data) ; },

      success: function(data) { returnData = data ; }
    });
    return returnData ;
  };
  
  var IndexFieldsMapping = function() {
    this.fieldsMapping = {};
    
    this.getFieldsMapping = function() { return this.fieldsMapping; };
    
    this.addFieldsMapping = function(indexConfig) {
      for(var key in indexConfig) {
        var mappings = indexConfig[key].mappings;
        for(var mappingKey in mappings) {
          this._extractFlatFieldMapping("", mappings[mappingKey].properties);
        }
      }
    };
    
    this.getFieldMapping = function(flatKey) { 
      return this.fieldsMapping[flatKey] ;
    };
    
    this._extractFlatFieldMapping = function(keyPrefix, properties) {
      for(var key in properties) {
        var field = properties[key];
        if(field.properties) {
          this._extractFlatFieldMapping(keyPrefix + key + ".", field.properties)
        } else {
          var fullKey = keyPrefix + key;
          this.fieldsMapping[fullKey] = field;
        }
      }
    };
  };
  
  var ESQueryResult = function() {
    this.hits = [];
    this.fieldStates = {};
    this.queryHistories = [];

    this.clearResult = function(query, result) {
      this.hits = [];
      this.queryHistories = [];
    };

    this.addQueryResult = function(query, result) {
      var qHistory = {
        query: query,
        resultInfo: {
          took: result.took,
          timeOut: result.time_out,
          shards: result._shards,
          hitTotal: result.hits.total,
          hitReturn: result.hits.hits.length
        }
      };
      this.queryHistories.push(qHistory);

      var hits = result.hits.hits ;
      for(var i = 0; i < hits.length; i++) {
        var hit = hits[i];
        this._collectHitInfo(hit);
        this.hits.push(hit);
      }
    };

    this._collectHitInfo = function(hit) {
      if(!this.fieldStates["_index"]) {
        this.fieldStates["_index"] = { count: 1 };
        this.fieldStates["_score"] = { count: 1 };
        this.fieldStates["_id"]    = { count: 1 };
      } else {
        this.fieldStates["_index"].count++;
        this.fieldStates["_score"].count++;
        this.fieldStates["_id"].count++;
      }
      var result = util.reflect.flatten(hit._source);
      for(var key in result) {
        if(this.fieldStates[key]) this.fieldStates[key].count++;
        else                      this.fieldStates[key] = { count: 1 };
      }
    };
  };

  var ESQueryContext = function(restURL, indices, query) {
    this.restURL   = restURL;
    this.indices   = indices;
    this.indexFieldsMapping = new IndexFieldsMapping();
    
    for(var i = 0 ; i < indices.length; i++) {
      this.indexFieldsMapping.addFieldsMapping(getDslQuery(restURL + "/" + indices[i], {}));
    }
    
    this.query     = query;
    this.searchURL = restURL + "/" + indices.join() + "/_search?pretty=true";
    this.queryResult = new ESQueryResult();

    this.setQuery = function(query) {
      this.query = query; 
      this.queryResult.clearResult();
    };

    this.retrieve = function(from, size) { 
      var dslQuery = { from: from, size: size, query: this.query }
      var result = this.dslQuery(dslQuery); 
      this.queryResult.addQueryResult(dslQuery, result);
    };

    this.getQueryResult = function() { return this.queryResult; };

    this.getIndexFieldsMapping = function() { return this.indexFieldsMapping; }
    
    this.dslQuery = function(dslQuery) {
      var restPath = this.searchURL;
      return postDslQuery(restPath, dslQuery);
    };
  };

  return ESQueryContext ;
});