package com.autocoding.juc.threadmessaging;

import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * 面试题：新建 T1、T2、T3 三个线程，如何保证它们按顺序执行？ 期望执行顺序：T1、T2、T3
 */
@Slf4j
public class Thread$JoinTest {

	private static T1 t1 = new T1("T1");
	private static T2 t2 = new T2("T2");
	private static T3 t3 = new T3("T3");

	public static void main(String[] args) {
		Thread$JoinTest.t3.start();
		Thread$JoinTest.t2.start();
		Thread$JoinTest.t1.start();
	}

	private static class T1 extends Thread {

		public T1(String name) {
			super(name);
		}

		@Override
		public void run() {
			Thread$JoinTest.log.info("当前线程T1:开始运行");
			try {
				TimeUnit.SECONDS.sleep(5);
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
			Thread$JoinTest.log.info("当前线程T1:结束运行");
		}

	}

	private static class T2 extends Thread {
		public T2(String name) {
			super(name);
		}

		@Override
		public void run() {
			try {
				Thread$JoinTest.t1.join();
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
			Thread$JoinTest.log.info("当前线程T2:开始运行");
			try {
				TimeUnit.SECONDS.sleep(5);
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
			Thread$JoinTest.log.info("当前线程T2:结束运行");
		}

	}

	private static class T3 extends Thread {
		public T3(String name) {
			super(name);
		}

		@Override
		public void run() {
			try {
				Thread$JoinTest.t2.join();
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
			Thread$JoinTest.log.info("当前线程T3:开始运行");
			try {
				TimeUnit.SECONDS.sleep(5);
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
			Thread$JoinTest.log.info("当前线程T3:结束运行");
		}

	}
}
