package com.autocoding.threadpool.demo;

public class AsynchronousTask implements Runnable {

	public AsynchronousTask(int i, String string, int j) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		System.out.println("AsynchronousTask执行");
		try {
			Thread.sleep(30000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
