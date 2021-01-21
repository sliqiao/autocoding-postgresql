package com.autocoding.stack;

import java.util.Arrays;
import java.util.Random;
import java.util.Stack;

/**
 * 一道算法面试题：要求对栈Stack中获取当前栈最小值，要求算法时间复杂度o(1)
 * <p>
 * 这里使用了两个设计模式：
 * 1、静态代理设计模式:StackWrapper对Stack进行了继承式代理，控制了Stack对象push()、pop()方法的访问
 * 2、装饰者设计模式:对Stack进行了方法的增强，增加了一个getMin()方法，对Stack对象进行了装饰
 * </p>
 * @ClassName:  StackWrapper   
 * @Description:  用一句话描述该文件做什么  
 * @author: QiaoLi
 * @date:   Jan 21, 2021 11:14:26 AM
 * @param <T>
 */
public class StackWrapper<T extends Comparable<T>> extends Stack<T> {

	private static final long serialVersionUID = 1L;
	private Stack<T> minValueStack = new Stack<T>();

	public T getMin() {
		if (this.minValueStack.empty()) {
			return null;
		}
		return this.minValueStack.peek();

	}

	@Override
	public T push(T item) {
		if (this.minValueStack.empty()) {
			this.minValueStack.push(item);
		} else {
			T curMinValue = this.minValueStack.peek();
			if (curMinValue.compareTo(item) > 0) {
				this.minValueStack.push(item);
			} else {
				this.minValueStack.push(curMinValue);
			}
		}

		return super.push(item);
	}

	@Override
	public synchronized T pop() {
		this.minValueStack.pop();
		return super.pop();
	}

	public static void main(String[] args) {
		StackWrapper<Integer> stackWrapper = new StackWrapper<Integer>();
		Random random = new Random();
		for (int i = 0; i < 10; i++) {
			Integer randomValue = random.nextInt(100);
			System.out.println("入栈：" + randomValue);
			stackWrapper.push(randomValue);
		}
		for (int i = 0; i < 10; i++) {
			Integer topValue = stackWrapper.pop();
			Integer[] array = new Integer[stackWrapper.size()];
			if (!stackWrapper.empty()) {
				Arrays.sort(stackWrapper.toArray(array));
			}

			System.err.println("出栈：" + topValue + "之后" + ",栈：" + Arrays.toString(array));
			System.err.println("最小值：" + stackWrapper.getMin());
		}

	}

}
