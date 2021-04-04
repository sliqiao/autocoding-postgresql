package com.autocoding.threadpool;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

class DeleteTaskStateDelayedModel<T> implements Delayed {

	private final long expiredTimeStamp;
	private final T data;

	public DeleteTaskStateDelayedModel(T data, int delayed, TimeUnit timeUnit) {
		this.expiredTimeStamp = TimeUnit.MILLISECONDS.convert(delayed, timeUnit)
				+ System.currentTimeMillis();
		this.data = data;
	}

	public T getData() {
		return this.data;
	}

	@Override
	public int compareTo(Delayed o) {
		final long d = this.getDelay(TimeUnit.MILLISECONDS) - o.getDelay(TimeUnit.MILLISECONDS);
		if (d == 0) {
			return 0;
		} else {
			if (d < 0) {
				return -1;
			} else {
				return 1;
			}
		}
	}

	@Override
	public long getDelay(TimeUnit unit) {
		final long delay = unit.convert(this.expiredTimeStamp - System.currentTimeMillis(), unit);
		return delay;
	}

}
