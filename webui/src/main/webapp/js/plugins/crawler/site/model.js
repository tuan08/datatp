define([
], function($, _, Backbone) {
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

  var model = {
    site: {
      config: {
        SiteConfigGeneric:  SiteConfigGeneric,
        WebpageTypePattern: WebpageTypePattern,
        ExtractConfig:      ExtractConfig,
        ExtractConfigXPath: ExtractConfigXPath
      }
    }
  }

  return model ;
});
