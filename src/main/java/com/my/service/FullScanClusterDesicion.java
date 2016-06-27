package com.my.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.collections.map.MultiKeyMap;

import com.my.common.Constant;
import com.my.entity.Cluster;
import com.my.exception.NoSolutionException;

public class FullScanClusterDesicion extends AbstractClusterDesicion {

	@Override
	public List<Cluster> make(final String view, List<Cluster> areadyClusters, List<Cluster> enabledClusters, List<Cluster> excludeClusters, double totalFlow, final MultiKeyMap clusterViewQOS)
			throws Exception {
		List<Cluster> resultClusters = null;

		List<Cluster> sortedClusters = getSortedClusters(view, areadyClusters, enabledClusters, excludeClusters, clusterViewQOS);
		boolean find = false;

		int areadyClusterSize = areadyClusters == null ? 0 : areadyClusters.size();

		int depth = Constant.depth - areadyClusterSize;
		
		double maxAVG = 0;
		for(int i=0; i<depth; i++) {
			double curAddedFlow = totalFlow / (i + 1 + areadyClusterSize);
			List<Cluster> finalClusters = getNeedFitClusters(sortedClusters, curAddedFlow, i+1);
			if(finalClusters.size() == (i+1)) {
				double total = 0;
				for (Cluster c : finalClusters) {
					total = total + (double) clusterViewQOS.get(c, view);
				}

				double avg = total / (i + 1);
				if (avg > maxAVG) {
					maxAVG = avg;

					find = true;
					resultClusters = finalClusters;
				}
			}
		}
		
		if (!find) {
			throw new NoSolutionException("no solution!");
		} else {
			changeFlow(totalFlow, resultClusters, areadyClusters);
		}
		
		return resultClusters;
	}
	
	private List<Cluster> getSortedClusters(final String view, List<Cluster> areadyClusters, List<Cluster> enabledClusters, List<Cluster> excludeClusters, final MultiKeyMap clusterViewQOS) {
		List<Cluster> ret = new ArrayList<Cluster>(enabledClusters);
		ret.removeAll(areadyClusters);
		ret.removeAll(excludeClusters);

		Collections.sort(ret, new Comparator<Cluster>() {

			@Override
			public int compare(Cluster o1, Cluster o2) {
				return (int) clusterViewQOS.get(o2.getName(), view) - (int) clusterViewQOS.get(o1.getName(), view);
			}
		});
		return ret;
	}

}
