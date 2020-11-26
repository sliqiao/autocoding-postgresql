package com.autocoding.designpattern.singleton;

/**
 * 饥饿模式单例
 * @ClassName:  SingletonInStarvationMode   
 * @author: QiaoLi
 * @date:   Nov 26, 2020 8:42:38 AM
 */
public class SingletonInStarvationMode {
	private static final SingletonInStarvationMode instance = new SingletonInStarvationMode();

	private SingletonInStarvationMode() {
	}

	public SingletonInStarvationMode getInstance() {
		return instance;
	}
}
