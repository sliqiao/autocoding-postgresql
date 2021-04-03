package com.autocoding.object.waitnotify;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WaitNotifyTest {

	private static class Buffer<T> {
		private static final int MAX_CAPACITY = 10;
		private final List<T> list;

		public Buffer() {
			this.list = new ArrayList<T>();
		}

		public T put(T t) {
			synchronized (this) {
				while (this.list.size() >= Buffer.MAX_CAPACITY) {
					try {
						this.wait();
					} catch (final InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				this.list.add(t);
				this.notifyAll();
				return t;

			}

		}

		public T take() {
			T t = null;
			synchronized (this) {
				while (this.list.size() == 0) {
					try {
						this.wait();
					} catch (final InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				t = this.list.remove(0);
				this.notifyAll();
			}
			return t;

		}
	}

	public static void main(String[] args) throws InterruptedException {
		final Buffer<String> buffer = new Buffer<String>();
		final Producer<String> producer1 = new Producer<String>(buffer);
		final Producer<String> producer2 = new Producer<String>(buffer);
		final Producer<String> producer3 = new Producer<String>(buffer);
		producer1.start();
		producer2.start();
		producer3.start();
		final Consumer<String> consumer = new Consumer<String>(buffer);
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
			for (int i = 1; i <= 1000; i++) {
				final T t = this.buffer.put((T) UUID.randomUUID().toString());
				WaitNotifyTest.log.info("生产：{}", t);
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
			for (int i = 1; i <= 1000; i++) {
				final T t = this.buffer.take();
				WaitNotifyTest.log.error("消费：{}", t);
			}

		}
	}

}
