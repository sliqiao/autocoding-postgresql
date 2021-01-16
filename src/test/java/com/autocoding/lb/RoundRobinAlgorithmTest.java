package com.autocoding.lb;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @ClassName:  RoundRobinAlgorithmTest   
 * @Description:  RoundRobin
 * @author: QiaoLi
 * @date:   Jan 16, 2021 1:41:55 PM
 */
public class RoundRobinAlgorithmTest {

	private static List<String> IP_LIST = new ArrayList<>();
	private int currentIndex = 0;
	static {
		IP_LIST.add("192.168.13.1");
		IP_LIST.add("192.168.13.2");
		IP_LIST.add("192.168.13.3");

	}

	public synchronized String selectServer() {

		if (currentIndex >= IP_LIST.size()) {
			currentIndex = 0;
		}
		String selectedServer = IP_LIST.get(currentIndex);
		currentIndex++;
		return selectedServer;
	}

	public static void main(String[] args) {
		RoundRobinAlgorithmTest roundRobinAlgorithmTest = new RoundRobinAlgorithmTest();

		for (int i = 0; i < 10; i++) {
			String server = roundRobinAlgorithmTest.selectServer();
			System.out.println(server);
		}

	}
}