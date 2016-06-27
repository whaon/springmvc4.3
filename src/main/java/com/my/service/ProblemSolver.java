package com.my.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.collections.map.MultiKeyMap;

import com.my.common.Constant;
import com.my.entity.Cluster;
import com.my.entity.DNSMap;
import com.my.exception.CanNotPartakeFlowException;

public class ProblemSolver {
	
	AbstractClusterDesicion decision = new OldClusterDesion();
	
	double lowestRate = Constant.lowestRate;
	double middleRate = Constant.middleRate;
	
	public void solveClusterCrashProblem(ClusterCrashProblem curProblem, DNSMap dnsmap) {
		HashMap<String, List<Cluster>> map = dnsmap.getMap();
		List<Cluster> enabledClusters = dnsmap.getClusters();
		HashMap<String, Double> view2Flow = dnsmap.getView2Flow();
		MultiKeyMap clusterViewQOS = dnsmap.getClusterViewQOS();
		
		Set<String> views = map.keySet();
		for(String view: views) {
			List<Cluster> usedClusters = map.get(view);
			
			Cluster curProblemCluster = (curProblem).getCluster();
			if(usedClusters.contains(curProblemCluster)) {
				usedClusters.remove(curProblemCluster);
			}
		}
	}
	
	private List<Map.Entry<FlowSpillKey, Double>> sort(Cluster curProblemCluster, List<DNSMap> dnsmaps) {
		
		// 存储该cluster一共占用的流量
		Map<FlowSpillKey, Double> viewFlow = new TreeMap<FlowSpillKey, Double>();
		
		for (DNSMap dnsmap : dnsmaps) {
			HashMap<String, List<Cluster>> map = dnsmap.getMap();
			List<Cluster> enabledClusters = dnsmap.getClusters();
			HashMap<String, Double> view2Flow = dnsmap.getView2Flow();
			MultiKeyMap clusterViewQOS = dnsmap.getClusterViewQOS();
			
			Set<String> views = map.keySet();
			for(String view: views) {
				List<Cluster> usedClusters = map.get(view);
				if(usedClusters.contains(curProblemCluster)) {
					int size = usedClusters.size();
					double loadFlow = view2Flow.get(view) / size;
					
					FlowSpillKey key = new FlowSpillKey(dnsmap, view);
					
					//viewFlow.put(key, viewFlow.get(key) == null ? loadFlow : viewFlow.get(key) + loadFlow);
					viewFlow.put(key, loadFlow);
				}
			}
			
		}
		
		List<Map.Entry<FlowSpillKey, Double>> list = new ArrayList<Map.Entry<FlowSpillKey, Double>>(viewFlow.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<FlowSpillKey, Double>>() {

			@Override
			public int compare(Entry<FlowSpillKey, Double> o1, Entry<FlowSpillKey, Double> o2) {
				return o1.getValue() - o2.getValue() > 0 ? -1 : 1;
			}
			
		});
		
		return list;
	}
	
	private void solve(DNSMap dnsmap, String view, Cluster curProblemCluster, 
			List<Cluster> crashedClusters, List<Cluster> flowSpillClusters) throws Exception {

		//boolean isResolved = false;
		
		HashMap<String, List<Cluster>> map = dnsmap.getMap();
		HashMap<String, Double> view2Flow = dnsmap.getView2Flow();
		List<Cluster> enabledAllClusters = dnsmap.getClusters();
		MultiKeyMap clusterViewQOS = dnsmap.getClusterViewQOS();
		
		List<Cluster> curClusters = map.get(view);
		//int no = curClusters.size();
		double loadFlow = view2Flow.get(view);
		
		double willFallFlow = loadFlow / curClusters.size();
		
		double afterCutRate = (curProblemCluster.getCurBandWidth() - willFallFlow) / curProblemCluster.getMaxBandWidth();
		
		curClusters.remove(curProblemCluster);
		
		boolean canAfford = true;
		
		if(curClusters.size() == 0) {
			canAfford = false;
		} else {
			for(Cluster c: curClusters) {
				if(flowSpillClusters.contains(c)) {
					continue;
				}
				double curFlow = c.getCurBandWidth() + loadFlow / (curClusters.size() - 1) - willFallFlow;// 移走了出问题的cluster后,剩下的需要负担多出来的流量
				double maxFlow = c.getMaxBandWidth();
				if(curFlow / maxFlow > lowestRate) {// 只要剩下的有1个不满足,都要重新开始分配cluster来负担流量
					canAfford = false;
					break;
				}
			}
		}
		
		curProblemCluster.setCurBandWidth(curProblemCluster.getCurBandWidth() - willFallFlow);
		
		if(curClusters.size() > 0 && canAfford) {// 说明剩下的可以负担
			for(Cluster c: curClusters) {
				double curFlow = c.getCurBandWidth() + loadFlow / (curClusters.size() - 1) - willFallFlow;// 移走了出问题的cluster后,剩下的需要负担多出来的流量
				c.setCurBandWidth(curFlow);
			}
		} else {// 剩下的不能负担,需要寻找新的 cluster来帮忙负担
			List<Cluster> excludeClusters = null;
			if(crashedClusters != null) {
				excludeClusters = new ArrayList<Cluster>(crashedClusters);
			}
			List<Cluster> enabledClusters = new ArrayList<Cluster>(enabledAllClusters);
			enabledClusters.removeAll(curClusters);// 有问题的cluster也参与竞争
			enabledClusters.removeAll(flowSpillClusters);// 有问题的cluster也参与竞争
			
			if(afterCutRate > lowestRate) {// 有问题的cluster不让参与竞争
				enabledClusters.remove(curProblemCluster);
			}
			
			List<Cluster> resultClusters = decision.make(view, curClusters, enabledClusters , excludeClusters, loadFlow, clusterViewQOS);
			for(Cluster resultCluster: resultClusters) {// 把结果反映出来
				curClusters.add(resultCluster);
			}
		}
		
		/*if(afterCutRate < lowestRate){// 说明已经解决
			isResolved = true;
		}*/
	
		//return isResolved;
	}
	
