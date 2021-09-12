package com.autocoding.com.google.common.escape;

import org.junit.Test;

public class HtmlEscapersTest {
	/** 
	 * 程序输出结果如下：
	 * <p>"wareQD":"手机主机丨电源适配器丨USB Type-C 数据线 ｜ Type?C转3.5mm耳机线丨手机保护壳丨贴膜（已覆在屏幕上） 丨插针丨说明书（含三包凭证）"</p>
	 * 
	 */
	@Test
	public void codePoint_method() {
		final String myString = "\"wareQD\":\"手机主机丨电源适配器丨USB Type-C 数据线 ｜ Type\u0002C转3.5mm耳机线丨手机保护壳丨贴膜（已覆在屏幕上） 丨插针丨说明书（含三包凭证）\"";
		final StringBuilder newString = new StringBuilder(myString.length());
		for (int offset = 0; offset < myString.length();) {
			final int codePoint = myString.codePointAt(offset);
			offset += Character.charCount(codePoint);

			// Replace invisible control characters and unused code points
			switch (Character.getType(codePoint)) {
			case Character.CONTROL: // \p{Cc}
			case Character.FORMAT: // \p{Cf}
			case Character.PRIVATE_USE: // \p{Co}
			case Character.SURROGATE: // \p{Cs}
			case Character.UNASSIGNED: // \p{Cn}
				newString.append('?');
				break;
			default:
				newString.append(Character.toChars(codePoint));
				break;
			}
		}
		System.err.println(newString);
	}


}
