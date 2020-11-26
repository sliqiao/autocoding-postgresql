package com.autocoding.jta;

import lombok.extern.slf4j.Slf4j;

/**
 * JTA事务解决方案
 * 
 * @ClassName: JtaTest
 * @author: QiaoLi
 * @date: Oct 22, 2020 8:18:42 AM
 */
@Slf4j
public class JtaTest {
	// 有定时任务周期性扫描本地消息表，将等待发送、处理失败的消息投递到MQ中，直到达到最大重试次数，不再重新投递
	// 达到最大重试次数的消息会进入DLQ，由人工进行处理
	public void 本地消息表_解决方案() {
		try {
			// tranction begin
			// 调用外部服务1 call rpc1(将此次调用信息存储至本地消息表)
			// 本地事务
			// 调用外部服务2 call rpc2(将此次调用信息存储至本地消息表)
			// tranction commit
		} catch (Exception e) {
			// tranction rollback
		} finally {
		}
	}
}
