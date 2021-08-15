package org.apache.commons.lang3;

import java.text.ParseException;

import org.junit.Test;

import com.google.common.hash.Hashing;

public class HashTest {
	/**
	 * 一致性哈希算法测试
	 * @throws ParseException
	 */
	@Test
	public void test_consistent_hash() throws ParseException {
		for (int i = 0; i <= 100; i++) {
			final int hash = Hashing.consistentHash(10L, i);
			System.err.print(hash);
		}

	}


}
