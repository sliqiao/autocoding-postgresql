package com.autocoding.juc.threadmessaging;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CompletableFutureTest {
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		final CompletableFuture cf = CompletableFuture.completedFuture("message").thenApply(s -> {
			return s + ",hello";
		}).thenApply(s -> s.toUpperCase());
		final String s = (String) cf.get();
		System.out.println("最终结果：" + s);
	}
}
