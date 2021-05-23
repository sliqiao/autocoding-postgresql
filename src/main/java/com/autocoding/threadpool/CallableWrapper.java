package com.autocoding.threadpool;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.Data;

/**
 * Callable装饰器模式，提供唯一标识符:id
 * 
 * @ClassName: CallableWrapper
 * @author: QiaoLi
 * @date: Oct 28, 2020 3:15:32 PM
 * @param <V>
 */
@Deprecated
@Data
class CallableWrapper<V> implements Callable<V> {
	private static String PREFIX = "c-";
	private static AtomicInteger COUNTER = new AtomicInteger(1);
	private Callable<V> callable;
	private String id;
	private String name;
	private String desc;

	public CallableWrapper(Callable<V> callable) {

		if (null == callable) {
			throw new NullPointerException("callable不能为Null");
		}
		final String id = CallableWrapper.PREFIX
				+ String.valueOf(CallableWrapper.COUNTER.getAndIncrement());
		this.callable = callable;
		this.id = id;
	}

	public CallableWrapper(String id, Callable<V> callable) {
		if (null == callable) {
			throw new NullPointerException("callable不能为Null");
		}
		if (null == id) {
			throw new NullPointerException("id不能为Null");
		}
		this.callable = callable;
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public V call() throws Exception {
		return this.callable.call();
	}

}
