package com.autocoding.id;

import java.util.Random;

public final class DefaultWorkerIdStrategy implements WorkerIdStrategy {

	private static DefaultWorkerIdStrategy instance = new DefaultWorkerIdStrategy();

	private DefaultWorkerIdStrategy() {
	}

	public static DefaultWorkerIdStrategy getInstance() {
		return instance;
	}

	@Override
	public long getDatacenterId() {
		int randomInt = new Random().nextInt(32);
		return Long.valueOf(randomInt);
	}

	@Override
	public long getMachineId() {
		int randomInt = new Random().nextInt(32);
		return Long.valueOf(randomInt);
	}

}
