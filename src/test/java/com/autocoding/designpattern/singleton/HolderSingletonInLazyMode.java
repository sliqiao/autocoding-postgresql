package com.autocoding.designpattern.singleton;

/**
 * 懒汉模式(Holder单例)
 * @ClassName:  HolderSingletonInLazyMode   
 * @author: QiaoLi
 * @date:   Nov 26, 2020 8:49:47 AM
 */
public class HolderSingletonInLazyMode {

	private HolderSingletonInLazyMode() {
	}

	public HolderSingletonInLazyMode getInstance() {

		return Holder.instance;
	}

	private static interface Holder

	{
        //类加载保证了线程安全，只有Holder被加载时，才会完成instance的初始化
		HolderSingletonInLazyMode instance = new HolderSingletonInLazyMode();

	}
}
