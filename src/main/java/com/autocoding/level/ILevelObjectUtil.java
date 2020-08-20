package com.autocoding.level;

import java.io.Serializable;
import java.util.Stack;
import java.util.function.Function;

public final class ILevelObjectUtil {

	public static ILevelObject setLevel(ILevelObject levelObject,
			Function<Serializable, ILevelObject> getTargetByIdFunction) {
		Stack<ILevelObject> stack = new Stack<ILevelObject>();
		initStack(levelObject, stack, getTargetByIdFunction);
		int index = stack.search(levelObject);
		if (index > -1) {
			levelObject.setLevel(index);
		}
		return levelObject;
	}

	private ILevelObjectUtil() {
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
		ILevelObject levelObject1 = DemoLevelObject.map.get(1);
		ILevelObject levelObject11 = DemoLevelObject.map.get(11);
		ILevelObject levelObject111 = DemoLevelObject.map.get(111);
		ILevelObjectUtil.setLevel(levelObject1, id -> DemoLevelObject.map.get(id));
		ILevelObjectUtil.setLevel(levelObject11, id -> DemoLevelObject.map.get(id));
		ILevelObjectUtil.setLevel(levelObject111, id -> DemoLevelObject.map.get(id));
		System.err.println("levelObject1 level:" + levelObject1.getLevel());
		System.err.println("levelObject1 level:" + levelObject11.getLevel());
		System.err.println("levelObject1 level:" + levelObject111.getLevel());
	}
}
