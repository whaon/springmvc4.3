package com.my.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

public class OldClusterDesion extends AbstractClusterDesicion {
	
	/*protected MultiKeyMap clusterViewQOS;

	//public Map<String, Cluster> clusterMaps;
	
	public ClusterDesion(MultiKeyMap clusterViewQOS) {
		super();
		this.clusterViewQOS = clusterViewQOS;
	}*/
	
	//int depth = Constant.depth;
	int levelSize = Constant.levelSize;
	
	double lowestRate = Constant.lowestRate;
	
	/* (non-Javadoc)
	 * @see com.my.service.ClusterDesicion#make(java.lang.String, java.util.List, java.util.List, java.util.List, double, org.apache.commons.collections.map.MultiKeyMap)
	 */
	@Override
	public List<Cluster> make(final String curView, List<Cluster> areadyClusters, 
			List<Cluster> enabledClusters, List<Cluster> excludeClusters, double totalFlow,
			MultiKeyMap clusterViewQOS) throws Exception {
		
		List<Cluster> result = new ArrayList<Cluster>();
		
		TreeMap<Integer, TreeSet<Cluster>> levelClusters = getLevelClusters(curView, enabledClusters, areadyClusters, excludeClusters, clusterViewQOS);
		
		LevelClusterChain chain = null;
		Set<Map.Entry<Integer, TreeSet<Cluster>>> levelClusterEntrySet = levelClusters.entrySet();
		
		LevelClusterChain previous = null;
		for(Map.Entry<Integer, TreeSet<Cluster>> entry: levelClusterEntrySet) {
			LevelClusterChain cur = new LevelClusterChain();
			if(previous != null) {
				previous.setNext(cur);
			} else {
				chain = cur;
			}
			TreeSet<Cluster> cs = entry.getValue();
			/*if(areadyClusters != null) {
				for(Cluster ec: areadyClusters) {
					if(cs.contains(ec)){
						cs.remove(ec);
					}
				}
			} else {
				areadyClusters = new ArrayList<Cluster>();// for using size() method
			}
			if(excludeClusters != null) {
				for(Cluster ec: excludeClusters) {
					if(cs.contains(ec)){
						cs.remove(ec);
					}
				}
			}*/
			cur.setClusters(cs);
			
			previous = cur;
		}
		
		System.out.println(chain);
		
		int areadyClusterSize = areadyClusters == null ? 0 : areadyClusters.size();
		int depth = Constant.depth - areadyClusterSize;
		
		Integer[][] dimArray = new Integer[depth][levelSize];
		
		boolean find = false;
		
		Integer resultX = null;
		Integer resultY = null;
		
		TreeSet<Cluster> usedBestClusters  = chain.getClusters();
		for(int i=0;i<depth;i++) {
			double curAddedFlow = totalFlow / (i + 1 + areadyClusterSize);
			int fitNo = getFitNo(usedBestClusters, curAddedFlow);
			if(fitNo >= (i+1)) {
				resultX = i;
				resultY = 0;
				find = true;
			}
		}
		
		if(!find) {
			for(int i=0;i<depth;i++) {
				double curAddedFlow = totalFlow / (i + 1 + areadyClusterSize);
				
				LevelClusterChain tempChain = chain;
				
				int j=0;
				while(true) {
					TreeSet<Cluster> usedClusters  = tempChain.getClusters();
					int fitNo = getFitNo(usedClusters, curAddedFlow);
					dimArray[i][j] = fitNo;
					
					int subTotal = 0;
					for(int x=0;x<=j;x++) {
						subTotal = subTotal + dimArray[i][x];
					}
					if(subTotal >= (i+1)) {
						/*if(j == 0) {
							find = true;
							resultX = i;
							resultY = 0;
						}*/
						break;
					}
					
					tempChain = tempChain.getNext();
					if(tempChain == null) {
						/*int subTotal2 = 0;
						for(int x=0;x<=j;x++) {
							subTotal2 = subTotal2 + dimArray[i][x];
						}
						if(subTotal2 < (i + 1)) {// 说明无解,把值都置为null
							for(int x=0;x<=j;x++) {
								dimArray[i][x] = null;
							}
						}*/
						for(int x=0;x<=j;x++) {// 不用计算了,能到这一步说明肯定不满足了
							dimArray[i][x] = null;
						}
						break;
					}
					
					j++;
				}
					
				/*if(find) {
					break;
				}*/
			}
		}
		
		if(!find) {
			double max = 0;
			for(int i=0;i<levelSize;i++) {
				for(int j=0;j<depth;j++) {
					if(dimArray[j][i] != null) {
						if(dimArray[j][i] != 0) {
							double temp = dimArray[j][i] * 1.0/(j + 1);
							if(temp > max) {// 不能等于,1个80~60的比2个80~60的要好
								max = temp;
								resultX = j;
							}
						}
					}
				}
				if(max != 0) {
					resultY = i;
					break;// 得解
				}
			}
		}
		
		List<Cluster> resultClusters = new ArrayList<Cluster>();
		if(resultX != null && resultY != null) {// 已经有解了
			int finalDepth = resultX+1;
			int alreadyNo = 0;
			double loadFlow = totalFlow / (finalDepth + areadyClusterSize);
			LevelClusterChain tempChain = chain;
			for(int j=0;j<=resultY;j++) {
				alreadyNo = alreadyNo + dimArray[resultX][j];
				TreeSet<Cluster> cs = tempChain.getClusters();
				if(alreadyNo <= finalDepth) {
					/*for(Cluster c: cs) {
						result.add(c.getName());
						c.setAddedBandWidth(c.getAddedBandWidth() + loadFlow);
					}*/
					resultClusters.addAll(cs);
				} else {// 说明有多个满足,挑出几个即可
					int usedNO = finalDepth - (alreadyNo - dimArray[resultX][j]);
					List<Cluster> rs = getUsedCluster(cs, usedNO, curView, clusterViewQOS);
					resultClusters.addAll(rs);
				}
				tempChain = tempChain.getNext();
			}
			
			for(Cluster c: resultClusters) {
				//result.add(c.getName());
				result.add(c);
				//c.setAddedBandWidth(c.getAddedBandWidth() + loadFlow);
				c.setCurBandWidth(c.getCurBandWidth() + loadFlow);
			}
			
			// areadyClusters应该要减少一部分负担的流量
			if(finalDepth > 1) {
				if(areadyClusters != null) {
					for(Cluster c: areadyClusters) {
						//c.setAddedBandWidth(totalFlow/(resultClusters.size() + 1) - totalFlow/(resultClusters.size() + finalDepth));
						c.setCurBandWidth(c.getCurBandWidth() - (totalFlow/(areadyClusterSize + 1) - loadFlow));
					}
				}
			}
		} else {
			throw new NoSolutionException("no solution!");
		}
		
		System.out.println(curView + "---" + result);
		
		/*for(Cluster c: areadyClusters) {
			result.add(c.getName());
		}*/
		
		return result;
		
	}
	
