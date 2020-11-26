package com.autocoding.designpattern.singleton;

/**
 *  懒汉模式单例(Double Check)
 * @ClassName:  DoubleCheckInLazyMode   
 * @Description:  用一句话描述该文件做什么  
 * @author: QiaoLi
 * @date:   Nov 26, 2020 8:45:38 AM
 */
public class DoubleCheckSingletonInLazyMode {
	// volatile修饰instance防止指令重排序
	private static volatile DoubleCheckSingletonInLazyMode instance;

	private DoubleCheckSingletonInLazyMode() {
	}

	public DoubleCheckSingletonInLazyMode getInstance() {
		if (null == instance) {
			// null的Double Check保证多线程安全，可能会有多个线程调用getInstance()方法
			synchronized (instance) {
				if (null == instance) {
					// 因为这里可能有指令重排序的问题，所以需要instance完成初始化之后才能返回，避免客户端获取一个未初始化的instance
					instance = new DoubleCheckSingletonInLazyMode();
				}

			}
		}
		return instance;
	}
}
