package com.autocoding.org.apache.commons.csv;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.junit.Test;

import com.google.common.collect.Lists;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProxyFactoryTest {

	@Test
	public void test_parse_csvfile() throws IOException {
		final Reader reader = new FileReader("I:\\test\\titles.csv");
		final Iterable<CSVRecord> records = CSVFormat.DEFAULT
				.withHeader("emp_no", "title", "from_date", "to_date").withSkipHeaderRecord()
				.parse(reader);
		for (final CSVRecord record : records) {
			final String emp_no = record.get("emp_no");
			final String title = record.get("title");
			final String from_date = record.get("from_date");
			final String to_date = record.get("from_date");
			ProxyFactoryTest.log.info("no:{},emp_no:{},title:{},from_date:{},to_date:{}",
					record.getRecordNumber(), emp_no, title, from_date, to_date);
		}
	}

	@Test
	public void test_output_csvfile() throws IOException {
		try (final CSVPrinter printer = new CSVPrinter(new FileWriter("I:\\test\\titles_new.csv"),
				CSVFormat.DEFAULT)) {
			printer.printRecord("emp_no", "title", "from_date", "to_date");
			for (final MyModel myModel : MyModel.mockList()) {
				printer.printRecord(myModel.getEmpNo(), myModel.getTitle(), myModel.getFromDate(),
						myModel.getToDate());
			}
		} catch (final Exception e) {
			ProxyFactoryTest.log.error("error", e);
		}

	}

	@Data
	private static class MyModel {
		private String empNo;
		private String title;
		private String fromDate;
		private String toDate;

		public static List<MyModel> mockList() {
			final List<MyModel> list = Lists.newArrayList();
			for (int i = 1; i <= 1000; i++) {
				final MyModel myModel = new MyModel();
				myModel.empNo = "empNo-" + i;
				myModel.title = "title-" + i;
				myModel.fromDate = "fromDate-" + i;
				myModel.toDate = "toDate-" + i;
				list.add(myModel);
			}
			return list;
		}
	}
}