	public List<Cluster> make2(final String curView, List<Cluster> areadyClusters, 
			List<Cluster> enabledClusters, List<Cluster> excludeClusters, double totalFlow,
			MultiKeyMap clusterViewQOS) throws Exception {
		List<Cluster> result = new ArrayList<Cluster>();
		
		TreeMap<Integer, TreeSet<Cluster>> levelClusters = getLevelClusters(curView, enabledClusters, areadyClusters, excludeClusters, clusterViewQOS);
		
		List<TreeSet<Cluster>> levelClusterList = new ArrayList<TreeSet<Cluster>>();
		for(Map.Entry<Integer, TreeSet<Cluster>> entry: levelClusters.entrySet()) {
			levelClusterList.add(entry.getValue());
		}
		
		boolean find = false;
		
		Integer resultX = 0;
		Integer resultY = 0;
		
		int areadyClusterSize = areadyClusters == null ? 0 : areadyClusters.size();
		
		int depth = Constant.depth - areadyClusterSize;
		
		for(int z=0;z<=levelSize;z++) {
			LevelClusterChain chain = null;
			Set<Map.Entry<Integer, TreeSet<Cluster>>> levelClusterEntrySet = levelClusters.entrySet();
			
			LevelClusterChain previous = null;
			int index = 0;
			for(Map.Entry<Integer, TreeSet<Cluster>> entry: levelClusterEntrySet) {
				if(index++ > z) {
					break;
				}
				LevelClusterChain cur = new LevelClusterChain();
				if(previous != null) {
					previous.setNext(cur);
				} else {
					chain = cur;
				}
				TreeSet<Cluster> cs = entry.getValue();
				cur.setClusters(cs);
				
				previous = cur;
			}
			
			int dimArrayXLength = z+1;
			Integer[][] dimArray = new Integer[depth][dimArrayXLength];
			
			for(int i=0;i<depth;i++) {
				double curAddedFlow = totalFlow / (i + 1 + areadyClusterSize);
				
				LevelClusterChain tempChain = chain;
				
				int j=0;
				while(true) {
					TreeSet<Cluster> usedClusters  = tempChain.getClusters();
					int fitNo = getFitNo(usedClusters, curAddedFlow);
					dimArray[i][j] = fitNo;
					
					int subTotal = 0;
					for(int x=0;x<=j;x++) {
						subTotal = subTotal + dimArray[i][x];
					}
					if(subTotal >= (i+1)) {
						if(j == 0) {
							find = true;
							resultX = i;
							resultY = 0;
						}
						break;
					}
					
					tempChain = tempChain.getNext();
					if(tempChain == null) {
						/*int subTotal2 = 0;
						for(int x=0;x<=j;x++) {
							subTotal2 = subTotal2 + dimArray[i][x];
						}
						if(subTotal2 < (i + 1)) {// 说明无解,把值都置为null
							for(int x=0;x<=j;x++) {
								dimArray[i][x] = null;
							}
						}*/
						for(int x=0;x<=j;x++) {// 不用计算了,能到这一步说明肯定不满足了
							dimArray[i][x] = null;
						}
						break;
					}
					
					j++;
				}
			}
			
			if(!find) {
				double max = 0;
				for(int i=0;i<dimArrayXLength;i++) {
					for(int j=0;j<depth;j++) {
						if(dimArray[j][i] != null) {
							if(dimArray[j][i] != 0) {
								double temp = dimArray[j][i] * 1.0/(j + 1);
								if(temp > max) {// 不能等于,1个80~60的比2个80~60的要好
									max = temp;
									resultX = j;
								} else if(max != 0 && temp == max) {
									int x = i + 1;
									while(x < dimArrayXLength) {
										if(dimArray[j][x]*1.0/(j+1)> dimArray[resultX][x]*1.0/(resultX+1)) {
											resultX = j;
											break;
										}
										if(dimArray[j][x] < dimArray[resultX][x]) {
											break;
										}
										x++;
									}
								}
							}
						}
					}
					/*if(max != 0) {
						resultY = i;
						find = true;
						break;// 得解
					}*/
					if(resultX != null) {
						break;
					}
						
				}
			}
			
			if(resultX != null) {
				resultY = dimArrayXLength - 1;
				find = true;
			}
			
			List<Cluster> resultClusters = new ArrayList<Cluster>();
			if(find) {// 已经有解了
				int finalDepth = resultX+1;
				int alreadyNo = 0;
				double loadFlow = totalFlow / (finalDepth + areadyClusterSize);
				LevelClusterChain tempChain = chain;
				for(int j=0;j<=resultY;j++) {
					alreadyNo = alreadyNo + dimArray[resultX][j];
					TreeSet<Cluster> cs = tempChain.getClusters();
					List<Cluster> availableClusters = getFitClusters(cs, loadFlow);
					if(alreadyNo <= finalDepth) {
						/*for(Cluster c: cs) {
							result.add(c.getName());
							c.setAddedBandWidth(c.getAddedBandWidth() + loadFlow);
						}*/
						resultClusters.addAll(availableClusters);
					} else {// 说明有多个满足,挑出几个即可
						int usedNO = finalDepth - (alreadyNo - dimArray[resultX][j]);
						List<Cluster> rs = getUsedCluster(availableClusters, usedNO, curView, clusterViewQOS);
						resultClusters.addAll(rs);
					}
					tempChain = tempChain.getNext();
				}
				
				for(Cluster c: resultClusters) {
					//result.add(c.getName());
					result.add(c);
					//c.setAddedBandWidth(c.getAddedBandWidth() + loadFlow);
					c.setCurBandWidth(c.getCurBandWidth() + loadFlow);
				}
				
				// areadyClusters应该要减少一部分负担的流量
				if(finalDepth > 1) {
					if(areadyClusters != null) {
						for(Cluster c: areadyClusters) {
							//c.setAddedBandWidth(totalFlow/(resultClusters.size() + 1) - totalFlow/(resultClusters.size() + finalDepth));
							c.setCurBandWidth(c.getCurBandWidth() - (totalFlow/(areadyClusterSize + 1) - loadFlow));
						}
					}
				}
				
				break;
			}
		}
		
		if(!find) {
			throw new NoSolutionException("no solution!");
		}
		return result;
	}
	
