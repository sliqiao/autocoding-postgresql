package com.autocoding.hutool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@BenchmarkMode(value = { Mode.All })
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
public class 普通关键字过滤Test {
	 
	private static List<String> keywords = new ArrayList<String>();
	static {
		keywords.add("大");
		keywords.add("大土豆");
		keywords.add("土豆");
		keywords.add("刚出锅");
		keywords.add("出锅");
	}

	public static void main(String[] args) throws Exception {
		String name = 普通关键字过滤Test.class.getName();
		Options options = new OptionsBuilder().include(name).forks(1).measurementIterations(10).warmupIterations(3)
				.build();
		new Runner(options).run();
	}

	@Benchmark
	@Test
	public void 普通测试() {
		String text = "我有一颗大土豆，刚出锅的";
	    List<String> hitList=new ArrayList<>();
	    for(String keyword:keywords){
	    	if(text.contains(keyword)){
	    		hitList.add(keyword);
	    	}
	    }
		System.err.println(hitList);

	}

	 
}
