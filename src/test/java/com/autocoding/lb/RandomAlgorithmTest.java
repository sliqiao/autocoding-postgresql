package com.autocoding.lb;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 
 * @ClassName:  RandomAlgorithmTest   
 * @Description:   随机算法
 * @author: QiaoLi
 * @date:   Jan 16, 2021 1:41:21 PM
 */
public class RandomAlgorithmTest {

	private static List<String> IP_LIST = new ArrayList<>();
	static {
		IP_LIST.add("192.168.13.1");
		IP_LIST.add("192.168.13.2");
		IP_LIST.add("192.168.13.3");

	}

	public static String selectServer() {

		Random random = new Random();
		int index = random.nextInt(IP_LIST.size());
		String selectedServer = IP_LIST.get(index);
		return selectedServer;
	}

	public static void main(String[] args) {

		for (int i = 0; i < 10; i++) {
			String server = RandomAlgorithmTest.selectServer();
			System.out.println(server);
		}

	}
}