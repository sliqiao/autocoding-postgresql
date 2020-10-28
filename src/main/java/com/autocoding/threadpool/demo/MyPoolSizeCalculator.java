package com.autocoding.threadpool.demo;

import java.lang.management.ManagementFactory;
import java.math.BigDecimal;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MyPoolSizeCalculator extends PoolSizeCalculator {

	public static void main(String[] args)
			throws InterruptedException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		MyPoolSizeCalculator calculator = new MyPoolSizeCalculator();
		calculator.calculateBoundaries(new BigDecimal(1.0), new BigDecimal(100000));
	}

	protected long getCurrentThreadCPUTime() {
		return ManagementFactory.getThreadMXBean().getCurrentThreadCpuTime();
	}

	protected Runnable creatTask() {
		return new AsynchronousTask(0, "IO", 1000000);
	}

	protected BlockingQueue createWorkQueue() {
		return new LinkedBlockingQueue<>();
	}

}