	public void solveFlowSpillProblem(FlowSpillProblem curProblem, List<DNSMap> dnsmaps, 
			List<Cluster> crashedClusters, List<Cluster> flowSpillClusters) throws Exception {
		
		Cluster curProblemCluster = ((FlowSpillProblem)curProblem).getCluster();
		
		List<Map.Entry<FlowSpillKey, Double>> list = sort(curProblemCluster, dnsmaps);
		
		int maxDepth = 5;
		int i=1;
		
		int index = 0;
		
		boolean isResolved = false;
		boolean oversize = false;
		for(Map.Entry<FlowSpillKey, Double> entry: list) {
			
			
			FlowSpillKey key = entry.getKey();
			DNSMap dnsmap = key.getDnsmap();
			String view = key.getView();
			
			HashMap<String, List<Cluster>> map = dnsmap.getMap();
			HashMap<String, Double> view2Flow = dnsmap.getView2Flow();
			//List<Cluster> enabledAllClusters = dnsmap.getClusters();
			//MultiKeyMap clusterViewQOS = dnsmap.getClusterViewQOS();
			
			List<Cluster> curClusters = map.get(view);
			double loadFlow = view2Flow.get(view);
			
			double willFallFlow = loadFlow / curClusters.size();
			
			double afterCutRate = (curProblemCluster.getCurBandWidth() - willFallFlow) / curProblemCluster.getMaxBandWidth();
			
			if(afterCutRate < 0.5) {
				oversize = true;
				index++;
				if(i++ > maxDepth) {
					break;
				}
				continue;
			} else if(afterCutRate < 0.9) {
				solve(dnsmap, view, curProblemCluster, crashedClusters, flowSpillClusters);
				isResolved = true;
				break;
			} else {
				if(oversize) {
					break;
				}
				solve(dnsmap, view, curProblemCluster, crashedClusters, flowSpillClusters);
				index++;
			}
			
		}
		
		if(!isResolved) {// 找第一个即可
			if(oversize) {
				Entry<FlowSpillKey, Double> entry = list.get(index - 1);
				
				FlowSpillKey key = entry.getKey();
				DNSMap dnsmap = key.getDnsmap();
				String view = key.getView();
				
				solve(dnsmap, view, curProblemCluster, crashedClusters, flowSpillClusters);
				
			} else {
				throw new CanNotPartakeFlowException(this.getClass().getName() + " not resolved");
			}
		}
		
		/*HashMap<String, List<Cluster>> map = dnsmap.getMap();
		List<Cluster> enabledClusters = dnsmap.getClusters();
		HashMap<String, Double> view2Flow = dnsmap.getView2Flow();
		MultiKeyMap clusterViewQOS = dnsmap.getClusterViewQOS();
		
		Set<String> views = map.keySet();
		for(String view: views) {
			List<Cluster> usedClusters = map.get(view);
			
			Cluster curProblemCluster = (curProblem).getCluster();
			
			if(usedClusters.contains(curProblemCluster)) {
				//TreeMap<Integer, List<Cluster>> levelClusters = ClusterDesion.getLevelClusters(view, enabledClusters);
				usedClusters.remove(curProblemCluster);
				List<Cluster> excludeClusters = Arrays.asList(curProblemCluster);
				
				List<Cluster> resultClusters = decision.make(view, usedClusters, enabledClusters, excludeClusters, view2Flow.get(view), clusterViewQOS);
				
				for(Cluster resultCluster: resultClusters) {
					usedClusters.add(resultCluster);
				}
			}
		}*/
	}
}

class FlowSpillKey {
	private DNSMap dnsmap;
	private String view;
	
	public FlowSpillKey(DNSMap dnsmap, String view) {
		super();
		this.dnsmap = dnsmap;
		this.view = view;
	}
	public DNSMap getDnsmap() {
		return dnsmap;
	}
	public void setDnsmap(DNSMap dnsmap) {
		this.dnsmap = dnsmap;
	}
	public String getView() {
		return view;
	}
	public void setView(String view) {
		this.view = view;
	}
}
