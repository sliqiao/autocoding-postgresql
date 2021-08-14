package com.autocoding.object.waitnotify;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LockSupportTest {

	private static class Buffer<T> {
		private static final int MAX_CAPACITY = 10;
		private final List<T> list;
		private final List<Producer<?>> listOfProducer = new ArrayList<Producer<?>>();
		private final List<Consumer<?>> listOfConsumer = new ArrayList<Consumer<?>>();

		public Buffer() {
			this.list = new ArrayList<T>();
		}

		public T put(T t) {

			while (this.list.size() >= Buffer.MAX_CAPACITY) {
				LockSupport.park(this.hashCode());
			}
			this.list.add(t);
			final Thread randomConsumer = this.listOfConsumer
					.get(new Random().nextInt(this.listOfConsumer.size()));
			LockSupport.unpark(randomConsumer);
			return t;

		}

		public T take() {
			T t = null;

			while (this.list.size() == 0) {
				LockSupport.park(this.hashCode());
			}
			t = this.list.remove(0);
			final Thread randomProducer = this.listOfProducer
					.get(new Random().nextInt(this.listOfProducer.size()));
			LockSupport.unpark(randomProducer);

			return t;

		}

		public void addProducer(Producer<?> producer) {
			this.listOfProducer.add(producer);
			LockSupport.unpark(producer);
		}

		public void addConsumer(Consumer<?> consumer) {
			this.listOfConsumer.add(consumer);
			LockSupport.unpark(consumer);
		}
	}

	public static void main(String[] args) throws InterruptedException {
		final Buffer<String> buffer = new Buffer<String>();
		final Producer<String> producer1 = new Producer<String>(buffer);
		final Producer<String> producer2 = new Producer<String>(buffer);
		final Producer<String> producer3 = new Producer<String>(buffer);
		buffer.addProducer(producer1);
		buffer.addProducer(producer2);
		buffer.addProducer(producer3);
		producer1.start();
		producer2.start();
		producer3.start();
		final Consumer<String> consumer = new Consumer<String>(buffer);
		buffer.addConsumer(consumer);
		consumer.start();
		TimeUnit.SECONDS.sleep(1000);

	}

	private static class Producer<T> extends Thread {
		private final Buffer<T> buffer;

		public Producer(Buffer<T> buffer) {
			this.buffer = buffer;
		}

		@Override
		public void run() {
			for (int i = 1; i <= 1000000000; i++) {
				final T t = this.buffer.put((T) UUID.randomUUID().toString());
				LockSupportTest.log.info("容量：{}，生产：{}", this.buffer.list.size(), t);
			}

		}

	}

	private static class Consumer<T> extends Thread {
		private final Buffer<T> buffer;

		public Consumer(Buffer<T> buffer) {
			this.buffer = buffer;
		}

		@Override
		public void run() {
			for (int i = 1; i <= 100000000; i++) {
				final T t = this.buffer.take();
				LockSupportTest.log.error("容量：{}，消费：{}", this.buffer.list.size(), t);
			}

		}
	}

}
