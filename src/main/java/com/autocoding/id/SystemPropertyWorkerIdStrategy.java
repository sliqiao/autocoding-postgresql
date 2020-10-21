package com.autocoding.id;

public class SystemPropertyWorkerIdStrategy implements WorkerIdStrategy {

	private static final String KEY_DATA_CENTER_ID = "datacenterId";
	private static final String KEY_MACHINE_ID = "machineId";
	private static SystemPropertyWorkerIdStrategy instance = new SystemPropertyWorkerIdStrategy();

	private SystemPropertyWorkerIdStrategy() {
	}

	public static SystemPropertyWorkerIdStrategy getInstance() {
		return instance;
	}

	@Override
	public long getDataCenterId() {
		String datacenterId = System.getProperty(KEY_DATA_CENTER_ID, "0");
		return Long.valueOf(datacenterId);
	}

	@Override
	public long getMachineId() {
		String machineId = System.getProperty(KEY_MACHINE_ID, "0");
		return Long.valueOf(machineId);
	}

}
