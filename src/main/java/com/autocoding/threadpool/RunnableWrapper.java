package com.autocoding.threadpool;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.alibaba.fastjson.JSON;

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
class RunnableWrapper implements Runnable, Comparable<RunnableWrapper> {
	private static int DEFAULT_PRIORITY = 0;
	private static String PREFIX = "r-";
	private static AtomicInteger COUNTER = new AtomicInteger(1);
	private Runnable runnable;
	private String id;
	private String name;
	private String desc;
	private Integer priority = RunnableWrapper.DEFAULT_PRIORITY;
	private Map<String, Object> ext;

	public static RunnableWrapper newInstance(Runnable runnable) {
		final String id = RunnableWrapper.PREFIX
				+ String.valueOf(RunnableWrapper.COUNTER.getAndIncrement());
		return new RunnableWrapper(runnable, null, id);
	}

	public static RunnableWrapper newInstance(String id, Runnable runnable) {
		return new RunnableWrapper(runnable, null, id);
	}

	public static RunnableWrapper newInstance(String name, String id, Runnable runnable) {
		return new RunnableWrapper(runnable, name, id);
	}

	private RunnableWrapper(Runnable runnable, String name, String id) {
		if (null == runnable) {
			throw new NullPointerException("runnable不能为Null");
		}
		if (null == id) {
			throw new NullPointerException("id不能为Null");
		}
		this.runnable = runnable;
		this.id = id;

	}


	@Override
	public void run() {
		try {
			//这里可以使用从主调线程传过来的ext
			if (this.ext != null && this.ext.size() > 0) {
				RunnableWrapper.log.info("ext:{}", JSON.toJSONString(this.ext));
			}
			this.runnable.run();
		} catch (final Exception e) {
			RunnableWrapper.log
			.error(String.format("taskName:%s ,taskId:%s,执行异常 ", this.name, this.id), e);
			throw e;
		}

	}

	/**
	 * 1、优先级 priority 高的任务，放到队列的前面，先出队列
	 * 2、如果有任务队列级未设置，则为相等
	 */
	@Override
	public int compareTo(RunnableWrapper o) {
		if (null != this.getPriority() && null != o.getPriority()) {
			return -1 * this.getPriority().compareTo(o.getPriority());
		}
		return 0;
	}

	@Override
	public String toString() {
		return "RunnableWrapper [id=" + this.id + ", name=" + this.name + "]";
	}

}
