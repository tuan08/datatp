define([
  'util/util',
], function(util) {
  var SiteConfigGeneric = {
    label: 'Site Config',
    fields: {
      "group": { label: "Group", required: true  },
      "hostname": { label: "Hostname", required: true },
      "status": { label: "Status" },
      "injectUrl": {label: "Inject URL", type: 'array' },
      "crawlSubDomain": {   
        label: "Crawl Subdomain", type: "select",
        options: [
          { label: 'True', value: true },
          { label: 'False', value: false }
        ]
      },
      "crawlDeep": { label: "Crawl Deep" },
      "maxConnection": { label: "Max Connection" },
      "language": { label: "Language" },
      "description": { label: "Description", type: "textarea" }
    },
  };

  var WebpageTypePattern = {
    label: 'Webpage Type Pattern',
    fields: {
      "type": {
        label: "Type", type: 'select',
        options: [
          { label: 'uncategorized', value: 'uncategorized' },
          { label: 'ignore',        value: 'ignore' },
          { label: 'list',          value: 'list' },
          { label: 'detail',        value: 'detail' }
        ]
      },
      "pattern": { label: "Pattern", type: "array" }
    }
  };

  var ExtractConfig = {
    label: 'Extract Config',
    fields: {
      "name": {
        label: "Entity Name", required: true, type: "select",
        options: [
          { label: 'content', value: 'content' },
          { label: 'job',     value: 'job' },
          { label: 'product', value: 'product' },
          { label: 'comment', value: 'comment' }
        ]
      },
      "extractType": {  
        label: "Extract Type", type: "select",
        options: [
          { label: 'content', value: 'content' },
          { label: 'article', value: 'article' },
          { label: 'forum',   value: 'forum' },
          { label: 'classified',   value: 'classified' },
          { label: 'job',   value: 'job' },
          { label: 'comment',   value: 'comment' }
        ]
      },
      "matchType": {   
        label: "Match Type", type: "select",
        options: [
          { label: 'Any', value: 'any' },
          { label: 'Url',  value: 'url' },
          { label: 'Title', value: 'title' }
        ]
      },
      "matchPattern": { label: "Match Pattern", type: "array" }
    }
  };

  var ExtractConfigXPath = {
    label: 'Extract XPath',
    fields: {
      "name": {
        label: "Name", required: true,
        options: [
          { label: 'Title',     value: 'title' },
          { label: 'Description', value: 'description' },
          { label: 'Content',     value: 'content' }
        ]
      },
      "xpath": {
        label: "XPath", required: true
      }
    },
  };

  var SiteStatistic = {
    label: 'Site Statistic',
    fields: {
      "hostname": { label: "Hostname" },
      "group": {   label: "Group" },
      "scheduleCount":   { label: "Schedule" },
      "commitCount": { label: "commit" },
      "inQueue":  { label: "In Queue" },
      "statistics.urlStatus_All.count": {   label: "URL All" },
      "statistics.urlStatus_Pending.count": {   label: "URL Pending" },
      "statistics.urlStatus_New.count":  { label: "URL New" },
      "statistics.urlStatus_Waiting.count": { label: "URL Waiting" },
      "statistics.responseCode_OK.count":  {  label: "RC OK" },
    }
  };

  var SiteStatisticEntry = {
    label: 'Site Statistic',
    fields: {
      "category": { label: "Category" },
      "name": {   label: "Name" },
      "count":   { label: "count" },
    }
  };

  var URLAnalysis = {
    label: 'URL Analysis',
    fields: {
      "pageType": { label: "Page Type" },
      "urlInfo.host": { label: "Host" },
      "urlInfo.directory": { label: "Directory" },
      "urlInfo.path": { label: "Path" },
      "urlInfo.extension": { label: "Extension" },
      "urlInfo.paramCount": { label: "Param Count" }
    }
  };

  var FetcherStatus = {
    label: 'Fetcher Status',
    fields: {
      "id":     { label: "Id" },
      "host":   { label: "Host" },
      "status": { label: "Status" },
    }
  };

  var URLFetcherReport = {
    label: 'URL Fetcher Report',
    fields: {
      "name": { label: "Name" }, 
      "status": { label: "Status" }, 
      "urlFetcherMetric.fetchCount":  { label: "Fetch Count" },
      "urlFetcherMetric.rc200Count":  { label: "RC 200" },
      "urlFetcherMetric.errorCount":  { label: "Error Count" }
    }
  };

  var URLFetcherMetrics = {
    label: 'URL Fetcher Metrics',
    fields: {
      "fetcher": { label: "Fetcher" }, 
      "category": { label: "Category" }, 
      "name": { label: "Name" }, 
      "count": { label: "count" }
    }
  };

  var URLSchedule = {
    label: 'URL Schedule',
    fields: {
      "time": { label: "Time", format: util.formater.time.locale  },
      "execTime": {   label: "Exec Time" } , 
      "urlCount":   { label: "URL" },
      "urlListCount": { label: "URL List"} ,
      "urlDetailCount": {  label: "URL Detail" } ,
      "urlUncategorizedCount": { label: "URL Uncategorized" },
      "scheduleCount": { label: "Schedule" },
      "delayScheduleCount": { label: "Delay Schedule" },
      "pendingCount": { label: "Pending" },
      "expiredPendingCount":  { label: "Expired Pending" },
      "waitingCount":  {  label: "Waiting" }
    }
  };

  var URLCommit = {
    label: 'URL Commit',
    fields: {
      "time": { label: "Time", format: util.formater.time.locale  },
      "execTime": { label: "Exec Time" },
      "commitURLCount": { label: "Commit URL" },  
      "newURLFoundCount": { label: "New URL Found" }
    }
  };

  var URLDatum = {
    label: 'URLDatum',
    fields: {
      "fetcher": { label: "Fetcher" }, 
      "originalUrl": { label: "Original Url" }, 
    }
  };



  var model = {
    site: {
      SiteStatistic: SiteStatistic,
      SiteStatisticEntry: SiteStatisticEntry,
      config: {
        SiteConfigGeneric:  SiteConfigGeneric,
        WebpageTypePattern: WebpageTypePattern,
        ExtractConfig:      ExtractConfig,
        ExtractConfigXPath: ExtractConfigXPath
      },
      analysis: {
        URLAnalysis: URLAnalysis
      }
    },

    fetcher: {
      FetcherStatus: FetcherStatus,
      URLFetcherReport: URLFetcherReport,
      URLFetcherMetrics: URLFetcherMetrics
    },
    
    scheduler: {
      URLSchedule: URLSchedule,
      URLCommit: URLCommit,
    },
    urldb: {
      URLDatum: URLDatum
    }
  }

  return model ;
});
