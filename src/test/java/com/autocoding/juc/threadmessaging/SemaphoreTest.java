package com.autocoding.juc.threadmessaging;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Semaphore;

/**
 * 利用JDK中信号量来将SET修饰为一个BlockingSet
 * @ClassName:  SemaphoreTest   
 * @author: QiaoLi
 * @date:   Jan 6, 2021 11:10:14 AM
 */
public class SemaphoreTest {

	public static void main(String[] args) throws InterruptedException {
		BlockingSet<Integer> blockingSet = new BlockingSet<>(new HashSet<Integer>(), 5);
		for (int i = 1; i <= 10; i++) {
			blockingSet.add(i);
		}
		System.err.println("blockingSet:" + blockingSet);
	}

	private static class BlockingSet<T> implements Set<T> {
		private Set<T> set = new HashSet<T>();
		private final Semaphore semaphore;

		public BlockingSet(Set<? extends T> set, Integer capacity) {
			if (null == set) {
				throw new IllegalArgumentException("set is null");
			}
			if (null == capacity) {
				throw new IllegalArgumentException("capacity is null");
			}
			this.set.addAll(set);
			this.semaphore = new Semaphore(capacity);
		}

		@Override
		public boolean add(Object e) {
			System.out.println("开始存储元素：" + e);
			try {
				semaphore.acquire();
			} catch (InterruptedException e1) {
				System.err.println(e1.getMessage());
			}
			boolean addFlag = false;
			try {
				addFlag = set.add((T) e);
				return addFlag;
			} finally {
				if (!addFlag) {
					semaphore.release();

				}
				System.err.println("结束存储元素：" + e);
			}

		}

		@Override
		public boolean remove(Object o) {
			System.out.println("开始删除元素：" + o);
			boolean removedFlag = set.remove(o);
			if (removedFlag) {
				semaphore.release();

			}
			System.out.println("结束删除元素：" + o);
			return removedFlag;
		}

		@Override
		public boolean addAll(Collection c) {
			boolean modified = false;
			for (Object e : c) {
				if (add(e)) {
					modified = true;
				}
			}
			return modified;
		}

		@Override
		public boolean retainAll(Collection c) {
			// 暂时不需要实现
			return false;
		}

		@Override
		public boolean removeAll(Collection c) {
			// 暂时不需要实现
			return false;
		}

		@Override
		public void clear() {
			// 暂时不需要实现

		}

		@Override
		public int size() {
			return this.set.size();
		}

		@Override
		public boolean isEmpty() {
			return this.set.isEmpty();
		}

		@Override
		public boolean contains(Object o) {
			return this.set.contains(o);
		}

		@Override
		public Iterator iterator() {
			return this.set.iterator();
		}

		@Override
		public Object[] toArray() {
			return this.set.toArray();
		}

		@Override
		public Object[] toArray(Object[] a) {
			return this.set.toArray(a);
		}

		@Override
		public boolean containsAll(Collection c) {
			return this.set.containsAll(c);
		}

	}

}
