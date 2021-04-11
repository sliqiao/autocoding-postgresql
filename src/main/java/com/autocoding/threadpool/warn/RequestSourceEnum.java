package com.autocoding.threadpool.warn;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
public enum RequestSourceEnum {

	SYSTEM_DEPLOYMENT("system-deployment", "系统部署") {
		@Override
		public String extractContent(String jsonStr) {
			final JSONObject jsonObject = JSON.parseObject(jsonStr);
			final String content = jsonObject.getString("content");
			return content;
		}
	},
	SKYWALKING_ALARM("skywalking-alarm", "skywalking告警") {
		@Override
		public String extractContent(String jsonStr) {
			final JSONArray jsonArray = JSON.parseArray(jsonStr);
			final StringBuilder sb = new StringBuilder();
			for (int i = 0; i <= jsonArray.size() - 1; i++) {
				sb.append(jsonArray.getJSONObject(i).getString("alarmMessage"));
				sb.append("\n");
			}
			final String content = sb.toString();
			return content;
		}
	};
	private String name;
	private String remark;

	private RequestSourceEnum(String name, String remark) {
		this.name = name;
		this.remark = remark;
	}

	public static RequestSourceEnum parseByName(String name) {
		if (null == name) {
			throw new NullPointerException("name is null");
		}
		for (final RequestSourceEnum e : RequestSourceEnum.values()) {
			if (name.equals(e.name)) {
				return e;
			}
		}
		return null;
	}

	public abstract String extractContent(String jsonStr);
}