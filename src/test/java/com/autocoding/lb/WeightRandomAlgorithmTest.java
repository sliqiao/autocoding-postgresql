package com.autocoding.lb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

/**
 * 
 * @ClassName:  WeightRandomAlgorithmTest   
 * @Description:  加权随机算法
 * @author: QiaoLi
 * @date:   Jan 16, 2021 1:41:21 PM
 */
public class WeightRandomAlgorithmTest {

	private static Map<String, Integer> IP_WEIGHT_MAP = new HashMap();
	private static List<String> IP_LIST = new ArrayList<>();
	static {
		IP_WEIGHT_MAP.put("192.168.13.1", 10);
		IP_WEIGHT_MAP.put("192.168.13.2", 20);
		IP_WEIGHT_MAP.put("192.168.13.3", 30);
		for (Entry<String, Integer> e : IP_WEIGHT_MAP.entrySet()) {
			for (int i = 0; i < e.getValue(); i++) {
				IP_LIST.add(e.getKey());
			}
		}

	}

	public static String selectServer() {

		Random random = new Random();
		int index = random.nextInt(IP_LIST.size());
		String selectedServer = IP_LIST.get(index);
		return selectedServer;
	}

	public static void main(String[] args) {

		for (int i = 0; i < 10; i++) {
			String server = WeightRandomAlgorithmTest.selectServer();
			System.out.println(server);
		}

	}
}