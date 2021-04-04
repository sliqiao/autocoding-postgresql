package com.autocoding.threadpool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.OptionalInt;

import com.google.common.base.Joiner;

import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName: MonitoredThreadPoolExecutorUtil
 * @author: QiaoLi
 * @date: Oct 13, 2020 5:21:18 PM
 */
@Slf4j
class MonitoredThreadPoolExecutorUtil {

	private static int maxNameLength = 15;
	public static char NOTATION_ADD = '+';
	public static char NOTATION_SUBSTRACTION = '-';
	public static char NOTATION_VERTICAL_LINE = '|';

	public static void log(Collection<MonitoredThreadPoolExecutor> monitoredThreadPoolCollection) {
		if (null == monitoredThreadPoolCollection || monitoredThreadPoolCollection.size() == 0) {
			return;
		}
		final StringBuilder sb = MonitoredThreadPoolExecutorUtil
				.logStatSummary(monitoredThreadPoolCollection);
		MonitoredThreadPoolExecutorUtil.log.info(sb.toString());
	}

	private static StringBuilder logStatSummary(
			Collection<MonitoredThreadPoolExecutor> monitoredThreadPoolCollection) {
		final StringBuilder sb = MonitoredThreadPoolExecutorUtil.logTitle(2048,
				monitoredThreadPoolCollection);

		final List<MonitoredThreadPoolExecutor> monitoredThreadPoolExecutorList = new ArrayList<>(
				monitoredThreadPoolCollection);
		final OptionalInt pollNameMaxLength = monitoredThreadPoolExecutorList.stream()
				.mapToInt((e) -> MonitoredThreadPoolExecutorUtil.getName(e.getPoolName()).length())
				.max();
		final int maxBeanClassLengthInt = Math.max(5, pollNameMaxLength.orElse(0));

		final String title = String.format(
				"%-2s|%-" + maxBeanClassLengthInt + "s|%15s|%15s|%12s|%12s|%15s|%15s|%12s|%10s|",
				"No", "PoolName", "ActiveThreadNum", "CurrentPoolSize", "CorePoolSize",
				"MaxPoolSize", "LargestPoolSize", "TaskCompleted", "TaskInQueue", "TaskTotal");
		sb.append(title).append('\n');
		MonitoredThreadPoolExecutorUtil.printSepLine(sb, title);
		monitoredThreadPoolExecutorList
		.sort(Comparator.comparing(MonitoredThreadPoolExecutor::getTaskCount).reversed());
		for (int i = 0; i <= monitoredThreadPoolExecutorList.size() - 1; i++) {
			final MonitoredThreadPoolExecutor e = monitoredThreadPoolExecutorList.get(i);
			sb.append(String.format("%-2s", i + 1)).append('|');
			sb.append(String.format("%-" + maxBeanClassLengthInt + "s",
					MonitoredThreadPoolExecutorUtil.getName(e.getPoolName()))).append('|');
			sb.append(String.format("%15s", e.getActiveCount())).append('|');
			sb.append(String.format("%15s", e.getPoolSize())).append('|');
			sb.append(String.format("%12s", e.getCorePoolSize())).append('|');
			sb.append(String.format("%12s", e.getMaximumPoolSize())).append('|');
			sb.append(String.format("%15s", e.getLargestPoolSize())).append('|');
			sb.append(String.format("%15s", e.getCompletedTaskCount())).append('|');
			sb.append(String.format("%12s", e.getQueue().size())).append('|');
			sb.append(String.format("%10s", e.getTaskCount())).append('|');
			sb.append('\n');
			sb.append(String.format("wating tasks:%s",
					Joiner.on(",").join(e.getWatingTaskIdSet()))).append('\n');
			sb.append(String.format("executing tasks:%s",
					Joiner.on(",").join(e.getExecutingTaskIdSet()))).append('\n');
			sb.append(String.format("completed tasks:%s",
					Joiner.on(",").join(e.getCompletedTaskIdSet()))).append('\n');
			sb.append(String.format("rejected tasks:%s",
					Joiner.on(",").join(e.getRejectedTaskIdSet()))).append('\n');
			e.clearTaskIdSet();
		}
		MonitoredThreadPoolExecutorUtil.printSepLine(sb, title);
		return sb;
	}

	private static StringBuilder logTitle(int initSize,
			Collection<MonitoredThreadPoolExecutor> monitoredThreadPoolCollection) {
		final StringBuilder sb = new StringBuilder(initSize);
		final String formatStr = String.format(
				"MonitoredThreadPoolExecutor stats ,num of threadPool:%d .... ",
				monitoredThreadPoolCollection.size());
		sb.append(formatStr).append("\n");
		return sb;
	}

	private static void printSepLine(StringBuilder sb, String title) {
		title.chars().forEach((c) -> {
			if (c == MonitoredThreadPoolExecutorUtil.NOTATION_VERTICAL_LINE) {
				sb.append(MonitoredThreadPoolExecutorUtil.NOTATION_ADD);
			} else {
				sb.append(MonitoredThreadPoolExecutorUtil.NOTATION_SUBSTRACTION);
			}
		});
		sb.append('\n');
	}

	private static String getName(String name) {
		if (name == null) {
			return null;
		}
		if (name.length() > MonitoredThreadPoolExecutorUtil.maxNameLength) {
			return "..." + name
					.substring(name.length() - MonitoredThreadPoolExecutorUtil.maxNameLength + 3);
		} else {
			return name;
		}
	}

}
