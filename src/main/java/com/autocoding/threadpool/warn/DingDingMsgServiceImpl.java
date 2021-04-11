
package com.autocoding.threadpool.warn;

import java.net.URI;
import java.net.URLEncoder;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.autocoding.threadpool.ExecutorServiceUtil;
import com.autocoding.threadpool.warn.DingDingProperties.SafeMode;

import cn.hutool.core.codec.Base64;
import cn.hutool.http.HttpUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @ClassName: DingDingMsgServiceImpl
 * @author: QiaoLi
 * @date: Oct 29, 2020 9:16:35 AM
 */
@Slf4j
public class DingDingMsgServiceImpl implements DingDingMsgService {

	private static DingDingMsgService DINGDINGMSGSERVICE = new DingDingMsgServiceImpl();
	private final DingDingProperties dingDingProperties = DingDingProperties.getDefault();

	public static DingDingMsgService getInstance() {
		return DingDingMsgServiceImpl.DINGDINGMSGSERVICE;
	}

	/**
	 * <p>
	 * 这里实现为什么要使用延迟任务调度，因为在某一个短时间之内，大量的请求发给钉钉，钉钉会认为是重复请求，会丢请求，所以要保证有一定的时间间断
	 * </p>
	 */
	@Override
	public boolean sent(RequestSourceEnum requestSourceEnum, DingDingMsg dingDingMsg) {
		final JSONObject jsonObject = new JSONObject();
		jsonObject.put("msgtype", dingDingMsg.getMsgType().getName());
		if (dingDingMsg.getMsgType() == DingDingMsg.MsgType.LINK) {
			jsonObject.put("link", dingDingMsg.getLink());
		} else if (dingDingMsg.getMsgType() == DingDingMsg.MsgType.TEXT) {
			jsonObject.put("text", dingDingMsg.getText());
		} else {
			throw new RuntimeException("非法的MsgType:" + dingDingMsg.getMsgType());
		}

		final ScheduledFuture<Boolean> scheduledFuture = ExecutorServiceUtil
				.getDefaultScheduledExecutorService().schedule(new Callable<Boolean>() {

					@Override
					public Boolean call() throws Exception {
						final String webhookUrl = DingDingMsgServiceImpl.this.wrapSignWithUrl(
								DingDingMsgServiceImpl.this.dingDingProperties.getWebhookUrl());
						DingDingMsgServiceImpl.log.info("向钉钉webhookUrl:{}，请求来源:{},推送消息：{}",
								webhookUrl, requestSourceEnum.name(), jsonObject.toJSONString());
						final URI uri = new URI(webhookUrl);
						final String response = HttpUtil.post(uri.toString(),
								jsonObject.toJSONString());
						final Err err = JSON.parseObject(response, Err.class);
						DingDingMsgServiceImpl.log.info("接收钉钉响应：{}",
								err == null ? null : JSON.toJSONString(err));
						if (null != err && null != err.errCode
								&& err.errCode.equals(Err.ERRCODE_SUCCESSFUL)) {
							return true;
						}
						if (null != err && "ok".equals(err.getErrmsg())) {
							return true;
						}
						return false;
					}
				}, 10, TimeUnit.SECONDS);
		try {
			return scheduledFuture.get().booleanValue();
		} catch (final Exception e) {
			DingDingMsgServiceImpl.log.error("发送钉钉消息异常", e);
			return false;
		}
	}

	/**
	 * 
	 * 给原始url包装签名
	 * 
	 * @param url
	 *            https://oapi.dingtalk.com/robot/send
	 * @return String https://oapi.dingtalk.com/robot/send?access_token=XXXXXX&
	 *         timestamp=XXX&sign=XXX
	 */
	private String wrapSignWithUrl(String url) {
		final SafeMode safeMode = this.dingDingProperties.safeMode();
		if (safeMode == SafeMode.CUSTOME_KEYWORDS) {
			return url;
		} else if (safeMode == SafeMode.SIGN) {
			final Long timestamp = System.currentTimeMillis();
			final String secret = this.dingDingProperties.getSecret();
			final String stringToSign = timestamp + "\n" + secret;
			String newUrl = new String(url);
			try {
				final Mac mac = Mac.getInstance("HmacSHA256");
				mac.init(new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256"));
				final byte[] signData = mac.doFinal(stringToSign.getBytes("UTF-8"));
				final String sign = URLEncoder.encode(new String(Base64.encode(signData)), "UTF-8");
				newUrl = newUrl + "&timestamp=" + timestamp + "&sign=" + sign;
			} catch (final Exception e) {
				DingDingMsgServiceImpl.log.error("异常", e);
			}
			return newUrl;
		}

		return url;

	}

	@Data
	private static class Err {
		private static Integer ERRCODE_SUCCESSFUL = 0;
		private Integer errCode;
		private String errmsg;

	}
}
