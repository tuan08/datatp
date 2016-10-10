define([
  'ui/UIView',
  'ui/UIBreadcumbs',
  'ui/UIBorderLayout',
  'plugins/crawler/UICrawlerReport',
  'plugins/crawler/UIFetcherReport',
  'plugins/crawler/site/UISiteConfigScreen',
  'plugins/crawler/model',
  'plugins/crawler/Rest',
  'text!plugins/crawler/UICrawlerStatus.tmpl'
], function(UIView, UIBreadcumbs, UIBorderLayout, UICrawlerReport, UIFetcherReport,  UISiteConfigScreen, model, Rest, UICrawlerStatusTmpl) {

  var UICrawlerStatus = UIView.extend({
    initialize: function(options) {
      this.crawlerStatus = Rest.crawler.getStatus();
    },

    _template: _.template(UICrawlerStatusTmpl),

    render: function() {
      var params = { crawlerStatus: this.crawlerStatus};
      $(this.el).html(this._template(params));
    },

    events: {
      "click .onStart": "onStart",
      "click .onStop": "onStop",
      "click .onSiteConfig": "onSiteConfig",
      "click .onRefresh": "onRefresh",
      "click .onFetcherStatusDetail": "onFetcherStatusDetail"
    },

    onStart: function(evt) {
      this.crawlerStatus = Rest.crawler.start();
      this.render();
    },

    onStop: function(evt) {
      this.crawlerStatus = Rest.crawler.stop();
      this.render();
    },

    onSiteConfig: function(evt) {
      var uiCrawlerBreadcumbs = this.getAncestorOfType("UICrawlerBreadcumbs");
      uiCrawlerBreadcumbs.push(new UISiteConfigScreen());
    },

    onRefresh: function(evt) {
      this.crawlerStatus = Rest.crawler.getStatus();
      this.render();
    },

    onFetcherStatusDetail: function(evt) {
      var id = $(evt.target).attr("id");
      var uiReport = new UIFetcherReport().init(id);
      uiReport.label =  "Fetcher[" + id + "]" ;
      var uiBreadcumbs = this.getAncestorOfType("UICrawlerBreadcumbs");
      uiBreadcumbs.push(uiReport);
    }

  });

  var UICrawler = UIBorderLayout.extend({
    label: 'Crawler',

    config: {
    },
    
    onInit: function(options) {
      var westConfig = { width: "400px"};
      this.setUI('west', new UICrawlerStatus(), westConfig);

      var centerConfig = {};
      this.setUI('center', new UICrawlerReport(), centerConfig);
    }
  });

  var UICrawlerBreadcumbs = UIBreadcumbs.extend({
    type:  "UICrawlerBreadcumbs",

    onInit: function(options) {
      this.uiCrawler = new UICrawler() ;
      this.push(this.uiCrawler);
      return this;
    }
  });

  return new UICrawlerBreadcumbs() ;
});
