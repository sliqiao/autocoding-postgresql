package org.apache.commons.lang3;

import java.text.ParseException;

import org.apache.commons.lang3.builder.Diff;
import org.junit.Test;

public class TypeUtilsTest {
	@Test
	public void test() throws ParseException {
		final MyDiff myDiff = new MyDiff("name");
		System.err.println(myDiff.getType());

	}

	private static class MyDiff extends Diff<Integer> {

		protected MyDiff(String fieldName) {
			super(fieldName);
			// TODO Auto-generated constructor stub
		}

		private static final long serialVersionUID = 1L;

		@Override
		public Integer getLeft() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Integer getRight() {
			// TODO Auto-generated method stub
			return null;
		}

	}
}
