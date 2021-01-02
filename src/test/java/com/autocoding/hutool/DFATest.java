package com.autocoding.hutool;

import java.util.List;

import org.junit.Test;

import cn.hutool.dfa.WordTree;

/**
 * 找出所有匹配的关键字<br>
 * 密集匹配原则：假如关键词有 ab,b，文本是abab，将匹配 [ab,b,ab]<br>
 * 贪婪匹配（最长匹配）原则：假如关键字a,ab，最长匹配将匹配[a, ab]
 *
 * @param text
 *            被检查的文本
 * @param limit
 *            限制匹配个数
 * @param isDensityMatch
 *            是否使用密集匹配原则
 * @param isGreedMatch
 *            是否使用贪婪匹配（最长匹配）原则
 * @return 匹配的词列表
 */
public class DFATest {
	private static WordTree tree = new WordTree();
	static {
		DFATest.tree.addWord("ab");
		DFATest.tree.addWord("b");
	}

	@Test
	public void DFA测试() {
		final String text = "ababbddddd";
		final List<String> matchAll = DFATest.tree.matchAll(text, -1, false, false);
		System.err.println(matchAll);

	}

}
