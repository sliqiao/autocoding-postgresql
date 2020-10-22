package com.autocoding.snowflake;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.commons.lang3.time.DateFormatUtils;

import com.autocoding.snowflake.SnowFlakeId.Builder;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * id格式：固定0(1位)+时间戳(41)+数据中心(5位)+机器(5位)+序列号(12位)
 * 
 * @ClassName: SnowFlake
 * @Description: 用一句话描述该文件做什么
 * @author: QiaoLi
 * @date: Oct 21, 2020 10:54:58 AM
 */
@Slf4j
public class SnowFlakeUtil {

	/** 起始的时间戳 */
	public static long START_TIMESTAMP = 0L;
	static {
		try {
			START_TIMESTAMP = new SimpleDateFormat("yyyy-MM-dd").parse("1988-06-04").getTime();
		} catch (ParseException e) {
			log.error("ParseException:", e);
		}
	}
	/** 数据中心占用的位数 */
	public final static long DATACENTER_BIT = 5;
	/** 机器标识占用的位数 */
	public final static long MACHINE_BIT = 5;
	/** 序列号占用的位数 */
	public final static long SEQUENCE_BIT = 12;

	/** 数据中心 最大值 */
	public final static long MAX_DATACENTER_NUM = -1L ^ (-1L << DATACENTER_BIT);
	/** 机器标识 最大值 */
	public final static long MAX_MACHINE_NUM = -1L ^ (-1L << MACHINE_BIT);
	/** 序列号最大值 */
	public final static long MAX_SEQUENCE = -1L ^ (-1L << SEQUENCE_BIT);

	/** 数据中心向左的位移数 */
	public final static long DATA_CENTER_SHIFT = SEQUENCE_BIT + MACHINE_BIT;
	/** 机器标识向左的位移数 */
	public final static long MACHINE_SHIFT = SEQUENCE_BIT;
	/** 时间戳的位移数 */
	public final static long TIMESTMP_SHIFT = DATA_CENTER_SHIFT + DATACENTER_BIT;
	/** 序列号 */
	private long sequence = 0L;
	/** 上一次时间戳 */
	private long lastTimeStamp = -1L;
	/** WorkerIdStrategy */
	private WorkerIdStrategy workerIdStrategy = null;

	private SnowFlakeUtil(WorkerIdStrategy workerIdStrategy) {
		if (null == workerIdStrategy) {
			throw new IllegalArgumentException("workerIdStrategy can not be null");
		}
		long datacenterId = workerIdStrategy.getDataCenterId();
		long machineId = workerIdStrategy.getMachineId();
		if (datacenterId > MAX_DATACENTER_NUM || datacenterId < 0) {
			throw new IllegalArgumentException("datacenterId can't be greater than MAX_DATACENTER_NUM or less than 0");
		}
		if (machineId > MAX_MACHINE_NUM || machineId < 0) {
			throw new IllegalArgumentException("machineId can't be greater than MAX_MACHINE_NUM or less than 0");
		}

		this.workerIdStrategy = workerIdStrategy;
	}

	public static SnowFlakeUtil getInstance(WorkerIdStrategy workerIdStrategy) {

		long datacenterId = workerIdStrategy.getDataCenterId();
		long machineId = workerIdStrategy.getMachineId();
		if (datacenterId > MAX_DATACENTER_NUM || datacenterId < 0) {
			throw new IllegalArgumentException("datacenterId can't be greater than MAX_DATACENTER_NUM or less than 0");
		}
		if (machineId > MAX_MACHINE_NUM || machineId < 0) {
			throw new IllegalArgumentException("machineId can't be greater than MAX_MACHINE_NUM or less than 0");
		}
		return new SnowFlakeUtil(workerIdStrategy);
	}

	public static SnowFlakeUtil getInstance() {
		WorkerIdStrategy workerIdStrategy = DefaultWorkerIdStrategy.getInstance();
		return SnowFlakeUtil.getInstance(workerIdStrategy);
	}

	public synchronized SnowFlakeId next() {
		long currentTimeStamp = getCurrentTimeStamp();
		if (currentTimeStamp < lastTimeStamp) {
			throw new RuntimeException("Clock moved backwards.  Refusing to generate id");
		}

		if (currentTimeStamp == lastTimeStamp) {
			sequence = (sequence + 1) & MAX_SEQUENCE;
			if (sequence == 0L) {
				currentTimeStamp = getNextMilliSencodsTimeStamp();
			}
		} else {
			sequence = 0L;
		}
		lastTimeStamp = currentTimeStamp;
		Builder builder = SnowFlakeId.Builder.builder();
		builder.originalDataCenterId(this.workerIdStrategy.getDataCenterId())
				.originalMachineId(this.workerIdStrategy.getMachineId()).originalTimeStamp(currentTimeStamp)
				.originalSequence(sequence);
		return builder.build();
	}

	private long getNextMilliSencodsTimeStamp() {
		long timeStamp = getCurrentTimeStamp();
		while (timeStamp <= lastTimeStamp) {
			timeStamp = getCurrentTimeStamp();
		}
		return timeStamp;
	}

	private long getCurrentTimeStamp() {
		return System.currentTimeMillis();
	}

}