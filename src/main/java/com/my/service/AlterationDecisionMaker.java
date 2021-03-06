package com.my.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.collections.map.MultiKeyMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.my.entity.Cluster;
import com.my.entity.DNSMap;
import com.my.exception.InputDataException;
import com.my.util.DNSMapRankComparator;
import com.my.util.MyBeanUtils;

//@SuppressWarnings(value="unchecked")
public class AlterationDecisionMaker {
	
	private static Logger logger = LoggerFactory.getLogger(AlterationDecisionMaker.class);
	
	// 从大到小
	TreeSet<DNSMap> allDNSMaps = new TreeSet<DNSMap>(new DNSMapRankComparator());

	private static int[] boundary = {100, 80, 60, 0};
	int size = boundary.length;

	public Map<String, Integer> bindwidthWillAddMap;
	public LinkedList<String> views;
	public Map<String, Cluster> clusters;
	public MultiKeyMap clusterViewQOS;
	
	public AlterationDecisionMaker(Map<String, Integer> bindwidthWillAddMap, LinkedList<String> views, Map<String, Cluster> clusters, MultiKeyMap clusterViewQOS) {
		super();
		this.bindwidthWillAddMap = bindwidthWillAddMap;
		this.views = views;
		this.clusters = clusters;
		this.clusterViewQOS = clusterViewQOS;
	}


	// Map<Integer, HashMap<String, List<String>>>
	//private Map<Point, HashMap<String, List<String>>> results = new HashMap<Point, HashMap<String, List<String>>>();

	public void invoke() throws Exception {

		MultiKeyMap viewLevel2Cluster = new MultiKeyMap();

		//List<Map<String, List<String>>> result = new ArrayList<Map<String, List<String>>>();

		//Map<String, Map<String, Integer>> view2ClusterQOS = new HashMap<String, Map<String, Integer>>();

		int size = boundary.length - 1;

		//LinkedList<String> listChain = new LinkedList<String>();
		//listChain.addAll(views);

		// HashMap<String, List<String>>
		HashMap<String, List<String>> originalResult = new HashMap<String, List<String>>();

		for (String view : views) {

			originalResult.put(view, new ArrayList<String>());

			Set<String> cKeys = clusters.keySet();
			for (String ckey : cKeys) {
				Integer qos = (int) clusterViewQOS.get(ckey, view);
				for (int i = 0; i < size; i++) {
					Integer max = boundary[i];
					Integer min = boundary[i + 1];
					if (qos < max && qos >= min) {
						List<String> list = (List<String>) viewLevel2Cluster.get(view, max);
						if (list == null) {
							list = new ArrayList<String>();
							viewLevel2Cluster.put(view, max, list);
						}
						list.add(ckey);
					}
				}
			}
		}

		//Map<String, List<String>> singleResultCopy = new HashMap<String, List<String>>();
		//results.put(new Point(0, 0), singleResult);

		//Map<String, List<String>> singleResult = new HashMap<String, List<String>>();
		//AtomicInteger level = new AtomicInteger(0);
		processChain(viewLevel2Cluster, views, clusters, originalResult);


	}

	private void processChain(MultiKeyMap viewLevel2Cluster, LinkedList<String> views,
			Map<String, Cluster> clusters, HashMap<String, List<String>> result) throws Exception {

		String curView = views.remove();

		boolean flag = false;
		
		
		/*List<String> enabledClusters = (List<String>) viewLevel2Cluster.get(curView, 100);
		if (enabledClusters != null && enabledClusters.size() > 0) {
			for (String enabledCluster : enabledClusters) {
				Cluster cluster = clusters.get(enabledCluster);
				// cluster.getCurBandWidth() + bindwidthWillAddMap.get(curView)
				if (cluster.getCurBandWidth() + Integer.parseInt(bindwidthWillAddMap.get(curView) + "") < cluster.getMaxBandWidth()) {// 满足条件先
					HashMap<String, List<String>> singleResult;
					HashMap<String, List<Cluster>> resultCopy = MyBeanUtils.deepCopyBean(result);

					resultCopy.get(curView).add(cluster);

					flag = true;

					LinkedList<String> listChainCopy = MyBeanUtils.deepCopyBean(views);

					if (views.size() > 0) {
						processChain(viewLevel2Cluster, views, resultCopy);
					} else {
						System.out.println("---" + resultCopy);
						System.out.println("------" + QA.evaluate(resultCopy));
					}

					views = listChainCopy;
				}

			}

		}*/
		
		for(int i=0;i<size;i++) {
			if (flag) {
				return;
			}
			List<String> enabledClusters = (List<String>) viewLevel2Cluster.get(curView, boundary[i]);
			if (enabledClusters != null && enabledClusters.size() > 0) {
				for (String enabledCluster : enabledClusters) {
					Cluster cluster = clusters.get(enabledCluster);
					// cluster.getCurBandWidth() + bindwidthWillAddMap.get(curView)
					int addedBandWidth = Integer.parseInt(bindwidthWillAddMap.get(curView) + "");
					if (cluster.getCurBandWidth() + addedBandWidth < cluster.getMaxBandWidth()) {// 满足条件先
						HashMap<String, List<String>> resultCopy = MyBeanUtils.deepCopyBean(result);

						//resultCopy.get(curView).add(cluster);
						resultCopy.get(curView).add(enabledCluster);
						cluster.setAddedBandWidth(cluster.getAddedBandWidth() + addedBandWidth);

						flag = true;

						LinkedList<String> listChainCopy = MyBeanUtils.deepCopyBean(views);
						Map<String, Cluster> clustersCopy = MyBeanUtils.deepCopyBean(clusters);

						if (views.size() > 0) {
							processChain(viewLevel2Cluster, views, clustersCopy, resultCopy);
						} else {
							//System.out.println("---" + resultCopy);
							//System.out.println("------" + QA.evaluate(resultCopy));
						}

						views = listChainCopy;
					}

				}

			}
		}
		

		if(!flag) {
			//System.out.println(curView);
			logger.error("can not find cluster for view:" + curView);
			throw new InputDataException("can not find cluster for view:" + curView);
		}
		

		/*if (flag) {
			return;
		}

		enabledClusters = (List<String>) viewLevel2Cluster.get(curView, 80);
		if (enabledClusters != null && enabledClusters.size() > 0) {
			for (String enabledCluster : enabledClusters) {
				if (1 == 1) {// 满足条件先
					HashMap<String, List<String>> singleResult;

					HashMap<String, List<Cluster>> resultCopy = MyBeanUtils.deepCopyBean(result);

					Cluster cluster = clusters.get(enabledCluster);
					resultCopy.get(curView).add(cluster);

					flag = true;

					LinkedList<String> listChainCopy = MyBeanUtils.deepCopyBean(views);

					if (views.size() > 0) {
						processChain(viewLevel2Cluster, views, resultCopy);
					} else {
						System.out.println("---" + resultCopy);
						System.out.println("------" + QA.evaluate(resultCopy));
					}

					views = listChainCopy;
				}

			}

		}
*/
		// 降级
		//enabledClusters = (List<String>) viewLevel2Cluster.get(curView, 80);
	}

}

