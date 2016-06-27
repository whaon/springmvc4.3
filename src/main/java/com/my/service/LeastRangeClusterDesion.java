package com.my.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.collections.map.MultiKeyMap;

import com.my.common.Constant;
import com.my.entity.Cluster;
import com.my.exception.NoSolutionException;

public class LeastRangeClusterDesion extends AbstractClusterDesicion {

	int levelSize = Constant.levelSize;

	double lowestRate = Constant.lowestRate;

	/* (non-Javadoc)
	 * @see com.my.service.ClusterDesicion#make(java.lang.String, java.util.List, java.util.List, java.util.List, double, org.apache.commons.collections.map.MultiKeyMap)
	 */
	@Override
	public List<Cluster> make(final String curView, List<Cluster> areadyClusters, List<Cluster> enabledClusters, List<Cluster> excludeClusters, double totalFlow, MultiKeyMap clusterViewQOS)
			throws Exception {

		List<Cluster> resultClusters = null;

		TreeMap<Integer, TreeSet<Cluster>> levelClusters = getLevelClusters(curView, enabledClusters, areadyClusters, excludeClusters, clusterViewQOS);

		boolean find = false;

		/*Integer resultX = null;
		Integer resultY = null;*/

		int areadyClusterSize = areadyClusters == null ? 0 : areadyClusters.size();

		int depth = Constant.depth - areadyClusterSize;

		Set<Map.Entry<Integer, TreeSet<Cluster>>> levelClusterEntrySet = levelClusters.entrySet();

		for (int z = 0; z <= levelSize; z++) {
			LevelClusterChain chain = null;

			LevelClusterChain previous = null;
			int index = 0;
			for (Map.Entry<Integer, TreeSet<Cluster>> entry : levelClusterEntrySet) {
				if (index++ > z) {
					break;
				}
				LevelClusterChain cur = new LevelClusterChain();
				if (previous != null) {
					previous.setNext(cur);
				} else {
					chain = cur;
				}
				TreeSet<Cluster> cs = entry.getValue();
				cur.setClusters(cs);

				previous = cur;
			}

			int dimArrayXLength = z + 1;
			Integer[][] dimArray = new Integer[depth][dimArrayXLength];

			for (int i = 0; i < depth; i++) {
				double curAddedFlow = totalFlow / (i + 1 + areadyClusterSize);

				LevelClusterChain tempChain = chain;

				int j = 0;
				int subTotal = 0;
				while (true) {
					TreeSet<Cluster> usedClusters = tempChain.getClusters();
					int fitNo = getFitNo(usedClusters, curAddedFlow);
					dimArray[i][j] = fitNo;

					subTotal = subTotal + dimArray[i][j];
						
					if (subTotal >= (i + 1)) {
						break;
					}

					tempChain = tempChain.getNext();
					if (tempChain == null) {
						for (int x = 0; x <= j; x++) {// 不用计算了,能到这一步说明肯定不满足了
							dimArray[i][x] = null;
						}
						break;
					}
					j++;
				}
			}

			double maxAVG = 0;
			
			double loadFlow = 0;

			for (int i = 0; i < depth; i++) {
				double total = 0;
				LevelClusterChain tempChain = chain;
				loadFlow = totalFlow / ((i + 1) + areadyClusterSize);

				List<Cluster> finalClusters = new ArrayList<Cluster>();

				boolean isAnswer = true;

				for (int j = 0; j < dimArrayXLength; j++) {
					if (dimArray[i][j] == null) {
						isAnswer = false;
						break;
					}
					if (dimArray[i][j] != 0) {
						TreeSet<Cluster> cs = tempChain.getClusters();
						int needSize = dimArray[i][j];
						List<Cluster> availableClusters = getNeedFitClusters(cs, loadFlow, needSize);
						finalClusters.addAll(availableClusters);
					}
					tempChain = tempChain.getNext();
				}

				if (isAnswer) {
					for (Cluster c : finalClusters) {
						total = total + (double) clusterViewQOS.get(c, curView);
					}

					double avg = total / (i + 1);
					if (avg > maxAVG) {
						maxAVG = avg;
						//resultX = i;

						find = true;
						resultClusters = finalClusters;
					}
				}
			}
			
			if(find) {
				changeFlow(totalFlow, resultClusters, areadyClusters);
				break;
			}
		}
		
		if (!find) {
			throw new NoSolutionException("no solution!");
		}
		return resultClusters;
	}

	private TreeMap<Integer, TreeSet<Cluster>> getLevelClusters(final String view, List<Cluster> enabledClusters, List<Cluster> areadyClusters, List<Cluster> excludeClusters, final MultiKeyMap clusterViewQOS) {
		TreeMap<Integer, TreeSet<Cluster>> levelClusters = new TreeMap<Integer, TreeSet<Cluster>>(new Comparator<Integer>() {

			@Override
			public int compare(Integer o1, Integer o2) {
				return o2 - o1;
			}
		});

		int levelSize = Constant.levelSize;
		int[] boundary = Constant.boundary;

		for (Cluster c : enabledClusters) {
			if (areadyClusters != null && areadyClusters.size() > 0) {
				if (areadyClusters.contains(c)) {
					continue;
				}
			}
			if (excludeClusters != null && excludeClusters.size() > 0) {
				if (excludeClusters.contains(c)) {
					continue;
				}
			}
			Integer qos = (int) clusterViewQOS.get(c.getName(), view);
			for (int i = 0; i < levelSize; i++) {
				Integer max = boundary[i];
				Integer min = boundary[i + 1];
				if (qos < max && qos >= min) {
					TreeSet<Cluster> cs = levelClusters.get(max);
					if (cs == null) {
						cs = new TreeSet<Cluster>(new Comparator<Cluster>() {

							@Override
							public int compare(Cluster o1, Cluster o2) {
								return (int)clusterViewQOS.get(o2.getName(), view) - (int)clusterViewQOS.get(o1.getName(), view);
							}
						});
						levelClusters.put(max, cs);
					}
					cs.add(c);
				}
			}
		}

		return levelClusters;
	}
}
