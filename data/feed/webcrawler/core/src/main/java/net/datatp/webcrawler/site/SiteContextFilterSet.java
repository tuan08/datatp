package net.datatp.webcrawler.site;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.datatp.webcrawler.site.SiteContext.Modify;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class SiteContextFilterSet implements Serializable {
	private List<SiteContext> contexts ;
	private List<SiteContext> filteredContexts ;
	private SiteContextFilter defaultFilter ;
	private Map<String, SiteContextFilter> filters = new HashMap<String, SiteContextFilter>() ;
	private LinkedHashMap<Object, SiteContext> contextLinkedHashMap = new LinkedHashMap<Object, SiteContext>();

	public SiteContextFilterSet(List<SiteContext> contexts) {
		defaultFilter = new SiteContextFilter.IndexRangeFilter(0, 1000) ;
		this.contexts = contexts ;
	}

	public List<SiteContext> getSiteContexts() { return this.contexts ; }

	public int getTotalChangedSiteContexts() {
    List<SiteContext> contexts = getSiteContexts() ;
    int counter = 0;
    for(int i = 0; i < contexts.size(); i++) {
      if(contexts.get(i).getModify() != Modify.NONE) counter++ ;
    }
    return counter ;
  }
	
	public List<SiteContext> getSiteContexts(LinkedHashMap<Object, SiteContext> contextLinkedHashMap) {
		this.contextLinkedHashMap = contextLinkedHashMap;
		contexts = new ArrayList<SiteContext>(contextLinkedHashMap.values());
		return contexts;
	}

	//TODO: Fix this method
	public LinkedHashMap<Object, SiteContext> getContextLinkedHashMap() {
		contexts = getSiteContexts();
		int size = contexts.size();
		for(int i = 0; i < size; i++) {
			contextLinkedHashMap.put(contexts.get(i).getSiteConfig().getHostname(), contexts.get(i));
		}
		return contextLinkedHashMap ; 
	}

	public List<SiteContext> getFilteredSiteContexts() { 
		if(filteredContexts == null) apply() ;
		return this.filteredContexts ; 
	}

	public void addFilter(SiteContextFilter filter) {
		this.filters.put(filter.getName(), filter);
		filteredContexts = null ;
	}

	public void removeFilter(Class<? extends SiteContextFilter> type) {
		filters.remove(type.getSimpleName()) ;
		filteredContexts = null ;
	}

	public void removeAllFilter() {
		filters.clear() ;
		filteredContexts = null ;
	}

	public void apply() {
		filteredContexts = new ArrayList<SiteContext>() ;
		if(filters.size() == 0) {
			for(int i = 0; i < contexts.size(); i++) {
				SiteContext context = contexts.get(i) ;
				if(defaultFilter.include(i, context)) {
					filteredContexts.add(context) ;
				} else {
				  return ;
				}
			}
		} else {
			SiteContextFilter[] filter = filters.values().toArray(new SiteContextFilter[filters.size()]) ;
			for(int i = 0; i < contexts.size(); i++) {
				SiteContext context = contexts.get(i) ;
				boolean include = true ;
				for(SiteContextFilter selFilter : filter) {
					if(!selFilter.include(i, context)) {
						include = false ;
						break ;
					}
				}
				if(include) filteredContexts.add(context) ;
			}
		}
	}
}