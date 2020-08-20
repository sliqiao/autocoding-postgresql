package com.autocoding.level;

import java.io.Serializable;
import java.util.Stack;

/**
 * 对层级对象的抽象
 * 
 * @ClassName: ILevelObject
 * @author: QiaoLi
 * @date: Aug 20, 2020 4:29:45 PM
 */
public interface ILevelObject<T> {
	/**
	 * 获取Id
	 */
	public Serializable getId();

	/**
	 * 获取PId
	 */
	public Serializable getPId();

	/**
	 * 获取层级
	 */
	public Integer getLevel();

	/**
	 * 设置层级
	 */
	public void setLevel(Integer level);

	/**
	 * 设置层级对象栈
	 */
	public void setStack(Stack<T> stack);

	/**
	 * 获取对象栈
	 */
	public Stack<T> getStack();

}
