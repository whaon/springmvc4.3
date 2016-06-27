package com.my.entry;

import java.util.ArrayList;
import java.util.List;

import com.my.entity.Cluster;
import com.my.entity.DNSMap;
import com.my.service.ClusterCrashProblem;
import com.my.service.FlowSpillProblem;
import com.my.service.Problem;
import com.my.service.ProblemSolver;

public class Controller {

	ProblemSolver solver = new ProblemSolver();
	
	public void execute(List<ClusterCrashProblem> clusterCrashProblems, List<FlowSpillProblem> flowSpillProblems,
			List<DNSMap> dnsmaps) {
		List<Cluster> crashedClusters = new ArrayList<Cluster>();
		for (ClusterCrashProblem curProblem : clusterCrashProblems) {
			crashedClusters.add(((ClusterCrashProblem) curProblem).getCluster());
			for (DNSMap dnsmap : dnsmaps) {
				solver.solveClusterCrashProblem((ClusterCrashProblem) curProblem, dnsmap);
			}
		}
		
		List<Cluster> flowSpillClusters = new ArrayList<Cluster>();
		for (FlowSpillProblem curProblem : flowSpillProblems) {
			flowSpillClusters.add(curProblem.getCluster());
		}
		
		for (FlowSpillProblem curProblem : flowSpillProblems) {
			try {
				solver.solveFlowSpillProblem((FlowSpillProblem) curProblem, dnsmaps, crashedClusters, flowSpillClusters);
				flowSpillClusters.remove(curProblem.getCluster());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void m(List<Problem> problems, List<DNSMap> dnsmaps) {
		// 对problems排序
		/*Collections.sort(problems, new Comparator<Problem>() {

			@Override
			public int compare(Problem o1, Problem o2) {
				if(o1 instanceof ClusterCrashProblem) {
					if(!(o2 instanceof ClusterCrashProblem)) {
						return -1;
					}
				}
				if(o2 instanceof ClusterCrashProblem) {
					if(!(o1 instanceof ClusterCrashProblem)) {
						return 1;
					}
				}
				return 0;
			}
		});*/
		
		
		
	}
}

