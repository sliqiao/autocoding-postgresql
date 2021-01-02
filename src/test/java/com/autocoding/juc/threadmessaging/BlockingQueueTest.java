package com.autocoding.juc.threadmessaging;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class BlockingQueueTest {

	public static void main(String[] args) throws InterruptedException {
		final BlockingQueue<String> blockingQueue = new LinkedBlockingQueue<String>();
		final Producer producer = new Producer(blockingQueue);
		final Consumer consumer = new Consumer(blockingQueue);
		for (int i = 0; i < 2; i++) {
			// 生产者线程
			new Thread(producer).start();
			// 消费者线程
			new Thread(consumer).start();
		}

		TimeUnit.SECONDS.sleep(10);
	}

	private static class Producer implements Runnable {

		private final BlockingQueue<String> blockingQueue;

		public Producer(BlockingQueue<String> blockingQueue) {
			this.blockingQueue = blockingQueue;
		}

		@Override
		public void run() {
			try {
				final String temp = "生产线程" + Thread.currentThread().getName();
				blockingQueue.put(temp);
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}

		}
	}

	private static class Consumer implements Runnable {

		private final BlockingQueue<String> blockingQueue;

		public Consumer(BlockingQueue<String> blockingQueue) {
			this.blockingQueue = blockingQueue;
		}

		@Override
		public void run() {
			try {
				// 取出阻塞队列中的消息
				final String temp = blockingQueue.take();
				// 打印结果消息
				System.out.println("消费线程" + Thread.currentThread().getName() + " 消费 " + temp);
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}

	}
}
