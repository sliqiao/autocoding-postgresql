package com.autocoding.juc.futuretask;

import java.lang.reflect.Field;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;
import sun.misc.Unsafe;

/**
 * FutureTaskTest
 * @author Administrator
 *
 */
@Slf4j
public class FutureTaskTest {
	private final Object object = new Object();
	private static long valueoffsetOfObject;

	static {
		final Unsafe Unsafe = FutureTaskTest.getUnsafeV1();
		try {
			FutureTaskTest.valueoffsetOfObject = Unsafe
					.objectFieldOffset(FutureTaskTest.class.getDeclaredField("object"));
		} catch (final NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		;
	}

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		final FutureTask<String> futureTask = new FutureTask<>(new MyCallable());
		//new Thread()创建的线程的daemon属性是根据父线程来的，main线程是一个前台线程，所以创建的子线程也是daemon线程
		//而JVM只有所以的前台线程退出，JVM才会退出
		new Thread(futureTask).start();
		final String returnValue = futureTask.get();
		FutureTaskTest.log.info("获取任务结果:{}", returnValue);
		System.err.println("valueoffsetOfObject:" + FutureTaskTest.valueoffsetOfObject);
	}

	private static class MyCallable implements Callable<String> {

		@Override
		public String call() throws Exception {
			FutureTaskTest.log.info("任务开始执行");
			TimeUnit.SECONDS.sleep(5);
			FutureTaskTest.log.info("任务结束执行");
			return "hello world";
		}

	}

	private static Unsafe getUnsafeV1() {
		Field f;
		try {
			f = Unsafe.class.getDeclaredField("theUnsafe");
			f.setAccessible(true);
			return (Unsafe) f.get(null);
		} catch (final Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 会抛异常：Caused by: java.lang.SecurityException: Unsafe
	 * <p>Unsafe只允许由Bootstrap类加载器加载的类来使用</p>
	 * @return
	 */
	private static Unsafe getUnsafeV2() {
		final Unsafe unsafe = sun.misc.Unsafe.getUnsafe();
		return unsafe;
	}
}