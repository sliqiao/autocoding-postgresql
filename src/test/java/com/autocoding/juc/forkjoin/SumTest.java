package com.autocoding.juc.forkjoin;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SumTest {

	public static void main(String[] args) {

		SumTest.log.info("开始计算");
		for (int count = 1; count <= 10000; count++) {
			Integer sum = 0;
			for (int i = 1; i <= 10001; i++) {
				sum = sum + i;
			}
		}

		SumTest.log.info("结束计算........");
	}
}