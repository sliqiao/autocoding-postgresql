package com.autododing.ContextedExceptionTest;

import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.exception.ContextedRuntimeException;

import lombok.extern.slf4j.Slf4j;

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
