package com.autocoding.threadpool.warn;

import lombok.Data;

/**
 * Configuration properties for dingding.
 * 
 * @ClassName: DingDingProperties
 * @author: QiaoLi
 * @date: Oct 29, 2020 9:04:24 AM
 */
@Data
public class DingDingProperties {
	/**
	 * webhookUrl
	 */
	private String webhookUrl;
	/**
	 * secket
	 */
	private String secret;

	/**
	 * safeMode
	 */
	private String safeMode;

	public static DingDingProperties getDefault() {
		final DingDingProperties dingDingProperties = new DingDingProperties();
		dingDingProperties.setWebhookUrl(
				"https://oapi.dingtalk.com/robot/send?access_token=c596a0c0ada118cadcdc7647d6389a019bd2786886b50e0e6005ad7742c64eea");
		dingDingProperties
		.setSecret("SECae11d785d399231f0534d8c7c4843a0e89e542734ec91d5e32bf820278ab31c2");
		dingDingProperties.setSafeMode("sign");
		return dingDingProperties;
	}

	public SafeMode safeMode() {
		return SafeMode.parseByName(this.safeMode);
	}

	public enum SafeMode {

		CUSTOME_KEYWORDS("customKeyWords", "自定义关键字"), SIGN("sign", "签名"), IP("ip", "IP地址段");
		private final String name;
		private final String remark;

		private SafeMode(String name, String remark) {
			this.name = name;
			this.remark = remark;

		}

		public static SafeMode parseByName(String name) {
			for (final SafeMode e : SafeMode.values()) {
				if (e.name.equals(name)) {
					return e;
				}
			}
			return CUSTOME_KEYWORDS;
		}
	}

}
