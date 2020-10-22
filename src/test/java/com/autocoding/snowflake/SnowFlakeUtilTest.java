package com.autocoding.snowflake;

import org.junit.Test;

public class SnowFlakeUtilTest {
	@Test
	public void snowFlakeId() {
		SnowFlakeUtil snowFlake = SnowFlakeUtil.getInstance(DefaultWorkerIdStrategy.getInstance(31L, 31L));
		long start = System.currentTimeMillis();
		for (int i = 0; i < 10; i++) {
			SnowFlakeId snowFlakeId = snowFlake.next();
			System.out.println("原始：id:" + snowFlakeId.id() + "|idString:" + snowFlakeId.idString());
			SnowFlakeId parseSnowFlakeId = SnowFlakeId.parse(snowFlakeId.id());
			System.out.println("解析：id:" + parseSnowFlakeId.id() + "|idString:" + parseSnowFlakeId.idString());
		}
		System.out.println("耗时(ms):" + (System.currentTimeMillis() - start));

	}
}
