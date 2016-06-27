package com.my.util;

import java.util.Comparator;

import com.my.entity.DNSMap;

public class DNSMapRankComparator implements Comparator<DNSMap> {

	@Override
	public int compare(DNSMap o1, DNSMap o2) {
		if(o1.getRank() > o2.getRank()) {
			return 1;
		} 
		return 0;
	}
	
}
