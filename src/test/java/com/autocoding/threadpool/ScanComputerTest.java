package com.autocoding.threadpool;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;

import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 题目要求：扫描计算机 D盘、E盘、F盘指定目录下所有的文件，要求把所有文件名，分别输出到 C盘下：D.txt、E.txt、F.txt
 * 1、要求 D盘、E盘、F盘分别使用三个线程去扫描
 * 2、要求 生成【 D.txt、E.txt、F.txt 】 也要使用另外3个线程去执行,先扫描完成的目录，先生成对应的文件
 * 3、在主线程中统计整个操作耗时多少秒
 * @author Administrator
 *
 */
@Slf4j
public class ScanComputerTest {

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		ScanComputerTest.log.info("开始执行任务");
		final StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		final List<String> fileDirs = new ArrayList<String>();
		fileDirs.add("D:\\");
		fileDirs.add("E:\\");
		fileDirs.add("F:\\");
		final ExecutorService scanExecutorService = Executors.newFixedThreadPool(3);
		final ExecutorService fileGenaratingExecutorService = Executors.newFixedThreadPool(3);
		final CompletionService<List<String>> scanCompletionService = new ExecutorCompletionService<>(
				scanExecutorService);
		final CompletionService<Void> fileGenaratingCompletionService = new ExecutorCompletionService<>(
				fileGenaratingExecutorService);
		final CountDownLatch fileGenaratingCountDownLatch = new CountDownLatch(fileDirs.size());
		for (final String fileDir : fileDirs) {
			scanCompletionService.submit(new ScanCallable(fileDir));
		}
		for (int i = 1; i <= fileDirs.size(); i++) {
			final List<String> fileNames = scanCompletionService.take().get();
			final String fileOutputPath = "C:\\" + fileNames.get(0).charAt(0) + ".txt";
			fileGenaratingCompletionService.submit(new FileGenaratingCallable(fileNames,
					fileOutputPath, fileGenaratingCountDownLatch));
		}
		fileGenaratingCountDownLatch.await();
		stopWatch.stop();
		ScanComputerTest.log.warn("..... 结束执行任务，共耗时【{}】ms ",
				stopWatch.getTime(TimeUnit.MILLISECONDS));

	}

	private static class ScanCallable implements Callable<List<String>> {
		private final String fileDir;

		public ScanCallable(String fileDir) {
			this.fileDir = fileDir;
		}

		@Override
		public List<String> call() throws Exception {
			ScanComputerTest.log.info("开始扫描任务，扫描路径：{}", this.fileDir);
			final List<String> fileNames = new ArrayList<String>();
			fileNames.add(this.fileDir);
			final List<File> files = FileUtil.loopFiles(this.fileDir);
			for (final File f : files) {
				fileNames.add(f.getAbsolutePath());
			}
			ScanComputerTest.log.warn("..... 结束扫描任务，扫描路径：{}", this.fileDir);
			return fileNames;
		}

	}

	private static class FileGenaratingCallable implements Callable<Void> {
		private final List<String> fileNames;
		private final String fileOutputPath;
		private final String fileName;
		private final CountDownLatch fileGenaratingCountDownLatch;

		public FileGenaratingCallable(List<String> fileNames, String fileOutputPath,
				CountDownLatch fileGenaratingCountDownLatch) {
			this.fileNames = fileNames;
			this.fileOutputPath = fileOutputPath;
			this.fileGenaratingCountDownLatch = fileGenaratingCountDownLatch;
			this.fileName = new File(fileOutputPath).getName();
		}

		@Override
		public Void call() throws Exception {
			try {
				ScanComputerTest.log.info("开始文件名生成任务：{}", this.fileName);
				FileUtil.writeLines(this.fileNames, this.fileOutputPath, "utf-8");
			} finally {
				ScanComputerTest.log.warn("..... 结束文件名生成任务：{}", this.fileName);
				this.fileGenaratingCountDownLatch.countDown();
			}

			return null;
		}

	}
}
