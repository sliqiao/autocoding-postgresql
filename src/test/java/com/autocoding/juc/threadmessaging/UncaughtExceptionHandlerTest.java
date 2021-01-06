package com.autocoding.juc.threadmessaging;

import java.lang.Thread.UncaughtExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UncaughtExceptionHandlerTest {
	public static void main(String[] args) throws InterruptedException {
		final Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				final int i = 0;
				final int j = 5 / i;

			}
		});
		thread.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {

			@Override
			public void uncaughtException(Thread t, Throwable e) {
				UncaughtExceptionHandlerTest.log.error("当前线程：" + t.getName(), e);

			}
		});
		thread.start();
		thread.join();
	}
}
