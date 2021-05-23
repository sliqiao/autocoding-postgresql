package com.autocoding.threadpool;

/**
 * JobContext Job上下文
 *
 * @ClassName: TaskContext
 * @author: QiaoLi
 * @date: Oct 13, 2020 5:10:48 PM
 */
public class JobContext {
	private static ThreadLocal<String> JOB_ID = new ThreadLocal<String>() {

		@Override
		protected String initialValue() {
			return null;
		}

	};
	private static ThreadLocal<String> JOB_NAME = new ThreadLocal<String>() {

		@Override
		protected String initialValue() {
			return null;
		}

	};
	private static ThreadLocal<Integer> NUM_OF_TASKS = new ThreadLocal<Integer>() {

		@Override
		protected Integer initialValue() {
			return 0;
		}

	};
	public static void setId(String id) {
		JobContext.JOB_ID.set(id);
	}

	public static String getId() {
		return JobContext.JOB_ID.get();
	}



	public static void setName(String name) {
		JobContext.JOB_NAME.set(name);
	}

	public static String getName() {
		return JobContext.JOB_NAME.get();
	}

	public static void setNumOfTasks(Integer numOfTasks) {
		JobContext.NUM_OF_TASKS.set(numOfTasks);
	}

	public static Integer getNumOfTasks() {
		return JobContext.NUM_OF_TASKS.get();
	}
	public static void removeId() {
		JobContext.JOB_ID.remove();

	}
	public static void removeName() {
		JobContext.JOB_NAME.remove();
	}

	public static void removeIdAndName() {
		JobContext.removeId();
		JobContext.removeName();
	}

	public static void beginRegisteringJob(String id, Integer numOfTasks) {
		JobContext.setId(id);
		JobContext.NUM_OF_TASKS.set(numOfTasks);
	}

	public static void endRegisteringJob(String id) {
		JobContext.removeIdAndName();

	}
}
