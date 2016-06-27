package com.my.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.map.MultiKeyMap;

import com.my.common.Constant;
import com.my.entity.Cluster;

public abstract class AbstractClusterDesicion {
	
	int levelSize = Constant.levelSize;
	
	double lowestRate = Constant.lowestRate;

	/**
	 * areadyClusters已经不能负担全部的流量了,需要再找一些cluster来帮忙负担,一次只解决一个问题 
	 * @param curView 当前要求解的view
	 * @param areadyClusters 已经使用的cluster
	 * @param enabledClusters 可供选择的cluster
	 * @param excludeClusters 排除使用的cluster
	 * @param totalFlow 表示该view总共要分担的流量
	 * @param clusterViewQOS MultiKeyMap类型的
	 * @return
	 * @throws Exception 
	 */
	public abstract List<Cluster> make(String view, List<Cluster> areadyClusters, List<Cluster> enabledClusters, List<Cluster> excludeClusters, double totalFlow, MultiKeyMap clusterViewQOS)
			throws Exception;
	
	protected void changeFlow(double totalFlow, List<Cluster> resultClusters,  List<Cluster> areadyClusters) {
		double loadFlow = totalFlow / (resultClusters.size() + areadyClusters.size());
		
		for(Cluster c: resultClusters) {
			c.setCurBandWidth(c.getCurBandWidth() + loadFlow);
		}
		
		// areadyClusters应该要减少一部分负担的流量
		if(areadyClusters != null) {
			if(resultClusters.size() > 1) {
				for(Cluster c: areadyClusters) {
					//c.setAddedBandWidth(totalFlow/(resultClusters.size() + 1) - totalFlow/(resultClusters.size() + finalDepth));
					c.setCurBandWidth(c.getCurBandWidth() - (totalFlow/(areadyClusters.size() + 1) - loadFlow));
				}
			}
		}
	}

	// 从符合条件的cluster中选取指定的数目的cluster
	/*protected List<Cluster> getUsedCluster(List<Cluster> cs, int usedNO, final String view, final MultiKeyMap clusterViewQOS) {
		Collections.sort(cs, new Comparator<Cluster>() {

			@Override
			public int compare(Cluster o1, Cluster o2) {
				double rate1 = o1.getCurBandWidth() / o1.getMaxBandWidth();
				double rate2 = o1.getCurBandWidth() / o1.getMaxBandWidth();
				if (rate1 < 0.5) {
					if (rate2 < 0.5) {
						int qos1 = (int) clusterViewQOS.get(o1.getName(), view);
						int qos2 = (int) clusterViewQOS.get(o2.getName(), view);
						return (qos1 > qos2) ? -1 : 1;
					} else {
						return -1;
					}
				}
				if (rate1 > 0.5) {
					if (rate2 < 0.5) {
						return 1;
					} else {
						int qos1 = (int) clusterViewQOS.get(o1.getName(), view);
						int qos2 = (int) clusterViewQOS.get(o2.getName(), view);
						return (qos1 > qos2) ? -1 : 1;
					}
				}
				return 0;
			}
		});
		return cs.subList(0, usedNO);
	}*/
	
	protected List<Cluster> getUsedClusterEasy(List<Cluster> cs, int usedNO) {
		return cs.subList(0, usedNO);
	}

	/**
	 * 
	 * @param usedClusters 已经排序了
	 * @param curAddedFlow
	 * @return
	 */
	protected int getFitNo(Collection<Cluster> usedClusters, double curAddedFlow) {
		int fitNo = 0;
		for (Cluster c : usedClusters) {
			if ((c.getCurBandWidth() + curAddedFlow) / c.getMaxBandWidth() < lowestRate) {
				//if((c.getCurBandWidth() + c.getAddedBandWidth() + curAddedFlow) / c.getMaxBandWidth() < 0.9) {
				fitNo++;
			}
		}
		return fitNo;
	}

	/*protected List<Cluster> getFitClusters(TreeSet<Cluster> usedClusters, double curAddedFlow) {
		List<Cluster> fitClusters = new ArrayList<Cluster>();
		for (Cluster c : usedClusters) {
			if ((c.getCurBandWidth() + curAddedFlow) / c.getMaxBandWidth() < lowestRate) {
				fitClusters.add(c);
			}
		}
		return fitClusters;
	}*/
	
	/**
	 * 
	 * @param usedClusters 已经排序了
	 * @param curAddedFlow
	 * @param needSize
	 * @return
	 */
	protected List<Cluster> getNeedFitClusters(Collection<Cluster> usedClusters, double curAddedFlow, int needSize) {
		List<Cluster> fitClusters = new ArrayList<Cluster>();
		int i = 0;
		for (Cluster c : usedClusters) {
			if(i == needSize) {
				break;
			}
			if ((c.getCurBandWidth() + curAddedFlow) / c.getMaxBandWidth() < lowestRate) {
				fitClusters.add(c);
				i++;
			}
		}
		return fitClusters;
	}

}