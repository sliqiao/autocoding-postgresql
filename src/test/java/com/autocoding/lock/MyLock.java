package com.autocoding.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

class MyLock implements Lock {

	private final Sync sync = new Sync();

	@Override
	public void lock() {
		this.sync.acquire(1);

	}

	@Override
	public void lockInterruptibly() throws InterruptedException {
		this.sync.acquireInterruptibly(1);

	}

	@Override
	public boolean tryLock() {

		return this.sync.tryAcquire(1);
	}

	@Override
	public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
		return this.sync.tryAcquireNanos(1, TimeUnit.NANOSECONDS.convert(time, unit));
	}

	@Override
	public void unlock() {
		this.sync.release(1);

	}

	@Override
	public Condition newCondition() {
		return this.sync.newCondition();
	}

	private static class Sync extends AbstractQueuedSynchronizer {

		// 等待状态
		private static int wating = 1;
		// 结束等待状态
		private static int done = 2;

		public Sync() {
			this.setState(Sync.done);
		}

		@Override
		protected boolean tryAcquire(int permit) {
			if (this.getState() == Sync.done) {
				if (this.compareAndSetState(Sync.done, Sync.wating)) {
					return true;
				}
			}
			return false;

		}

		@Override
		protected boolean tryRelease(int permits) {
			if (this.getState() == Sync.wating) {
				if (this.compareAndSetState(Sync.wating, Sync.done)) {
					return true;
				}
			}
			return false;
		}

		final ConditionObject newCondition() {
			return new ConditionObject();
		}
	}
}
