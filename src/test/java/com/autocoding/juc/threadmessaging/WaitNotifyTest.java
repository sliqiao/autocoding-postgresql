package com.autocoding.juc.threadmessaging;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

/**
 * Simple Java program to demonstrate How to use wait, notify and notifyAll()
 * method in Java by solving producer consumer problem.
 *
 * @author Javin Paul
 */
public class WaitNotifyTest {
	public static void main(String args[]) {
		System.out.println("How to use wait and notify method in Java");
		System.out.println("Solving Producer Consumper Problem");
		final Queue<Integer> buffer = new LinkedList<>();
		final int maxSize = 10;
		final Thread producer = new Producer(buffer, maxSize, "PRODUCER");
		final Thread consumer = new Consumer(buffer, maxSize, "CONSUMER");
		producer.start();
		consumer.start();
	}
}

/**
 * Producer Thread will keep producing values for Consumer to consumer. It will
 * use wait() method when Queue is full and use notify() method to send
 * notification to Consumer Thread.
 *
 * @author WINDOWS 8
 *
 */
class Producer extends Thread {
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
			synchronized (queue) {
				while (queue.size() == maxSize) {
					try {
						System.out.println("Queue is full, " + "Producer thread waiting for "
								+ "consumer to take something from queue");
						queue.wait();
					} catch (final Exception ex) {
						ex.printStackTrace();
					}
				}
				final Random random = new Random();
				final int i = random.nextInt();
				System.out.println("Producing value : " + i);
				queue.add(i);
				queue.notifyAll();
			}
		}
	}
}

/**
 * Consumer Thread will consumer values form shared queue. It will also use
 * wait() method to wait if queue is empty. It will also use notify method to
 * send notification to producer thread after consuming values from queue.
 *
 * @author WINDOWS 8
 *
 */
class Consumer extends Thread {
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
			synchronized (queue) {
				while (queue.isEmpty()) {
					System.out.println("Queue is empty," + "Consumer thread is waiting"
							+ " for producer thread to put something in queue");
					try {
						queue.wait();
					} catch (final Exception ex) {
						ex.printStackTrace();
					}
				}
				System.out.println("Consuming value : " + queue.remove());
				queue.notifyAll();
			}
		}
	}
}