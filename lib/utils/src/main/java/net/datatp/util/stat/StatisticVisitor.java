package net.datatp.util.stat;

/**
 * $Author: Tuan Nguyen$ 
 **/
public interface StatisticVisitor {
	public void onVisit(Statistics statistics, Statistic statistic) ;
}
