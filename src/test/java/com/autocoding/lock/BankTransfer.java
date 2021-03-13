
package com.autocoding.lock;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * 银行转账场景：为控制并发，需要保证转账的原子操作
 * <P>
 * A账户向B账户转账10次，模拟并发
 * </P>
 */
@Slf4j
public class BankTransfer {

	private static Map<String, Object> lockMap = new HashMap<>();
	private static final Account FROM = new Account("A账户", 100);
	private static final Account TO = new Account("B账户", 100);

	public static void main(String[] args) throws InterruptedException {
		System.err.println(String.format("转账之前,A账户余额：%d,B账户余额：%d", BankTransfer.FROM.getBalance(),
				BankTransfer.TO.getBalance()));
		final CountDownLatch countDownLatch = new CountDownLatch(10);
		for (int i = 1; i <= 10; i++) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					BankTransfer.transferWithLock(BankTransfer.FROM, BankTransfer.TO);
					countDownLatch.countDown();
				}
			}).start()
			;
		}

		countDownLatch.await();
		System.err.println(String.format("转账完成,A账户余额：%d,B账户余额：%d", BankTransfer.FROM.getBalance(),
				BankTransfer.TO.getBalance()));
	}

	private static void transferWithLock(Account from, Account to) {
		final Object lock = BankTransfer.getLock(from, to);
		final int totalAmount = 10;
		synchronized (lock) {
			from.setBalance(from.getBalance() - totalAmount);
			to.setBalance(to.getBalance() + totalAmount);
		}
	}

	public static synchronized Object getLock(Account from, Account to) {
		final String key = from.getName() + "_" + to.getName();
		if (BankTransfer.lockMap.containsKey(key)) {
			return BankTransfer.lockMap.get(key);
		}
		final Object object = new Object();
		BankTransfer.lockMap.put(key, object);
		return object;
	}

	private static class Account {
		private String name;
		private Integer balance;

		public String getName() {
			return this.name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Integer getBalance() {
			return this.balance;
		}

		public void setBalance(Integer balance) {
			this.balance = balance;
		}

		public Account(String name, Integer balance) {
			this.name = name;
			this.balance = balance;
		}

	}

}
