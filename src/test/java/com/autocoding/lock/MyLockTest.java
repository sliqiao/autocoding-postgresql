
package com.autocoding.lock;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyLockTest {
	private static Lock lock = new MyLock();

	public static void main(String[] args) {
		final List<Thread> threadList = new ArrayList<>();
		// step 1:创建10个线程
		for (int i = 1; i <= 5; i++) {
			threadList.add(new Thread(new Runnable() {

				@Override
				public void run() {

					try {
						MyLockTest.lock.lock();
						MyLockTest.log.info("当前线程【{}】获取到锁，开始执行", Thread.currentThread().getName());
						TimeUnit.SECONDS.sleep(5);
					} catch (final Exception e) {
						System.err.println(e.getMessage());
					} finally {
						MyLockTest.log.info("当前线程【{}】释放锁，结束执行", Thread.currentThread().getName());
						MyLockTest.lock.unlock();
					}

				}
			}, "thead-" + i));
		}
		// step 2:启动10个线程
		for (final Thread thread : threadList) {
			thread.start();
		}
		// step 3:主线程等待这10个线程执行完成
		for (final Thread thread : threadList) {
			try {
				thread.join();
			} catch (final InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
