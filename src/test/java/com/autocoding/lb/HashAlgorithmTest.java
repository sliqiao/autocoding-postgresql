package com.autocoding.lb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 
 * @ClassName:  HashAlgorithmTest   
 * @Description:  基于哈希函数的负载均衡算法
 * @author: QiaoLi
 * @date:   Jan 16, 2021 1:46:44 PM
 */
public class HashAlgorithmTest {

	private static List<String> IP_LIST = new ArrayList<>();
	static {
		IP_LIST.add("192.168.13.1");
		IP_LIST.add("192.168.13.2");
		IP_LIST.add("192.168.13.3");

	}

	public static String selectServer(String clientIp) {

		int index = Math.abs(clientIp.hashCode()) % IP_LIST.size();
		String selectedServer = IP_LIST.get(index);
		return selectedServer;
	}

	public static void main(String[] args) {

		List<String> clientIpList = Arrays.asList("192.168.1.101", "192.168.1.102", "192.168.1.103");
		for (String clientIp : clientIpList) {
			String server = HashAlgorithmTest.selectServer(clientIp);
			System.out.println(server);
		}

	}
}