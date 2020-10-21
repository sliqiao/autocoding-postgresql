package com.autocoding.snowflake;

public final class RedisWorkerIdStrategy implements WorkerIdStrategy {

	@Override
	public long getDataCenterId() {
		// TODO 前置条件:key=dataCenterId:{dataCenterId} value=String ，按照value进行升序排序,为什么使用value，因为不同的key，保证不同的expireTime;
		//              key=dataCenterIdSet value=Set{dataCenterId:0,dataCenterId:1,,dataCenterId:3}
		// TODO step 1:获取key=dataCenterIdSet中所有元素并过滤dataCenterId:{dataCenterId}存在，不存在从dataCenterIdSet删除，  对应的多个String,并进行升序排序，选择一个未被占用的数值，作为自己的value,保证整个过程Atomicity
		// TODO step 2:应用程序与redis保持心跳机制，每隔5分钟，将自己对应的value重新设定expireTime=5分钟
		// TODO step 3:当应用重启之后，可以循环使用这些空出来的占位值
		// TODO step 4:
		return 0L;
	}

	@Override
	public long getMachineId() {
		//TODO 同上
		return 0L;
	}

}
