package com.my.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections.map.MultiKeyMap;

public class DNSMap {
	private String name;
	// 所谓的DNSMAP
	private HashMap<String, List<Cluster>> map = new HashMap<String, List<Cluster>>();
	// view对应的流量,定时更新
	private HashMap<String, Double> view2Flow = new HashMap<String, Double>();
	// 可用的cluster
	private List<Cluster> clusters = new ArrayList<Cluster>();
	private int rank;
	
	private MultiKeyMap clusterViewQOS;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public MultiKeyMap getClusterViewQOS() {
		return clusterViewQOS;
	}
	public void setClusterViewQOS(MultiKeyMap clusterViewQOS) {
		this.clusterViewQOS = clusterViewQOS;
	}
	public List<Cluster> getClusters() {
		return clusters;
	}
	public void setClusters(List<Cluster> clusters) {
		this.clusters = clusters;
	}
	
	public HashMap<String, Double> getView2Flow() {
		return view2Flow;
	}
	public void setView2Flow(HashMap<String, Double> view2Flow) {
		this.view2Flow = view2Flow;
	}
	public HashMap<String, List<Cluster>> getMap() {
		return map;
	}
	public void setMap(HashMap<String, List<Cluster>> map) {
		this.map = map;
	}
	public int getRank() {
		return rank;
	}
	public void setRank(int rank) {
		this.rank = rank;
	}
	
}
