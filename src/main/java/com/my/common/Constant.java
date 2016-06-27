package com.my.common;

import java.util.Map;

import com.my.entity.Cluster;

public class Constant {
	public static int depth = 5;// 最多用几个cluster来覆盖
	
	public static int[] boundary = {100, 80, 60, 0};
	
	public static int levelSize = boundary.length - 1;
	
	public static double lowestRate = 0.9;
	public static double middleRate = 0.5;
	
	// 定时更新
	public static Map<String, Cluster> clustersMap;
	
}
