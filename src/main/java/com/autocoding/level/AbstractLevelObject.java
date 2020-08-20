package com.autocoding.level;

import java.io.Serializable;
import java.util.Stack;

import net.sf.json.JSONObject;

/**
 * ILevelObject的一个示例
 * 
 * @ClassName: DemoLevelObject
 * @Description: 用一句话描述该文件做什么
 * @author: QiaoLi
 * @date: Aug 20, 2020 4:31:59 PM
 */
public class AbstractLevelObject<T> implements ILevelObject<T> {

	private Integer id;
	private Integer pid;
	private int level;
	private Stack<T> stack;

	/**
	 * @param id
	 * @param pid
	 */
	public AbstractLevelObject(Integer id, Integer pid) {
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

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractLevelObject other = (AbstractLevelObject) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "AbstractLevelObject [id=" + id + ", pid=" + pid + ", level=" + level + ", stack=" +(stack==null?0:stack.size()) + "]";
	}

}
