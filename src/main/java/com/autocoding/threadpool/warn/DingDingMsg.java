package com.autocoding.threadpool.warn;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * 钉钉消息
 *
 */
@Data
@NoArgsConstructor
public class DingDingMsg {
	private MsgType msgType;
	private Link link;
	private Text text;

	public DingDingMsg(Link linkObject) {
		this.msgType = MsgType.LINK;
		this.link = linkObject;
	}

	public static DingDingMsg createTextDingDingMsg(String content) {
		final DingDingMsg dingDingMsg = new DingDingMsg();
		dingDingMsg.msgType = MsgType.TEXT;
		dingDingMsg.text = new Text(content);
		return dingDingMsg;
	}

	@Data
	@NoArgsConstructor
	public static class Link {
		private String text;
		private String title;
		private String picUrl;
		private String messageUrl;

		public Link(String text, String title, String picUrl, String messageUrl) {
			super();
			this.text = text;
			this.title = title;
			this.picUrl = picUrl;
			this.messageUrl = messageUrl;
		}

	}

	@Data
	@NoArgsConstructor
	public static class Text {
		private String content;

		public Text(String content) {
			this.content = content;
		}

	}

	public static enum MsgType {
		LINK("link", "链接消息类型"), TEXT("text", "文本消息类型");
		private String name;
		private String remark;

		public String getName() {
			return this.name;
		}

		public String getRemark() {
			return this.remark;
		}

		private MsgType(String name, String remark) {
			this.name = name;
			this.remark = remark;

		}
	}

}
