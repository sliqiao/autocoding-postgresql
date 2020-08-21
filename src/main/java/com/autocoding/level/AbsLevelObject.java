package com.autocoding.level;

import java.io.Serializable;
import java.util.Stack;

/**
 * ILevelObject的抽象实现
 * 
 * @ClassName: DemoLevelObject
 * @Description: 用一句话描述该文件做什么
 * @author: QiaoLi
 * @date: Aug 20, 2020 4:31:59 PM
 */
public class AbsLevelObject<T> implements ILevelObject<T> {

	private Serializable id;
	private Serializable pid;
	private int level;
	private Stack<T> stack;

	public AbsLevelObject(Serializable id, Serializable pid) {
		this.id = id;
		this.pid = pid;
	}

	@Override
	public Serializable getId() {
		return this.id;
	}

	@Override
	public Serializable getPId() {

		return this.pid;
	}

	@Override
	public Integer getLevel() {
		return this.level;
	}

	@Override
	public void setLevel(Integer level) {
		this.level = level;

	}

	@Override
	public void setStack(Stack<T> stack) {
		this.stack = stack;

	}

	@Override
	public Stack<T> getStack() {
		return this.stack;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbsLevelObject<T> other = (AbsLevelObject<T>) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "AbstractLevelObject [id=" + id + ", pid=" + pid + ", level=" + level + ", stack="
				+ (stack == null ? 0 : stack.size()) + "]";
	}

}
