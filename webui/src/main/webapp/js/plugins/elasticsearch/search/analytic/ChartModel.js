define([
  'jquery'
], function($) {
  var Aggregation = function(type, name) {
    this.type    = type;
    this.name    = name;
    this.subaggs = [];

    this.getSubAggregations = function() { return this.subaggs ; }
    
    this.addSubAggregation = function(agg) { this.subaggs.push(agg); };
    
    this.rmSubAggregation = function(field) { 
      for(var i = 0; i < this.subaggs.length; i++) {
        var subagg = this.subaggs[i];
        if(subagg.field == field) {
          this.subaggs.splice(i, 1);
          break;
        }
      }
    };
    
    this.appendSubAggs = function(agg) {
      if(this.subaggs.length > 0) {
        agg.aggs = {};
        for(var i = 0; i < this.subaggs.length; i++) {
          var subagg = this.subaggs[i];
          subagg.append(agg.aggs);
        }
      }
    };

    this.buildChartData = function(result) {
      var chartDataMap = {};
      var chartData = { key: this.name, bar: true, values: [] } ;
      var buckets = result.aggregations[this.name].buckets;
      if(buckets != null) {
        for(var i = 0; i < buckets.length; i++) {
          var bucket = buckets[i];
          var coord = { x: bucket.key, y: bucket.doc_count };
          chartData.values.push(coord);
          for(var j = 0; j < this.subaggs.length; j++) {
            this.subaggs[j].appendChartData(chartDataMap, bucket);
          }
        }
      }
      var chartDatas = [chartData];
      for(var key in chartDataMap) {
        chartDatas.push(chartDataMap[key]);
      }
      return chartDatas ;
    };
    
    this.appendChartData = function(chartDataMap, bucket) {
      var subBuckets = bucket[this.name].buckets ;
      for(var i = 0; i < subBuckets.length; i++) {
        var subBucket = subBuckets[i];
        if(!chartDataMap[subBucket.key]) {
          chartDataMap[subBucket.key] = { key: subBucket.key, values: [] } ;
        }
        var coord = { x: bucket.key, y: subBucket.doc_count };
        chartDataMap[subBucket.key].values.push(coord);
      }
    }
  };

  var DateHistogramAggregation = function(name, dateField, interval) {
    $.extend(this, new Aggregation('DateHistogram', name));
    this.dateField = dateField;
    this.interval  = interval;

    this.append = function(aggs) {
      var agg = {
        date_histogram : { field: this.dateField, interval: this.interval, format: "dd/MM/yyyy hh:mm:ss" },
      };
      this.appendSubAggs(agg) ;
      aggs[this.name] = agg;
    };
  }

  var TermsAggregation = function(name, field, size) {
    $.extend(this, new Aggregation('TermTopHit', name));
    this.field = field;
    this.size  = size;

    this.append = function(aggs) {
      var agg = { terms: { field: this.field, size: this.size } };
      this.appendSubAggs(agg) ;
      aggs[this.name] = agg;
    };
  };

  var DateHistogramModel = function(name, dateField, interval) {
    this.aggregation = new DateHistogramAggregation(name, dateField, interval);

    this.getSubAggregations = function() { return this.aggregation.getSubAggregations() ; }
    
    this.addSubAggregation = function(agg) {
      this.aggregation.addSubAggregation(agg);
    };
    
    this.rmSubAggregation = function(field) { 
      this.aggregation.rmSubAggregation(field);
    };
    
    this.aggs = function() {
      var aggs = { };
      this.aggregation.append(aggs);
      return aggs ;
    };

    this.buildChartData = function(result) {
      return this.aggregation.buildChartData(result);
    };
  };

  var chart = {
    aggregation: {
      DateHistogramAggregation: DateHistogramAggregation,
      TermsAggregation: TermsAggregation
    },

    model: {
      DateHistogramModel: DateHistogramModel
    }
  }

  return chart ;
});
