define([
  'jquery', 
  'underscore', 
  'backbone',
  'ui/UIContainer',
  'ui/UITable',
  'ui/UIBean',
  'ui/UIBreadcumbs',
  'plugins/crawler/Rest'
], function($, _, Backbone, UIContainer, UITable, UIBean, UIBreadcumbs, Rest) {
  var UIURLFetcherInfo = UIBean.extend({
    label: "Fetcher Info",
    config: {
      beans: {
        bean: {
          label: 'URL Fetcher Info',
          fields: [
            { field:  "name", label: "Name"},
            { field:  "fetchCount", label: "Fetch Count"}
          ],
          edit: { actions: [ ], },
          view: { actions: [ ] }
        }
      }
    }
  });

  var UIPageTypeStat = UITable.extend({
    label: "Page Type Statistic",

    config: {
      toolbar: {
        dflt: { actions: [ ] }
      },
      
      bean: {
        label: 'Page Type Statistic',
        fields: [
          { field:  "name", label: "Name",  toggled: true, filterable: true },
          { field:  "count", label: "Count",  toggled: true, filterable: true }
        ]
      }
    }
  });

  var UIResponseCodeStat = UITable.extend({
    label: "Response Code Statistic",

    config: {
      toolbar: { dflt: { actions: [ ] } },
      
      bean: {
        label: 'Response code Statistic',
        fields: [
          { field:  "name", label: "Name",  toggled: true, filterable: true },
          { field:  "count", label: "Count",  toggled: true, filterable: true }
        ]
      }
    }
  });

  var UIErrorStat = UITable.extend({
    label: "Error Statistic",

    config: {
      toolbar: { dflt: { actions: [ ] } },
      
      bean: {
        label: 'Error Statistic',
        fields: [
          { field:  "name", label: "Name",  toggled: true, filterable: true },
          { field:  "count", label: "Count",  toggled: true, filterable: true }
        ]
      }
    }
  });

  var UIRecentFetchURL = UITable.extend({
    label: "Recent Fetched Urls",

    config: {
      toolbar: {
        dflt: {
          actions: [ ]
        }
      },
      
      bean: {
        label: 'Recent Fetched Urls',
        fields: [
          { field:  "originalUrl", label: "URL",  toggled: true, filterable: true },
          { 
            field:  "pageType", label: "Page Type",  toggled: true, filterable: true,
            custom: {
              getDisplay: function(bean) {
                if(bean.pageType == 0)      return "ignore";
                else if(bean.pageType == 1) return "list";
                else if(bean.pageType == 2) return "detail";
                else                        return "uncategorized" ;
              }
            }
          },
        ]
      }
    }
  });


  var UIURLFetcherMetric = UIContainer.extend({
    label: "URL Fetcher Metric", 
    config: {
      actions: [
      ]
    },

    onInit: function(options) {
      this.setHideHeader(true);
      var metric = options.urlFetcherMetric;
       
      var uiFetcherInfo = new UIURLFetcherInfo();
      uiFetcherInfo.bind('bean', metric);
      uiFetcherInfo.setReadOnly(true);
      this.add(uiFetcherInfo);

      var uiRCStat = new UIResponseCodeStat();
      uiRCStat.setBeans(metric.responseCodeGroups);
      this.add(uiRCStat);

      var uiErrorStat = new UIErrorStat();
      uiErrorStat.setBeans(metric.errorCodeGroups);
      this.add(uiErrorStat);

      var uiPageTypeStat = new UIPageTypeStat();
      uiPageTypeStat.setBeans(metric.pageTypes);
      this.add(uiPageTypeStat);

      var uiRecentFetchURL = new UIRecentFetchURL();
      uiRecentFetchURL.setBeans(metric.recentFetchUrls);
      this.add(uiRecentFetchURL);
    }
  }) ;

  return UIURLFetcherMetric ;
});
