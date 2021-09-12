package com.autocoding.org.apache.commons.lang3;

import java.text.ParseException;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.junit.Test;

public class DurationFormatUtilTest {
	@Test
	public void test() throws ParseException {
		final Date date1 = DateUtils.parseDate("2020-10-01 10:00:00", "yyyy-MM-dd HH:mm:ss");
		final Date date2 = DateUtils.parseDate("2021-12-02 11:01:01", "yyyy-MM-dd HH:mm:ss");
		final long durationMillis = date2.getTime() - date1.getTime();
		final String s = DurationFormatUtils.formatPeriod(date1.getTime(), date2.getTime(),
				"'耗时:'y'年'MM'月'dd'天'HH'小时'mm'分钟'ss'秒'SS'毫秒'", false, TimeZone.getDefault());
		System.out.println(s);

	}

}
