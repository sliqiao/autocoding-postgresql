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

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNode;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.json.JSONUtil;

@BenchmarkMode(value = { Mode.All })
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
public class TreeUtilTest {
	public static void main(String[] args) throws Exception {
		String name = TreeUtilTest.class.getName();
		Options options = new OptionsBuilder().include(name).forks(1).measurementIterations(10).warmupIterations(3)
				.build();
		new Runner(options).run();
	}

	@Benchmark
	@Test
	public void treeTest() {
		List<TreeNode<String>> nodeList = CollUtil.newArrayList();
		nodeList.add(new TreeNode<>("1", "0", "系统管理", 5));
		nodeList.add(new TreeNode<>("11", "1", "用户管理", 222222));
		nodeList.add(new TreeNode<>("111", "11", "用户添加", 0));
		nodeList.add(new TreeNode<>("2", "0", "店铺管理", 1));
		nodeList.add(new TreeNode<>("21", "2", "商品发布", 44));
		nodeList.add(new TreeNode<>("221", "2", "商品库存", 2));
		List<Tree<String>> treeList = TreeUtil.build(nodeList, "0");
		System.err.println("生成一棵树");
		System.err.println(JSONUtil.toJsonStr(treeList));

	}
}
