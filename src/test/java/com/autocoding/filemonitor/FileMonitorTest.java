package com.autocoding.filemonitor;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileMonitorTest {

	private static Logger logger = LoggerFactory.getLogger(FileMonitorTest.class);

	public static void main(String[] args) throws IOException, InterruptedException {
		System.out.println("start to watch files changes");
		//实例化WatchService对象
		final WatchService watchService = FileSystems.getDefault().newWatchService();
		final String localPath = "C:\\data";
		//构建Path对象
		final Path path = Paths.get(localPath);
		//注册监听事件
		path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY,
				StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE);
		final Executor executor = Executors.newFixedThreadPool(5);
		for (;;) {
			final WatchKey key = watchService.take();
			if (null == key) {
				FileMonitorTest.logger.info("暂无文件变化");
				continue;
			}
			//利用 key.pollEvents() 方法返回一系列的事件列表
			for (final WatchEvent<?> event : key.pollEvents()) {
				//得到 监听的事件类型
				final Kind kind = event.kind();
				final Path pathName = (Path) event.context();
				final Object context = event.context();
				executor.execute(new Runnable() {
					@Override
					public void run() {
								FileMonitorTest.logger.info(kind.name() + "--" + pathName);

					}
				});

				//每次的到新的事件后，需要重置监听池
				key.reset();
			}
		}

	}

}
