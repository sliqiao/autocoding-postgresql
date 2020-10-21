package com.autocoding.id;

import static com.autocoding.id.SnowFlake.MAX_DATACENTER_NUM;
import static com.autocoding.id.SnowFlake.MAX_MACHINE_NUM;

import org.apache.commons.lang3.StringUtils;

public final class CustomWorkerIdStrategy implements WorkerIdStrategy {

	private String dataCenterIdString;
	private String machineIdString;

	private CustomWorkerIdStrategy(String datacenterIdString, String machineIdString) {
		super();
		this.dataCenterIdString = datacenterIdString;
		this.machineIdString = machineIdString;
	}

	/**
	 * 
	 * 支持传入dataCenterIdString与machineIdString
	 * 
	 * @param dataCenterIdString 
	 * @param machineIdString
	 * @return CustomWorkerIdStrategy
	 */
	public static CustomWorkerIdStrategy getInstance(String dataCenterIdString, String machineIdString) {
		if (StringUtils.isEmpty(dataCenterIdString)) {
			throw new IllegalArgumentException("datacenterIdString不能为Null");
		}
		if (StringUtils.isEmpty(machineIdString)) {
			throw new IllegalArgumentException("machineIdString不能为Null");
		}
		return new CustomWorkerIdStrategy(dataCenterIdString, machineIdString);
	}

	@Override
	public long getDataCenterId() {
		char[] charArray = dataCenterIdString.toCharArray();
		long sum = 0L;
		for (char c : charArray) {
			sum = sum + Integer.valueOf(c);
		}
		return sum % MAX_DATACENTER_NUM;
	}

	@Override
	public long getMachineId() {
		char[] charArray = machineIdString.toCharArray();
		long sum = 0L;
		for (char c : charArray) {
			sum = sum + Integer.valueOf(c);
		}
		return sum % MAX_MACHINE_NUM;
	}

}
