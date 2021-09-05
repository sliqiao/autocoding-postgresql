package com.google.common.reflect1;

import org.junit.Test;

import com.google.common.reflect.TypeToken;

public class TypeTokenTest {

	@Test
	public void test() {
		final User user = new User();
		System.err.println(user.getType());
		System.err.println(TypeToken.of(User.class).getType());
	}

	private static class User extends TypeToken<User> {
		private String name;

	}

}
