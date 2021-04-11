package com.autocoding.lock.livelock;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * 场景：
 * <p> t1线程先尝试获取lock1,再尝试获取lock2,如果获取lock2失败，则释放lock1，再进行重试 </p>
 * <p> t2线程先尝试获取lock2,再尝试获取lock1,如果获取lock1失败，则释放lock2，再进行重试 </p>
 * 这种场景是会出现活锁现象的
 * <p>解决方案1</p>
 * 加入一些随机因素，再次竞争资源时，重试时间间隔加入一些随机时间
 */
@Slf4j
public class Solve1LiveLockTest {
	public static void main(String[] args) throws InterruptedException {
		final Lock lock1 = new ReentrantLock();
		final Lock lock2 = new ReentrantLock();
		final Thread thread1 = new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					if (lock1.tryLock()) {
						try {
							Solve1LiveLockTest.log.info("获取了lock1,准备获取lock2.....");
							//为了模拟两个线程相互持有对方需要的锁，这里休眠一下
							try {
								TimeUnit.MILLISECONDS.sleep(1);
							} catch (final InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							if (lock2.tryLock()) {
								try {
									Solve1LiveLockTest.log.info("获取了lock2");
									Solve1LiveLockTest.log.info("业务逻辑处理");
									break;
								} finally {
									lock2.unlock();
								}
							}
						} finally {
							lock1.unlock();
						}
					}
					// 加入一些随机因素，再次竞争资源时，重试时间间隔加入一些随机时间
					try {
						TimeUnit.MILLISECONDS.sleep(new Random().nextInt(10));
					} catch (final InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

			}
		});
		thread1.setName("线程1");
		final Thread thread2 = new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					if (lock2.tryLock()) {
						try {
							Solve1LiveLockTest.log.info("获取了lock2,准备获取lock1.....");
							//为了模拟两个线程相互持有对方需要的锁，这里休眠一下
							try {
								TimeUnit.MILLISECONDS.sleep(1);
							} catch (final InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							if (lock1.tryLock()) {
								try {
									Solve1LiveLockTest.log.info("获取了lock1");
									Solve1LiveLockTest.log.info("业务逻辑处理");
									break;
								} finally {
									lock1.unlock();
								}
							}
						} finally {
							lock2.unlock();
						}
					}
					// 加入一些随机因素，再次竞争资源时，重试时间间隔加入一些随机时间
					try {
						TimeUnit.MILLISECONDS.sleep(new Random().nextInt(10));
					} catch (final InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
		});
		thread2.setName("线程2");
		thread1.start();
		thread2.start();
		thread1.join();
		thread2.join();
	}
}
