package com.autocoding.hutool;

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

import cn.hutool.dfa.WordTree;

@BenchmarkMode(value = { Mode.All })
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
public class DFA关键字过滤Test {
	private static WordTree tree = new WordTree();
	static {
		DFA关键字过滤Test.tree.addWord("大");
		DFA关键字过滤Test.tree.addWord("大土豆");
		DFA关键字过滤Test.tree.addWord("土豆");
		DFA关键字过滤Test.tree.addWord("刚出锅");
		DFA关键字过滤Test.tree.addWord("出锅");
	}

	public static void main(String[] args) throws Exception {
		final String name = DFA关键字过滤Test.class.getName();
		final Options options = new OptionsBuilder().include(name).forks(1)
				.measurementIterations(10).warmupIterations(3).build();
		new Runner(options).run();
	}

	@Benchmark
	@Test
	public void DFA测试() {
		final String text = "我有一颗大土豆，刚出锅的";
		final List<String> matchAll = DFA关键字过滤Test.tree.matchAll(text, -1, false, false);
		System.err.println(matchAll);

	}

}
