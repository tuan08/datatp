define([
  'jquery'
], function($) {
  var Aggregation = function(type, field, beanModel) {
    this.type        = type;
    this.field       = field;
    this.fieldModel  = beanModel.fields[field];
    this.subaggs     = {};

    this.getType = function() { return this.type; }

    this.getField = function() { return this.field; }

    this.getSubAggregations = function() { return this.subaggs; };

    this.addSubAggregation = function(agg) { 
      if(agg.id == null) throw new Error("Id is not available");
      this.subaggs[agg.id] = agg; 
    };

    this.rmSubAggregation = function(id) { 
      var agg = this.subaggs[id];
      delete this.subaggs[id]; 
      return agg;
    };

    this.addESSubAggs = function(agg) {
      for(var name in this.subaggs) {
        if(!agg.aggs) agg.aggs = {};
        var subagg = this.subaggs[name];
        subagg.addESAgg(agg.aggs);
      }
    };

    this.addToTree = function(tree, aggs, level) {
      var agg = aggs[this.field];
      if(!agg) return;
      var buckets = aggs[this.field].buckets;
      for(var i = 0; i < buckets.length; i++) {
        var bucket = buckets[i];
        var node = { level: level, bucket: this.field, key: bucket.key, count: bucket.doc_count, children: [] };
        tree.children.push(node);
        for(var name in this.subaggs) {
          var subagg = this.subaggs[name];
          subagg.addToTree(node, bucket, level + 1);
        }
      }
      if(level > 0) tree.children.push({separator: true});
    };

    this.addToChartDataModel = function(chartDataMap, bucket, xCoord) {
      var subBuckets = bucket[this.field].buckets;
      for(var i = 0; i < subBuckets.length; i++) {
        var subBucket = subBuckets[i];
        var key = subBucket.key;
        if(!chartDataMap[key]) {
          chartDataMap[key] = { key: key, values: [] } ;
        }
        var coord = { x: xCoord, y: subBucket.doc_count };
        chartDataMap[key].values.push(coord);
        for(var name in this.subaggs) {
          var subagg = this.subaggs[name];
          subagg.addToChartDataModel(chartDataMap, subBucket, xCoord);
        }
      }
    };

    this.buildChartDataModel = function(result) {
      var chartDataMap = {};
      var chart = { key: this.field, values: [], bar: true } ;
      chartDataMap[this.field] = chart;
      var buckets = result.aggregations[this.field].buckets;
      for(var i = 0; i < buckets.length; i++) {
        var bucket = buckets[i];
        chart.values.push({ x: bucket.key, y: bucket.doc_count });
        for(var name in this.subaggs) {
          var subagg = this.subaggs[name];
          subagg.addToChartDataModel(chartDataMap, bucket, bucket.key);
        }
      }
      var chartData = [];
      for(var key in chartDataMap) {
        chartData.push(chartDataMap[key]);
      }
      return chartData ;
    };
  };

  var DateHistorgramAgg = function(field, beanModel, interval, format, from, to) {
    $.extend(this, new Aggregation('DateHistogram', field, beanModel));

    this.interval  = interval;
    this.format    = format == null ? "dd/MM/yyyy hh:mm:ss" : format;

    this.setInterval = function(interval) { this.interval = interval; }

    this.setFormat = function(format) { 
      if(format == null || format == '') this.format = "dd/MM/yyyy hh:mm:ss";
      else this.format = format; 
    }

    this.addESAgg = function(aggs) {
      var qFieldName = this.field;
      qFieldName = qFieldName.replace("_source.", "");
      var agg = {
        date_histogram : { field: qFieldName, interval: this.interval, format: this.format }
      };
      this.addESSubAggs(agg) ;
      aggs[this.field] = agg;
    };
  };

  var TopTermsAgg = function(field, beanModel, size) {
    $.extend(this, new Aggregation('TopTerms', field, beanModel));
    this.size  = size;

    this.addESAgg = function(aggs) {
      var qFieldName = this.field;
      qFieldName = qFieldName.replace("_source.", "");
      var agg = { terms: { field: qFieldName, size: this.size } };
      this.addESSubAggs(agg) ;
      aggs[this.field] = agg;
    };
  };

  var Aggregations = function(searchHitModel) {
    this.idTracker = 0;
    this.searchHitModel = searchHitModel;
    this.availableFields = {};
    this.aggregation = null;

    for(var fName in searchHitModel.fields) {
      this.availableFields[fName] = true;
    }


    this.getAggregation = function() { return this.aggregation; }
    this.setAggregation = function(agg) { 
      agg.id = "root";
      this.aggregation = agg; 
      this.availableFields[agg.field] = false;
    }


    this.getSubAggregations = function() { return this.aggregation.getSubAggregations() ; };

    this.addSubAggregation = function(agg) {
      agg.id = "id-" + this.idTracker++;
      this.aggregation.addSubAggregation(agg);
      if(agg.field) this.availableFields[agg.field] = false;
    };

    this.rmSubAggregation = function(aggId) {
      var agg = this.aggregation.rmSubAggregation(aggId);
      if(agg.field) this.availableFields[agg.field] = true;
    };

    this.getAvailableFieldsForAgg = function(agg) {
      if(agg == null) return [];
      if("DateHistogram" == agg.getType()) {
        return this.getAvailableFields(agg.getField(), "date");
      } else if("TopTerms" == agg.getType()) {
        return this.getAvailableFields(agg.getField(), "string");
      }
      return [] ;
    };

    this.getAvailableFields = function(include, datatype) {
      var fields = [] ;
      if(include == "") fields.push(include);
      for(var fName in this.availableFields) {
        if(!this.availableFields[fName] && fName != include) continue;
        var fieldConfig = this.searchHitModel.fields[fName];
        if(fieldConfig.mapping == null) continue;
        if(fieldConfig.mapping.type == datatype) {
          fields.push(fName);
        }
      }
      return fields;
    };

    this.findAggregationById = function(aggId) {
      if(this.aggregation.id == aggId) return this.aggregation;
      for(var id in this.aggregation.subaggs) {
        if(aggId == id) return this.aggregation.subaggs[id] ;
      }
    };

    this.changeAggregationField = function(aggId, newField) {
      var agg = this.findAggregationById(aggId);
      agg.field = newField;
    };

    this.getESAggs = function() {
      var aggs = { };
      this.aggregation.addESAgg(aggs);
      return aggs;
    };

    this.buildTreeModel = function(result) {
      var tree = { children: [] };
      this.aggregation.addToTree(tree, result.aggregations, 0);
      return tree;
    };

    this.buildChartDataModel = function(result) { return this.aggregation.buildChartDataModel(result); };
  };

  var model = {
    agg: {
      DateHistogram: DateHistorgramAgg,
      TopTerms: TopTermsAgg
    },

    Aggregations: Aggregations,
  }

  return model ;
});
