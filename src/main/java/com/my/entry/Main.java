package com.my.entry;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.MultiKeyMap;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.StopWatch;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.LinkedListMultimap;
import com.my.entity.Cluster;
import com.my.entity.DNSMap;
import com.my.entity.QOS;
import com.my.service.InitializeDecisionMaker;
import com.my.util.JsonUtil;

public class Main {
	
	private static Map<String, Cluster> clustersMap = new HashMap<String, Cluster>();
	//private static Multimap<String, QOS> clusterViewQOS = HashMultimap.create();
	public static MultiKeyMap clusterViewQOS = new MultiKeyMap();
	private static Map<String, Integer> bindwidthWillAddMap = new HashMap<String, Integer>();
	
	//private static ExecutorService executorService = Executors.newCachedThreadPool();
	
	//private static Map<String, Map<String, Integer>> view2ClusterQOS = new HashMap<String, Map<String, Integer>>();
	
	//private static Map<String, List<String>> finalDNSMAP = new HashMap<String, List<String>>();
	
	static LinkedList<String> views = new LinkedList<String>();
	static List<Cluster> clusters = new ArrayList<Cluster>();
	
	//private static int[] boundary = {100,80,60,0};
	//private static Map<String, Multimap<Integer, QOS>> qosSplitMap = new HashMap<String, Multimap<Integer, QOS>>();
	//private static Map<Integer, LinkedListMultimap<String, QOS>> qosSplitMap = new HashMap<Integer, LinkedListMultimap<String, QOS>>();

	public static void main(String[] args) throws Exception {
		init();
		
		DNSMap map = new DNSMap();
		map.setClusters(clusters);
		
		
		InitializeDecisionMaker p = new InitializeDecisionMaker(map, bindwidthWillAddMap, views, clustersMap, clusterViewQOS);
		StopWatch s = new StopWatch();
		s.start();
		HashMap<String, List<String>> result = p.invoke();
		s.stop();
		
		System.out.println(s.getTime() + "ms");
		
		System.out.println(result);
		
	}

	public static void init() {
		String cluster = null;
		try {
			cluster = FileUtils.readFileToString(new File(Main.class.getClassLoader().getResource("cluster.json").getFile()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//System.out.println(cluster);
		
		List<Cluster> clusterList = JsonUtil.parseArray(cluster, Cluster.class);
		
		for(Cluster c: clusterList) {
			clustersMap.put(c.getName(), c);
			clusters.add(c);
		}
		
		System.out.println(clustersMap);
		
		try {
			String shanghaiQOS = FileUtils.readFileToString(new File(Main.class.getClassLoader().getResource("cluster_shanghai_qos.json").getFile()));
			//clusterViewQOS.putAll("shanghai_cluster", JsonUtil.parseArray(shanghaiQOS, QOS.class));
			
			List<QOS> list = JsonUtil.parseArray(shanghaiQOS, QOS.class);
			
			for(QOS q: list) {
				clusterViewQOS.put("shanghai_cluster", q.getViewName(), q.getQos());
			}
			
			//List<QOS> shanghai = JsonUtil.parse(shanghaiQOS, List.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			String tianjingQOS = FileUtils.readFileToString(new File(Main.class.getClassLoader().getResource("cluster_tianjing_qos.json").getFile()));
			//clusterViewQOS.putAll("tianjing_cluster", JsonUtil.parseArray(tianjingQOS, QOS.class));
			
			List<QOS> list = JsonUtil.parseArray(tianjingQOS, QOS.class);
			
			for(QOS q: list) {
				clusterViewQOS.put("tianjing_cluster", q.getViewName(), q.getQos());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			String xiamenQOS = FileUtils.readFileToString(new File(Main.class.getClassLoader().getResource("cluster_xiamen_qos.json").getFile()));
			//clusterViewQOS.putAll("xiamen_cluster", JsonUtil.parseArray(xiamenQOS, QOS.class));
			
			List<QOS> list = JsonUtil.parseArray(xiamenQOS, QOS.class);
			
			for(QOS q: list) {
				//clusterViewQOS.put("xiamen_cluster", q.getViewName(), q.getQos());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			String xianQOS = FileUtils.readFileToString(new File(Main.class.getClassLoader().getResource("cluster_xian_qos.json").getFile()));
			//clusterViewQOS.putAll("xian_cluster", JsonUtil.parseArray(xianQOS, QOS.class));
			
			List<QOS> list = JsonUtil.parseArray(xianQOS, QOS.class);
			
			for(QOS q: list) {
				//clusterViewQOS.put("xian_cluster", q.getViewName(), q.getQos());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(clusterViewQOS);
		
		try {
			String bindwidth = FileUtils.readFileToString(new File(Main.class.getClassLoader().getResource("view_bindwidth_forecast.json").getFile()));
			bindwidthWillAddMap = JsonUtil.parse(bindwidth, Map.class);
			//System.out.println(bindwidthMap);
			//List<QOS> shanghai = JsonUtil.parse(shanghaiQOS, List.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(bindwidthWillAddMap);
		
		try {
			List<String> lines = FileUtils.readLines(new File(Main.class.getClassLoader().getResource("view.json").getFile()));
			for(String line: lines) {
				//finalDNSMAP.put(line, new ArrayList<String>());
				views.add(line);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//System.out.println(finalDNSMAP);
		
		LinkedHashMultimap<String, QOS> clusterViewQOS = LinkedHashMultimap.create();
		
		LinkedListMultimap<String, QOS> clusterViewQOS2 = LinkedListMultimap.create();
	}
}
