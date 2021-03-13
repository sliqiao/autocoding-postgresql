package com.autocoding.test;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class User {
	private String name;

	public static void main2(String[] args) {
		final List<String> list1 = new ArrayList<>();

		list1.add("liqiao");
		list1.add("liqiao");
		System.out.println(list1);

		final Date date = new Date();
	}

	public static void main1(String[] args) {
		final List<String> list = new ArrayList<>();
		list.add("liqiao");
		System.out.println(list);



	}

	public static void main(String[] args) {
		System.out.println("args = " + args);
		System.out.println("args = " + Arrays.deepToString(args));
		final int i=1;
		final int j=2;
		System.out.println(i);
		System.out.println("j = " + j);
		System.out.println("i = " + i);

	}
}
