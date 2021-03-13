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
public class AtomicReferenceDemo {
	private static AtomicReference<UserInfo> userInfoRef;

	private static AtomicInteger counter = new AtomicInteger(0);

	public static void main(String[] args) throws InterruptedException {
		final UserInfo userInfo = new UserInfo("Mark", 0);
		AtomicReferenceDemo.userInfoRef = new AtomicReference<UserInfo>(userInfo);
		System.err.println("user修改之前 age=0  " + AtomicReferenceDemo.userInfoRef.get());
		final List<Thread> threads = new LinkedList<>();
		for (int i = 1; i <= 10; i++) {
			final Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					UserInfo oldUser = AtomicReferenceDemo.userInfoRef.get();
					final UserInfo updateUser = new UserInfo("Bill",
							AtomicReferenceDemo.counter.incrementAndGet());
					// 通过比较对象oldUser和userInfoRef存储对象地址是否相同，来更新atomicUserRef的引用值
					// 自旋操作
					for (;;) {
						// 多个线程并发修改userInfoRef中的引用，只有一个能修改成功，修改成功即出自旋；否则，休眠10ms，并继续竞争cas
						if (AtomicReferenceDemo.userInfoRef.compareAndSet(oldUser, updateUser)) {
							System.out.println("当前线程: " + Thread.currentThread().getName()
									+ " ,user修改之前:" + oldUser + ",修改之后:" + updateUser);
							break;
						} else {
							try {
								TimeUnit.MILLISECONDS.sleep(10);
							} catch (final InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							oldUser = AtomicReferenceDemo.userInfoRef.get();
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
		System.err.println("compareAndSet之后的结果" + AtomicReferenceDemo.userInfoRef.get());
	}

	private static class UserInfo {
		private final String name;
		private int age;

		public UserInfo(String name, int age) {
			this.name = name;
			this.age = age;
		}

		public String getName() {
			return this.name;
		}

		public int getAge() {
			return this.age;
		}

		public void setAge(int age) {
			this.age = age;
		}

		@Override
		public String toString() {
			return "UserInfo{" + "addr='" + System.identityHashCode(this) + "'name='" + this.name
					+ '\''
					+ ", age=" + this.age + '}';
		}
	}

}