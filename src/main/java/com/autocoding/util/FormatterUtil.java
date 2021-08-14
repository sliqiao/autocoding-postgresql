package com.autocoding.util;

import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;

/**
 * 消息格式化工具类
 * @author Administrator
 *
 */
public final class FormatterUtil {
	private FormatterUtil() {
	}

	/**
	 * 格式化之后的字符串
	 * @param templateStr 模板字符串
	 * @param args 参数数组
	 * @return
	 */
	public static String format(String templateStr, Object... args) {
		final FormattingTuple formattingTuple = MessageFormatter.arrayFormat(templateStr, args);
		return formattingTuple.getMessage();
	}

	public static void main(String[] args) {
		final String templateStr = "http://www.ycz.hk/#/goodsDetail?商品编码={}&外部邀请码={}";
		final Object[] tempArgs = new Object[] { "603292272930459648", "1111111" };
		System.err.println(FormatterUtil.format(templateStr, tempArgs));
	}
}
