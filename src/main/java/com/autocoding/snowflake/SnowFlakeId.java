package com.autocoding.snowflake;

import lombok.Data;

@Data
public class SnowFlakeId {
	private long originalDataCenterId;
	private long originalMachineId;
	private long originalTimeStamp;
	private long originalSequence;

	public SnowFlakeId(Builder builder) {
		this.originalDataCenterId = builder.originalDataCenterId;
		this.originalMachineId = builder.originalMachineId;
		this.originalTimeStamp = builder.originalTimeStamp;
		this.originalSequence = builder.originalSequence;
	}

	public long id() {
		return (originalTimeStamp - SnowFlakeUtil.START_TIMESTAMP) << SnowFlakeUtil.TIMESTMP_SHIFT
				| this.originalDataCenterId << SnowFlakeUtil.DATA_CENTER_SHIFT
				| this.originalMachineId << SnowFlakeUtil.MACHINE_SHIFT | originalSequence;
	}

	public static SnowFlakeId parse(long id) {
		String sonwFlakeId = Long.toBinaryString(id);
		int length = sonwFlakeId.length();
		int sequenceStart = (int) (length < SnowFlakeUtil.MACHINE_SHIFT ? 0 : length - SnowFlakeUtil.MACHINE_SHIFT);
		int workerStart = (int) (length < SnowFlakeUtil.DATA_CENTER_SHIFT ? 0
				: length - SnowFlakeUtil.DATA_CENTER_SHIFT);
		int timeStart = (int) (length < SnowFlakeUtil.TIMESTMP_SHIFT ? 0 : length - SnowFlakeUtil.TIMESTMP_SHIFT);
		String sequence = sonwFlakeId.substring(sequenceStart, length);
		String workerId = sequenceStart == 0 ? "0" : sonwFlakeId.substring(workerStart, sequenceStart);
		String dataCenterId = workerStart == 0 ? "0" : sonwFlakeId.substring(timeStart, workerStart);
		String time = timeStart == 0 ? "0" : sonwFlakeId.substring(0, timeStart);
		int sequenceInt = Integer.valueOf(sequence, 2);
		int workerIdInt = Integer.valueOf(workerId, 2);
		int dataCenterIdInt = Integer.valueOf(dataCenterId, 2);
		long diffTime = Long.parseLong(time, 2);
		Builder builder = SnowFlakeId.Builder.builder();
		builder.originalDataCenterId(dataCenterIdInt).originalMachineId(workerIdInt)
				.originalTimeStamp(diffTime + SnowFlakeUtil.START_TIMESTAMP).originalSequence(sequenceInt);
		return builder.build();
	}

	public static class Builder {
		private long originalDataCenterId;
		private long originalMachineId;
		private long originalTimeStamp;
		private long originalSequence;

		private Builder() {
		}

		public static Builder builder() {
			Builder builder = new Builder();
			return builder;
		}


		public Builder originalDataCenterId(long originalDataCenterId) {
			this.originalDataCenterId = originalDataCenterId;
			return this;
		}

		public Builder originalMachineId(long originalMachineId) {
			this.originalMachineId = originalMachineId;
			return this;
		}

		public Builder originalTimeStamp(long originalTimeStamp) {
			this.originalTimeStamp = originalTimeStamp;
			return this;
		}

		public Builder originalSequence(long originalSequence) {
			this.originalSequence = originalSequence;
			return this;
		}

		public SnowFlakeId build() {
			return new SnowFlakeId(this);
		}
	}

}