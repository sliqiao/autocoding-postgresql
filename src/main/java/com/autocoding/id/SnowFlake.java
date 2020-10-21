package com.autocoding.id;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * id格式：时间戳()+数据中心(5位)+机器(5位)+序列号(12位)
 * 
 * @ClassName: SnowFlake
 * @Description: 用一句话描述该文件做什么
 * @author: QiaoLi
 * @date: Oct 21, 2020 10:54:58 AM
 */
@Slf4j
public class SnowFlake {

	/** 起始的时间戳 */
	private static long START_TIMESTAMP = 0L;
	static {
		try {
			START_TIMESTAMP = new SimpleDateFormat("yyyy-MM-dd").parse("1988-06-04").getTime();
		} catch (ParseException e) {
			log.error("ParseException:", e);
		}
	}

	/** 数据中心占用的位数 */
	private final static long DATACENTER_BIT = 5;
	/** 机器标识占用的位数 */
	private final static long MACHINE_BIT = 5;
	/** 序列号占用的位数 */
	private final static long SEQUENCE_BIT = 12;

	/** 数据中心 最大值 */
	public final static long MAX_DATACENTER_NUM = -1L ^ (-1L << DATACENTER_BIT);
	/** 机器标识 最大值 */
	public final static long MAX_MACHINE_NUM = -1L ^ (-1L << MACHINE_BIT);
	/** 序列号最大值 */
	private final static long MAX_SEQUENCE = -1L ^ (-1L << SEQUENCE_BIT);

	/** 数据中心向左的位移数 */
	private final static long DATACENTER_LEFT = SEQUENCE_BIT + MACHINE_BIT;
	/** 机器标识向左的位移数 */
	private final static long MACHINE_LEFT = SEQUENCE_BIT;
	/** 时间戳的位移数 */
	private final static long TIMESTMP_LEFT = DATACENTER_LEFT + DATACENTER_BIT;
	/** 数据中心 */
	private long datacenterId;
	/** 机器标识 */
	private long machineId;
	/** 序列号 */
	private long sequence = 0L;
	/** 上一次时间戳 */
	private long lastTimeStamp = -1L;

	private SnowFlake(long datacenterId, long machineId) {
		if (datacenterId > MAX_DATACENTER_NUM || datacenterId < 0) {
			throw new IllegalArgumentException("datacenterId can't be greater than MAX_DATACENTER_NUM or less than 0");
		}
		if (machineId > MAX_MACHINE_NUM || machineId < 0) {
			throw new IllegalArgumentException("machineId can't be greater than MAX_MACHINE_NUM or less than 0");
		}
		this.datacenterId = datacenterId;
		this.machineId = machineId;
	}

	public static SnowFlake getInstance(WorkerIdStrategy workerIdStrategy) {

		long datacenterId = workerIdStrategy.getDatacenterId();
		long machineId = workerIdStrategy.getMachineId();
		if (datacenterId > MAX_DATACENTER_NUM || datacenterId < 0) {
			throw new IllegalArgumentException("datacenterId can't be greater than MAX_DATACENTER_NUM or less than 0");
		}
		if (machineId > MAX_MACHINE_NUM || machineId < 0) {
			throw new IllegalArgumentException("machineId can't be greater than MAX_MACHINE_NUM or less than 0");
		}
		return new SnowFlake(datacenterId, machineId);
	}

	public static SnowFlake getInstance() {
		WorkerIdStrategy workerIdStrategy = DefaultWorkerIdStrategy.getInstance();
		long datacenterId = workerIdStrategy.getDatacenterId();
		long machineId = workerIdStrategy.getMachineId();
		if (datacenterId > MAX_DATACENTER_NUM || datacenterId < 0) {
			throw new IllegalArgumentException("datacenterId can't be greater than MAX_DATACENTER_NUM or less than 0");
		}
		if (machineId > MAX_MACHINE_NUM || machineId < 0) {
			throw new IllegalArgumentException("machineId can't be greater than MAX_MACHINE_NUM or less than 0");
		}
		return new SnowFlake(datacenterId, machineId);
	}

	/**
	 * 
	 * 获取Id
	 * 
	 * @return long
	 */
	public synchronized long nextId() {
		long currentTimeStamp = getCurrentTimeStamp();
		if (currentTimeStamp < lastTimeStamp) {
			throw new RuntimeException("Clock moved backwards.  Refusing to generate id");
		}

		if (currentTimeStamp == lastTimeStamp) {
			// 相同毫秒内，序列号自增
			sequence = (sequence + 1) & MAX_SEQUENCE;
			// 同一毫秒的序列数已经达到最大
			if (sequence == 0L) {
				currentTimeStamp = getNextMilliSencodsTimeStamp();
			}
		} else {
			// 不同毫秒内，序列号置为0
			sequence = 0L;
		}

		lastTimeStamp = currentTimeStamp;

		return (currentTimeStamp - START_TIMESTAMP) << TIMESTMP_LEFT // 时间戳部分
				| datacenterId << DATACENTER_LEFT // 数据中心部分
				| machineId << MACHINE_LEFT // 机器标识部分
				| sequence; // 序列号部分
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

	public static void main(String[] args) {
		SnowFlake snowFlake = SnowFlake.getInstance();

		long start = System.currentTimeMillis();
		for (int i = 0; i < 1000000; i++) {
			System.out.println("id:" + snowFlake.nextId());
		}

		System.out.println("耗时(ms):" + (System.currentTimeMillis() - start));

	}
}