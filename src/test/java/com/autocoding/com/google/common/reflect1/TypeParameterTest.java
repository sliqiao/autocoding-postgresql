package com.autocoding.com.google.common.reflect1;

import org.junit.Test;

import com.google.common.reflect.TypeParameter;

public class TypeParameterTest {

	@Test
	public void test() {
		final User user = new User();
		System.err.println(user.toString());
	}

	private static class User extends TypeParameter<String> {
		private String name;

	}

}
