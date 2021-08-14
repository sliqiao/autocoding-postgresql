package com.autocoding.threadpool;

import java.util.HashMap;
import java.util.Map;

/**
 * TaskContext 任务上下文
 *
 * @ClassName: TaskContext
 * @author: QiaoLi
 * @date: Oct 13, 2020 5:10:48 PM
 */
public class TaskContext {
	private static ThreadLocal<String> RUNNABLE_ID_THREAD_LOCAL = new ThreadLocal<String>() {

		@Override
		protected String initialValue() {
			return null;
		}

	};
	private static ThreadLocal<Map<String, Object>> EXT = new ThreadLocal<Map<String, Object>>() {

		@Override
		protected Map<String, Object> initialValue() {
			return new HashMap<>();
		}

	};
	private static ThreadLocal<String> RUNNABLE_NAME_THREAD_LOCAL = new ThreadLocal<String>() {

		@Override
		protected String initialValue() {
			return null;
		}

	};

	public static void setId(String id) {
		TaskContext.RUNNABLE_ID_THREAD_LOCAL.set(id);
	}

	public static String getId() {
		return TaskContext.RUNNABLE_ID_THREAD_LOCAL.get();
	}

	public static void removeId() {
		TaskContext.RUNNABLE_ID_THREAD_LOCAL.remove();

	}

	public static void setName(String name) {
		TaskContext.RUNNABLE_NAME_THREAD_LOCAL.set(name);
	}

	public static String getName() {
		return TaskContext.RUNNABLE_NAME_THREAD_LOCAL.get();
	}

	public static void removeName() {
		TaskContext.RUNNABLE_NAME_THREAD_LOCAL.remove();

	}

	public static void append2Ext(String key, Object value) {
		TaskContext.EXT.get().put(key, value);
	}

	public static Map<String, Object> getExt() {
		return TaskContext.EXT.get();
	}

	public static Map<String, Object> getExtAndRemove() {
		final Map<String, Object> ext = TaskContext.EXT.get();
		TaskContext.EXT.remove();
		return ext;
	}
	@SuppressWarnings("unchecked")
	public static <T> T getExtByKey(String key) {
		return (T) TaskContext.EXT.get().get(key);
	}

	@SuppressWarnings("unchecked")
	public static <T> T removeFromExt(String key) {
		return (T) TaskContext.EXT.get().remove(key);
	}

	@SuppressWarnings("unchecked")
	public static void removeExt() {
		TaskContext.EXT.remove();
	}
}
