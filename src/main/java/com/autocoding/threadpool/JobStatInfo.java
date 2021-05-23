package com.autocoding.threadpool;

import java.util.Date;
import java.util.Objects;

import lombok.Data;

/**
 * Job执行统计信息
 *
 * @ClassName: JobStatInfo
 * @author: QiaoLi
 * @date: Oct 13, 2020 5:22:21 PM
 */
@Data
class JobStatInfo {

	/**
	 * job Id
	 */
	private String jobId;

	/**
	 * numOfTaskInJob
	 */
	private Integer numOfTaskInJob;

	/**
	 * 任务描述
	 */
	private String desc;

	/**
	 * Job开始执行时间
	 */
	private Date startTime;
	/**
	 * Job结束执行时间
	 */
	private Date endTime;

	public static JobStatInfo newInstance(String jobId, Integer numOfTaskInJob) {
		final JobStatInfo jobStatInfo = new JobStatInfo();
		jobStatInfo.setJobId(jobId);
		jobStatInfo.setNumOfTaskInJob(numOfTaskInJob);
		jobStatInfo.setStartTime(new Date());
		return jobStatInfo;
	}

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
		final JobStatInfo that = (JobStatInfo) object;
		return java.util.Objects.equals(this.jobId, that.jobId);

	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), this.jobId);
	}
}
