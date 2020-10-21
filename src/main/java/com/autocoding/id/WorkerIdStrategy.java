package com.autocoding.id;

/**
 * workId生成策略
 * 
 * @ClassName: WorkerIdStrategy
 * @author: QiaoLi
 * @date: Oct 21, 2020 11:11:59 AM
 */
public interface WorkerIdStrategy {
	/**
	 * 
	 * 返回数据中心Id,范围为0-31
	 * 
	 * @return long
	 */
	public long getDataCenterId();

	/**
	 * 
	 * 机器Id ,范围为0-31
	 * 
	 * @return long
	 */
	public long getMachineId();

}