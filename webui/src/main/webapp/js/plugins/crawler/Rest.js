define([
  'rest/Server'
], function(Server) {
  var server = new Server("http://192.168.1.14:8080/crawler");

  function SiteRest(server) {
    this.getSiteConfigs = function() { return server.restGET("/site/configs"); };

    this.getSiteStatistics = function() { return server.restGET("/site/statistics"); };

    this.save = function(siteConfig) {
      return server.syncPOSTJson("/site/save", siteConfig);
    };

    this.remove = function(group, site) {
      return server.restGET("/site/remove", {group: group, site: site});
    };

    this.exportURL = function() { return server.toURL("/site/export"); };

    this.getAnalyzedURLSiteStructure = function(siteConfig, maxDownload, forceNew) {
      var params = { siteConfig: siteConfig, maxDownload: maxDownload, forceNew: forceNew } ;
      return server.syncPOSTJson("/site/analyzed-site-url", params);
    };

    this.reanalyseURLSiteStructure = function(siteConfig, maxDownload) {
      var params = { siteConfig: siteConfig, maxDownload: maxDownload, forceNew: false } ;
      return server.syncPOSTJson("/site/reanalyse-site-url", params);
    };

    this.recrawlURLSiteStructure = function(siteConfig, maxDownload) {
      var params = { siteConfig: siteConfig, maxDownload: maxDownload, forceNew: false } ;
      return server.syncPOSTJson("/site/recrawl-site-url", params);
    };

    this.getAnalyzedURLData = function(url) {
      var params = { url: url } ;
      return server.restGET("/site/analyzed-url-data", params);
    };
  };

  function SchedulerRest(server) {
    this.server = server;
    
    this.getURLCommitInfos = function(max) {
      if(max === undefined) max = 100;
      return this.server.restGET("/scheduler/report/url-commit", {max: max});
    };

    this.getURLScheduleInfos = function(max) {
      if(max === undefined) max = 100;
      return this.server.restGET("/scheduler/report/url-schedule", {max: max});
    };
  };

  function FetcherRest(server) {

    this.getFetcherReport = function(id) {
      return server.restGET("/fetcher/report", {id: id});
    };
  };

  function CrawlerRest(server) {
    
    this.getStatus = function() {
      return server.restGET("/status", {});
    };

    this.start = function() {
      return server.restGET("/start", {});
    };

    this.stop = function() {
      return server.restGET("/stop", {});
    };

  };

  var Rest = {
    site:       new SiteRest(server),
    scheduler:  new SchedulerRest(server),
    fetcher:    new FetcherRest(server),
    crawler:    new CrawlerRest(server)
  }
  return Rest ;
});
