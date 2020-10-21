package com.autocoding.snowflake;

import java.util.Random;

public final class DefaultWorkerIdStrategy implements WorkerIdStrategy {


	private Long dataCenterId = null;
	private Long machineId = null;

	private DefaultWorkerIdStrategy() {
	}

	public static WorkerIdStrategy getInstance(Long dataCenterId, Long machineId) {
		if (null == dataCenterId) {
			throw new IllegalArgumentException("dataCenterId不能为Null");
		}
		if (null == machineId) {
			throw new IllegalArgumentException("machineId不能为Null");
		}
		DefaultWorkerIdStrategy defaultWorkerIdStrategy = new DefaultWorkerIdStrategy();
		defaultWorkerIdStrategy.dataCenterId = dataCenterId;
		defaultWorkerIdStrategy.machineId = machineId;
		return defaultWorkerIdStrategy;
	}

	public static WorkerIdStrategy getInstance() {
		return getInstance(0L, 0L);
	}

	@Override
	public long getDataCenterId() {
		if (null != dataCenterId) {
			return dataCenterId;
		}
		int randomInt = new Random().nextInt(32);
		return Long.valueOf(randomInt);
	}

	@Override
	public long getMachineId() {
		if (null != machineId) {
			return machineId;
		}
		int randomInt = new Random().nextInt(32);
		return Long.valueOf(randomInt);
	}

}
