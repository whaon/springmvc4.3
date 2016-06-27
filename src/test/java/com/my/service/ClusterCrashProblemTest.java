package com.my.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.collections.map.MultiKeyMap;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.Test;

import com.my.entity.Cluster;
import com.my.entity.DNSMap;
import com.my.entity.QOS;
import com.my.entry.Controller;
import com.my.entry.Main;
import com.my.util.JsonUtil;

public class ClusterCrashProblemTest {
	
	//ClusterDesion dec;
	
	//private static Multimap<String, QOS> clusterViewQOS = HashMultimap.create();
	
	//
	
	//private static ExecutorService executorService = Executors.newCachedThreadPool();
	
	//private static Map<String, Map<String, Integer>> view2ClusterQOS = new HashMap<String, Map<String, Integer>>();
	
	//private static Map<String, List<String>> finalDNSMAP = new HashMap<String, List<String>>();
	
	//static LinkedList<String> views = new LinkedList<String>();
	//static List<Cluster> clusters = new ArrayList<Cluster>();
	
	public void init() {
		
		Map<String, Cluster> clustersMap = new HashMap<String, Cluster>();
		
		MultiKeyMap clusterViewQOS = new MultiKeyMap();
		
		//private static Map<String, Double> bindwidthWillAddMap = new HashMap<String, Double>();
		
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
			//clusters.add(c);
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
		
		/*try {
			String bindwidth = FileUtils.readFileToString(new File(Main.class.getClassLoader().getResource("view_bindwidth_forecast.json").getFile()));
			bindwidthWillAddMap = JsonUtil.parse(bindwidth, Map.class);
			//System.out.println(bindwidthMap);
			//List<QOS> shanghai = JsonUtil.parse(shanghaiQOS, List.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(bindwidthWillAddMap);*/
		
		/*try {
			List<String> lines = FileUtils.readLines(new File(Main.class.getClassLoader().getResource("view.json").getFile()));
			for(String line: lines) {
				//finalDNSMAP.put(line, new ArrayList<String>());
				views.add(line);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		//dec = new ClusterDesion();
		//ClusterDesion.clusterViewQOS = clusterViewQOS;
	}
	
	@Test
	public void test() {
		init();
		StopWatch s = new StopWatch();
		s.start();
		
		List<Problem> problems = new ArrayList<Problem>();
		
		ClusterCrashProblem problem = new ClusterCrashProblem();
		problem.setCluster(new Cluster("shanghai_cluster"));
		
		problems.add(problem);
		
		List<DNSMap> dnsmaps = new ArrayList<DNSMap>();
		
		DNSMap map = new DNSMap();
		dnsmaps.add(map);
		
		Collections.sort(dnsmaps, new Comparator<DNSMap>() {

			@Override
			public int compare(DNSMap o1, DNSMap o2) {
				return o2.getRank() - o1.getRank();
			}
		});
		
		Controller c = new Controller();
		c.make(problems, dnsmaps);
		
		s.stop();
		System.out.println(s.getTime() + "ms");
	}
	
	@Test
	public void testCollection() {
		List<String> l1 = new ArrayList<String>();
		l1.add("a");
		l1.add("b");
		l1.add("c");
		l1.add("d");
		
		List<String> l2 = new ArrayList<String>();
		l2.add("x");
		l2.add("b");
		l2.add("c");
		l2.add("y");
		
		System.out.println(ListUtils.intersection(l1, l2));
		System.out.println(ListUtils.retainAll(l1, l2));
		
		System.out.println(ListUtils.removeAll(l1, l2));
		
		System.out.println(l1);
		System.out.println(l2);
		
	}

}
