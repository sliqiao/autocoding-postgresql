package com.autocoding.level;

import java.io.Serializable;
import java.util.Stack;
import java.util.function.Function;

/**
 * 层级对象工具类，用来设置level与stack
 * 
 * @ClassName: ILevelObjectUtil
 * @author: QiaoLi
 * @date: Aug 20, 2020 5:28:09 PM
 */
public final class ILevelObjectUtil {

	private ILevelObjectUtil() {
	}

	/**
	 * 
	 * 设置ILevelObject的层级
	 * 
	 * @param levelObject
	 * @param getTargetByIdFunction
	 *            根据Id获取ILevelObject的业务逻辑函数
	 * @return ILevelObject
	 */
	public static void setLevel(ILevelObject levelObject,
			Function<Serializable, ? extends ILevelObject> getTargetByIdFunction) {
		Stack<ILevelObject> stack = new Stack<ILevelObject>();
		initStack(levelObject, stack, getTargetByIdFunction);
		int index = stack.search(levelObject);
		if (index > -1) {
			levelObject.setLevel(index);
		}
		levelObject.setStack(stack);

	}

	private static void initStack(ILevelObject levelObject, Stack<ILevelObject> stack,
			Function<Serializable, ? extends ILevelObject> getTargetByIdFunction) {
		stack.push(levelObject);
		if (null == levelObject.getPId()) {
			return;
		}
		ILevelObject parentLevelObject = getTargetByIdFunction.apply(levelObject.getPId());
		initStack(parentLevelObject, stack, getTargetByIdFunction);
	}

	public static void main(String[] args) {
		DemoLevelObject levelObject1 = DemoLevelObject.map.get(1);
		DemoLevelObject levelObject11 = DemoLevelObject.map.get(11);
		DemoLevelObject levelObject111 = DemoLevelObject.map.get(111);
		Function<Serializable, DemoLevelObject> getTargetByIdFunction = new Function<Serializable, DemoLevelObject>() {
			@Override
			public DemoLevelObject apply(Serializable id) {
				return DemoLevelObject.map.get(id);
			}

		};
		ILevelObjectUtil.setLevel(levelObject1, getTargetByIdFunction);
		ILevelObjectUtil.setLevel(levelObject11, getTargetByIdFunction);
		ILevelObjectUtil.setLevel(levelObject111, getTargetByIdFunction);
		System.err.println("id=1：" + levelObject1);
		System.err.println("id=11：" + levelObject11);
		System.err.println("id=111：" + levelObject111);
	}
}
