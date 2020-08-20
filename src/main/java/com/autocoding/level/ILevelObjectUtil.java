package com.autocoding.level;

import java.io.Serializable;
import java.util.Stack;
import java.util.function.Function;

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
	public static ILevelObject setLevel(ILevelObject levelObject,
			Function<Serializable, ILevelObject> getTargetByIdFunction) {
		Stack<ILevelObject> stack = new Stack<ILevelObject>();
		initStack(levelObject, stack, getTargetByIdFunction);
		int index = stack.search(levelObject);
		if (index > -1) {
			levelObject.setLevel(index);
		}
		levelObject.setStack(stack);
		return levelObject;
	}

	private static void initStack(ILevelObject levelObject, Stack<ILevelObject> stack,
			Function<Serializable, ILevelObject> getTargetByIdFunction) {
		stack.push(levelObject);
		if (null == levelObject.getPId()) {
			return;
		}
		levelObject = getTargetByIdFunction.apply(levelObject.getPId());
		initStack(levelObject, stack, getTargetByIdFunction);
	}

	public static void main(String[] args) {
		DemoLevelObject levelObject1 = DemoLevelObject.map.get(1);
		DemoLevelObject levelObject11 = DemoLevelObject.map.get(11);
		DemoLevelObject levelObject111 = DemoLevelObject.map.get(111);
		Function<Serializable, ILevelObject> getTargetByIdFunction = new Function<Serializable, ILevelObject>() {
			@Override
			public ILevelObject apply(Serializable id) {
				return DemoLevelObject.map.get(id);
			}

		};
		ILevelObjectUtil.setLevel(levelObject1, getTargetByIdFunction);
		ILevelObjectUtil.setLevel(levelObject11, getTargetByIdFunction);
		ILevelObjectUtil.setLevel(levelObject111, getTargetByIdFunction);
		System.err.println("DemoLevelObject:id=1" + levelObject1);
		System.err.println("DemoLevelObject:id=11" + levelObject11);
		System.err.println("DemoLevelObject:id=111" + levelObject111);
	}
}