	public static void main(String[] args) {
		TreeMap<Integer, List<Cluster>> levelClusters = new TreeMap<Integer, List<Cluster>>(new Comparator<Integer>() {

			@Override
			public int compare(Integer o1, Integer o2) {
				return o2 - o1;
			}

			
		});
		levelClusters.put(80, Arrays.asList(new Cluster("c"), new Cluster("d")));
		levelClusters.put(100, Arrays.asList(new Cluster("a"), new Cluster("b")));
		levelClusters.put(60, Arrays.asList(new Cluster("e"), new Cluster("f")));
		//new ClusterDesion().make("", levelClusters, 60);
		
		List<StringBuffer> l1 = new ArrayList<StringBuffer>();
		l1.add(new StringBuffer("a"));
		l1.add(new StringBuffer("b"));
		List<StringBuffer> l2 = new ArrayList<StringBuffer>();
		l2.addAll(l1);
		
		l2.get(0).append("x");
		
		System.out.println(l1.get(0));
		System.out.println(l2.get(0));
		
		System.out.println(l1.subList(0, 2));
		
		List<C> cs = new ArrayList<C>();
		cs.add(new C(0.4, 85));
		cs.add(new C(0.3, 75));
		cs.add(new C(0.6, 75));
		cs.add(new C(0.7, 85));

		Collections.sort(cs, new Comparator<C>() {

			@Override
			public int compare(C o1, C o2) {
				double rate1 = o1.rate;
				double rate2 = o2.rate;
				if (rate1 < 0.5) {
					if (rate2 < 0.5) {
						int qos1 = o1.qos;
						int qos2 = o2.qos;
						return (qos1 > qos2) ? -1 : 1;
					} else {
						return -1;
					}
				}
				if (rate1 > 0.5) {
					if (rate2 < 0.5) {
						return 1;
					} else {
						int qos1 = o1.qos;
						int qos2 = o2.qos;
						return (qos1 > qos2) ? -1 : 1;
					}
				}
				return 0;
			}
		});

	}
}

class C {
	double rate;
	int qos;
	public C(double rate, int qos) {
		super();
		this.rate = rate;
		this.qos = qos;
	}
	@Override
	public String toString() {
		return "C [rate=" + rate + ", qos=" + qos + "]";
	}
}

class LevelClusterChain {
	private TreeSet<Cluster> clusters;
	private LevelClusterChain next;
	public TreeSet<Cluster> getClusters() {
		return clusters;
	}
	public void setClusters(TreeSet<Cluster> clusters) {
		this.clusters = clusters;
	}
	public LevelClusterChain getNext() {
		return next;
	}
	public void setNext(LevelClusterChain next) {
		this.next = next;
	}
}

/*TreeSet<Cluster> set = new TreeSet<Cluster>(new Comparator<Cluster>() {

	@Override
	public int compare(Cluster o1, Cluster o2) {
		int qos1 = (int) clusterViewQOS.get(curView, o1.getName());
		int qos2 = (int) clusterViewQOS.get(curView, o2.getName());
		if(qos1 > qos2) {
			return 1;
		} else if(qos1 < qos2) {
			return -1;
		}
		return 0;
	}
});*/
