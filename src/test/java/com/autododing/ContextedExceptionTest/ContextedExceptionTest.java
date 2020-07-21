package com.autododing.ContextedExceptionTest;

import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.exception.ContextedRuntimeException;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @ClassName: ContextedExceptionTest
 * @Description:(这里用一句话描述这个类的作用)
 * @author: QiaoLi
 * @date: Jul 21, 2020 11:25:46 AM
 */
@Slf4j
public class ContextedExceptionTest {
	public static void main(String[] args) {
		try {
			throw new IllegalAccessException("非法访问异常");
		} catch (Exception e) {
			e = new ContextedRuntimeException(e).addContextValue("user.dir", SystemUtils.USER_DIR);
			log.error("执行ContextedExceptionTest.main()异常:", e);
		}
	}
}
