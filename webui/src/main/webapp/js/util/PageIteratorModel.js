define([
  'jquery', 
], function($) {
  /**@type util.PageIteratorModel */
  var PageIteratorModel = function(currentPage, pageSize, available) {
    this.currentPage = currentPage ;
    this.available = available;
    this.pageSize = pageSize;
    this.availablePage  = 1;
  
    /**@memberOf util.PageIteratorModel */
    this.getPageSize = function() { return pageSize  ; };
    
    /**@memberOf util.PageIteratorModel */
    this.setPageSize = function(pageSize) {
      this.pageSize = pageSize ;
      this.setAvailablePage(this.available) ;
    };
    
    /**@memberOf util.PageIteratorModel */
    this.getCurrentPage = function() { return this.currentPage ; };
    
    /**@memberOf util.PageIteratorModel */
    this.getAvailable = function() { return this.available ; };
    
    /**@memberOf util.PageIteratorModel */
    this.getAvailablePage = function() { return this.availablePage ; };
    
    /**@memberOf util.PageIteratorModel */
    this.getPrevPage = function() {
      if(this.currentPage == 1) return 1 ;
      return this.currentPage - 1 ;
    };
    
    /**@memberOf util.PageIteratorModel */
    this.getNextPage = function() {
      if(this.currentPage == this.availablePage) return this.currentPage ;
      return this.currentPage + 1 ;
    };
    
    /**@memberOf util.PageIteratorModel */
    this.checkAndSetPage = function(page) {
      if(page < 1 || page > this.availablePage) {
        throw new Error("Page is out of range " + page) ;
      }
      this.currentPage =  page ;
    };
    
    /**@memberOf util.PageIteratorModel */
    this.setAvailablePage = function(available) {
      this.available = available ;
      if (available == 0)  {
        this.availablePage = 1 ; 
        this.currentPage =  1 ;
      } else {
        var pages = Math.ceil(available / this.pageSize) ;
        //if(available % this.pageSize > 0) pages++ ;
        this.availablePage = pages ;
        this.currentPage =  1 ;
      }
    };
    
    /**@memberOf util.PageIteratorModel */
    this.getFrom = function() { 
      return (this.currentPage - 1) * this.pageSize ; 
    };
    
    /**@memberOf util.PageIteratorModel */
    this.getTo = function() { 
      var to = this.currentPage * this.pageSize ; 
      if (to > this.available ) to = this.available ;
      return to ;
    };
    
    /**@memberOf util.PageIteratorModel */
    this.getSubRange = function(page, rangeSize) {
      if(page < 1 || page > this.availablePage) {
        throw new RuntimeException("page " + page + " is out of range") ; 
      }
      var range = [];
      if(rangeSize >= this.availablePage) {
        range[0] = 1 ;
        range[1] = this.availablePage ;
        return range ;
      }
      
      var half = rangeSize/2 ;
      if(page - half < 1) {
        range[0] = 1 ;
        range[1] = rangeSize ;
      } else if(page + (rangeSize - half) > this.availablePage) {
        range[0] = this.availablePage -  rangeSize ;
        range[1] = this.availablePage ;
      } else {
        range[0] = page - half;
        range[1] = page + (rangeSize - half) ;
      }
      return  range ;
    };
    
    //---------------------------------------------------------------------------
    
    this.setAvailablePage(this.available) ;
  };
  return PageIteratorModel ;
});
