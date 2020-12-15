package com.autocoding.hutool;

import org.junit.Test;

import cn.hutool.core.exceptions.ExceptionUtil;

public class ExceptionUtilTest {
	@Test
	public void getStackElements() {
		StackTraceElement[] stackTraceElementArray = ExceptionUtil.getStackElements();
		// 输出虚拟机栈VM Stack所有的StackFrame信息
		System.out.println("测试:" + stackTraceElementArray);

	}

	@Test
	public void stacktraceToString() {
		String content = ExceptionUtil.stacktraceToString(new RuntimeException("测试"));
		System.out.println(content);

	}
	@Test
	public void stacktraceToOneLineString() {
		String content = ExceptionUtil.stacktraceToOneLineString(new RuntimeException("测试"));
		System.out.println(content);

	}
}
