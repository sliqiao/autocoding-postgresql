package com.autocoding.threadpool;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TimeoutUtilTest {
	@Test
	public void test_runnable_timeout() throws Exception {
		TimeoutUtil.execute(new MyRunnable(1), 5);
		TimeoutUtilTest.log.info("方法返回!!!!!!!!");
		TimeUnit.SECONDS.sleep(100);
	}

	private static class MyRunnable implements Runnable {

		private final Integer executionInSeconds;

		public MyRunnable(Integer executionInSeconds) {
			this.executionInSeconds = executionInSeconds;
		}

		@Override
		public void run() {
			//模拟方法执行时间：executionInSeconds
			try {
				TimeoutUtilTest.log.info("开始执行");
				TimeUnit.SECONDS.sleep(this.executionInSeconds);
				TimeoutUtilTest.log.info("结束执行........");
			} catch (final InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {

			}

		}

	}

}
