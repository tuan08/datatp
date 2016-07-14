package net.datatp.util.stat;

/**
 * $Author: Tuan Nguyen$ 
 **/
public interface StatisticVisitor {
	public void onVisit(Statistic statistics, StatisticEntry statistic) ;
}
