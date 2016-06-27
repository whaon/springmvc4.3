package com.my.util;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.map.MultiKeyMap;

import com.my.entry.Main;

public class QAComparator implements Comparator<HashMap<String, List<String>>> {
	
	@Override
	public int compare(HashMap<String, List<String>> o1, HashMap<String, List<String>> o2) {
		if(QAComparator.evaluate(o1) - QAComparator.evaluate(o2) < 0) {
			return 1;
		} else {
			return -1;
		}
	}
	
	public static double evaluate(HashMap<String, List<String>> result) {
		
		MultiKeyMap clusterViewQOS = Main.clusterViewQOS;
		
		double total = 0;
		
		int size = 0;
		
		Set<String> keys = result.keySet();
		for(String view: keys) {
			List<String> cs = result.get(view);
			for(String c: cs) {
				size ++;
				int qos = (int) clusterViewQOS.get(c, view);
				total += qos;
			}
		}
		
		double avg = total * 1.0/size;
		
		return avg;
	}
}
