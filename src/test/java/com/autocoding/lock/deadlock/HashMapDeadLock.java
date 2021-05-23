package com.autocoding.lock.deadlock;

import java.util.HashMap;

/**
 * 用Jprofiler来观察线程100%的情况
 * HashMap在多线程环境下是非线程安全的，在高并发写场景下，可能会出现死循环，而导致CPU100%
 * @author Administrator
 *
 */
public class HashMapDeadLock {
	private final HashMap<String, String> hashMap = new HashMap<>();

	public void add(String key, String value) {
		this.hashMap.put(key, value);
	}

	public static void main(String[] args) {
		final HashMapDeadLock hashMapDeadLock = new HashMapDeadLock();
		for (int i = 1; i <= 2; i++) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					for (int counter = 1; counter <= Integer.MAX_VALUE; counter++) {
						/*try {
							TimeUnit.SECONDS.sleep(2);
						} catch (final InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}*/
						hashMapDeadLock.add(String.valueOf(counter), String.valueOf(counter));
					}
				}
			}, "线程-" + i).start();
		}
	}
}
