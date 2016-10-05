define([
  'jquery',
  'underscore'
], function($, _) {
  var select_options_tmpl = _.template(`
    <%for(var i = 0; i < options.length; i++) {%>
      <%var selected = ""%>
      <%if(options[i] == select) selected = "selected='true'"%>
      <option value="<%=options[i]%>" <%=selected%>><%=options[i]%></option>
    <%}%>
  `);

  var select_options_with_label_tmpl = _.template(`
    <%for(var i = 0; i < options.length; i++) {%>
      <%var selected = ""%>
      <%if(options[i] == select) selected = "selected='true'"%>
      <option value="<%=options[i]%>" <%=selected%>><%=labels[i]%></option>
    <%}%>
  `);

  var page_iterator_tmpl = _.template(`
    <div class="ui-page-iterator" style="text-align: right">
      <a class="onSelectPage ui-action" page="<%=pageList.getPrevPage()%>">Prev</a>
      <%var cpage = pageList.getCurrentPage(); %>
      <%var range = pageList.getSubRange(cpage, 10); %>
      <%if(range[0] > 1) {%>
        <a class="onSelectPage ui-action" page="1">First</a>
        <a class="ui-disabled ui-action">..</a>
      <%}%>
      <%for(var i = range[0]; i <= range[1] ; i++) { %>
        <%if(i == cpage) {%>
          <a class="ui-disabled onSelectPage ui-action" page="<%=i%>"><%=i%></a>
        <%} else {%>
          <a class="onSelectPage ui-action" page="<%=i%>"><%=i%></a>
        <%} %>
      <%} %>
      <%var lastPage =  pageList.getAvailablePage()%>
      <%if(range[1] < lastPage) {%>
        <a class="ui-disabled ui-action">..</a>
        <a class="onSelectPage ui-action" page="<%=lastPage%>">Last</a>
      <%}%>
      <a class="onSelectPage ui-action" page="<%=pageList.getNextPage()%>">Next</a>
    </div>
  `);

  var widget = {
    select: {
      options: {
        render: function(options, select) {
          var params = { options: options, select: select };
          return select_options_tmpl(params);
        },

        renderWithLabel: function(labels, options, select) {
          var params = {labels: labels, options: options, select: select };
          return select_options_with_label_tmpl(params);
        }
      }
    },

    page: {
      iterator: {
        render: function(pageList) {
          var params = { pageList: pageList };
          return page_iterator_tmpl(params);
        },
        getSelectPage: function(evt) {
          var selPage = $(evt.target).closest("a.onSelectPage").attr("page") ;
          var page = parseInt(selPage) ;
          return page;
        }
      }
    }
  };
  
  return widget;
});
