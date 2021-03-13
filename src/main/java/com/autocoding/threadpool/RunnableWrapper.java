package com.autocoding.threadpool;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * Runnable装饰器模式，提供任务标识符:id
 *
 * @ClassName: RunnableWrapper
 * @author: QiaoLi
 * @date: Oct 28, 2020 3:13:16 PM
 */
@Slf4j
@Data
public class RunnableWrapper implements Callable<Void> {
	private static String PREFIX = "r-";
	private static AtomicInteger COUNTER = new AtomicInteger(1);
	private Runnable runnable;
	private String id;
	private String desc;

	public static RunnableWrapper newInstance(Runnable runnable) {
		final String id = RunnableWrapper.PREFIX
				+ String.valueOf(RunnableWrapper.COUNTER.getAndIncrement());
		return new RunnableWrapper(runnable, id);
	}

	public static RunnableWrapper newInstance(String id, Runnable runnable) {
		return new RunnableWrapper(runnable, id);
	}

	private RunnableWrapper(Runnable runnable, String id) {
		if (null == runnable) {
			throw new NullPointerException("runnable不能为Null");
		}
		if (null == id) {
			throw new NullPointerException("id不能为Null");
		}
		this.runnable = runnable;
		this.id = id;

	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDesc() {
		return this.desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	@Override
	public Void call() throws Exception {
		this.runnable.run();
		return null;
	}



}
