package com.autocoding.threadpool;

import java.util.Date;
import java.util.Objects;

import lombok.Data;

/**
 * 任务执行统计信息
 *
 * @ClassName: TaskStatInfo
 * @author: QiaoLi
 * @date: Oct 13, 2020 5:22:21 PM
 */
@Data
public class TaskStatInfo {
	/**
	 * 任务Id
	 */
	private String id;
	/**
	 * 原Runnable/Callable任务的hashCode
	 */
	private int oriHashCode;
	/**
	 * 任务描述
	 */
	private String desc;
	/**
	 * 任务提交时间
	 */
	private Date submittedTime;
	/**
	 * 任务开始执行时间
	 */
	private Date startTime;
	/**
	 * 任务结束执行时间
	 */
	private Date endTime;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		if (object == null || this.getClass() != object.getClass()) {
			return false;
		}
		if (!super.equals(object)) {
			return false;
		}
		final TaskStatInfo that = (TaskStatInfo) object;
		return java.util.Objects.equals(this.id, that.id);

	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), this.id);
	}
}
