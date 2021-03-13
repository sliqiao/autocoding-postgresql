package com.autocoding.juc.atomic;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * AtomicReference示例代码
 * 
 * @author Administrator
 *
 */
public class AtomicReferenceDemo2 {
	private static AtomicReference<Integer> integerRef;

	private static AtomicInteger counter = new AtomicInteger(0);

	public static void main(String[] args) throws InterruptedException {
		final Integer integer = new Integer(0);
		AtomicReferenceDemo2.integerRef = new AtomicReference<Integer>(integer);

		final List<Thread> threads = new LinkedList<>();
		for (int i = 0; i <= 5; i++) {
			final Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					// 请注意old涉及到装箱操作，所以CAS永远不会成功
					int old = AtomicReferenceDemo2.integerRef.get();

					final int newInt = AtomicReferenceDemo2.counter.incrementAndGet();
					// 通过比较对象old和newInt存储对象地址是否相同，来更新atomicUserRef的引用值
					// 自旋操作
					for (;;) {
						// 多个线程并发修改integerRef中的引用，只有一个能修改成功，修改成功即退出自旋；否则，休眠10ms，并继续竞争cas
						if (AtomicReferenceDemo2.integerRef.compareAndSet(old, newInt)) {
							System.out.println("当前线程: " + Thread.currentThread().getName()
									+ " ,user修改之前:" + old + ",修改之后:" + newInt);
							break;
						} else {
							try {
								TimeUnit.MILLISECONDS.sleep(100);
							} catch (final InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							old = AtomicReferenceDemo2.integerRef.get();
						}
					}

				}
			}, "thread-" + i);
			threads.add(t);
			t.start();
		}

		for (final Thread thread : threads) {
			thread.join();
		}
		System.err.println("最终：integerRef：" + AtomicReferenceDemo2.integerRef.get());

	}

}