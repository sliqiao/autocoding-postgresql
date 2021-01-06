package com.autocoding.juc.threadmessaging;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ConditionTest {
	private static final ReentrantLock BUFFER_LOCK = new ReentrantLock();
	private static final Condition BUFFER_CONDITION = ConditionTest.BUFFER_LOCK.newCondition();

	public static void main(String args[]) {
		final Queue<Integer> buffer = new LinkedList<>();
		final int maxSize = 10;

		final Thread producer = new Producer(buffer, maxSize, "PRODUCER");
		final Thread consumer = new Consumer(buffer, maxSize, "CONSUMER");
		producer.start();
		consumer.start();
	}

	private static class Producer extends Thread {
		private final Queue<Integer> queue;
		private final int maxSize;

		public Producer(Queue<Integer> queue, int maxSize, String name) {
			super(name);
			this.queue = queue;
			this.maxSize = maxSize;
		}

		@Override
		public void run() {
			while (true) {
				ConditionTest.BUFFER_LOCK.lock();
				try {
					while (queue.size() == maxSize) {
						try {
							System.out.println("Queue is full, " + "Producer thread waiting for "
									+ "consumer to take something from queue");
							ConditionTest.BUFFER_CONDITION.await();
						} catch (final Exception ex) {
							ex.printStackTrace();
						}
					}
					final Random random = new Random();
					final int i = random.nextInt();
					System.out.println("Producing value : " + i);
					queue.add(i);
					ConditionTest.BUFFER_CONDITION.signalAll();
				} finally {
					ConditionTest.BUFFER_LOCK.unlock();
				}

			}
		}

	}

	private static class Consumer extends Thread {
		final private Queue<Integer> queue;
		final private int maxSize;

		public Consumer(Queue<Integer> queue, int maxSize, String name) {
			super(name);
			this.queue = queue;
			this.maxSize = maxSize;
		}

		@Override
		public void run() {
			while (true) {
				ConditionTest.BUFFER_LOCK.lock();
				try {
					while (queue.isEmpty()) {
						try {
							System.out.println("Queue is empty," + "Consumer thread is waiting"
									+ " for producer thread to put something in queue");
							ConditionTest.BUFFER_CONDITION.await();
						} catch (final Exception ex) {
							ex.printStackTrace();
						}
					}
					System.out.println("Consuming value : " + queue.remove());
					ConditionTest.BUFFER_CONDITION.signalAll();
				} finally {
					ConditionTest.BUFFER_LOCK.unlock();
				}
			}

		}
	}
}
