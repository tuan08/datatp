define([
  'jquery', 'util/util'
], function($, util) {
  function Bucket(pPath, name) {
    if(pPath == null) this.path = name;
    else              this.path = pPath + "/" + name;
    this.name = name;
    this.objects = [];

    this.add = function(obj) { this.objects.push(obj); } ;

    this.setObjects = function(objects) { this.objects = objects; } ;

    this.aggregate = function(aggs) {
      if(aggs == null || aggs.length == 0) return;
      var agg = aggs[0];
      this.aggregation = agg;
      this.buckets = agg.aggregate(this);

      if(this.buckets == null) return;
      aggs = aggs.slice(1);  
      if(aggs == null || aggs.length == 0) return;
      for(var name in this.buckets) {
        this.buckets[name].aggregate(aggs); 
      }
    },

    this.getPath = function() { return this.path; }

    this.getBucketSize = function() { 
      if(this.buckets == null) return 0;
      return Object.keys(this.buckets).length;
    };

    this.getObjectSize = function() { return this.objects.length; };

    this.findBucketByPath = function(path) {
      var segments = path.split('/');
      var bucket = this;
      if(bucket.name != segments[0]) return null; 
      for(var i = 1; i < segments.length; i++) {
        if(!bucket) return null;
        if(!bucket.buckets) return null;
        bucket = bucket.buckets[segments[i]];
      }
      return bucket;
    },

    this.dumpBucket = function(indent) {
      if(!indent) indent = "";
      console.log(indent + this.name) ;
      if(this.buckets == null) return;
      indent = indent + "  ";
      for(var name in this.buckets) {
        this.buckets[name].dumpBucket(indent);
      }
    }
  }

  function FieldValueAggregation(field) {
    this.field = field;

    this.aggregate = function(bucket) {
      var objects = bucket.objects;
      if(objects == null || objects.length == 0) return null;
      var buckets = {} ;
      for(var i = 0; i < objects.length; i++) {
        var value = this.getValue(objects[i], this.field); 
        if(value == null) value = "N/A";
        if(!buckets[value]) buckets[value] = new Bucket(bucket.getPath(), value);
        buckets[value].add(objects[i]);
      }
      return buckets;
    },

    this.getValue = function(bean, field) {
      var value = util.reflect.getFieldValue(bean, field); 
      return value;
    }
  }

  var bucket = {
    Bucket: Bucket,
    aggregation: {
      FieldValueAggregation: FieldValueAggregation
    }
  }

  return bucket;
});
