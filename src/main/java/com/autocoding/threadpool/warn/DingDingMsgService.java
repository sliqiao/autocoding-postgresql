package com.autocoding.threadpool.warn;

public interface DingDingMsgService {
	/**
	 * 
	 * 发送钉钉消息：msgType:link
	 * 
	 * @param dingDingMsg
	 * @return boolean
	 */
	public boolean sent(RequestSourceEnum requestSourceEnum,DingDingMsg dingDingMsg);

}
