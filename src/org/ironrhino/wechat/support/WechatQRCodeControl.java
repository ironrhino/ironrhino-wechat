package org.ironrhino.wechat.support;

import java.io.IOException;
import java.io.Serializable;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.ironrhino.core.util.JsonUtils;
import org.ironrhino.wechat.service.Wechat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.NoArgsConstructor;

@Component
@Retryable(include = IOException.class, backoff = @Backoff(delay = 1000, maxDelay = 5000, multiplier = 2))
public class WechatQRCodeControl {

	@Autowired
	private Wechat wechat;

	public QRCodeResponse invode(QRCodeRequest request) throws IOException {
		return JsonUtils.fromJson(wechat.invoke("/qrcode/create", request.toString()), QRCodeResponse.class);
	}

	public String createTemporary(int scene_id, int expire_seconds) throws IOException {
		return generateQRCodeImageUrl(invode(new QRCodeRequest(scene_id, expire_seconds)));
	}

	public String createPermanent(int scene_id) throws IOException {
		return generateQRCodeImageUrl(invode(new QRCodeRequest(scene_id)));
	}

	public String createPermanent(String scene_id) throws IOException {
		return generateQRCodeImageUrl(invode(new QRCodeRequest(scene_id)));
	}

	private static String generateQRCodeImageUrl(QRCodeResponse response) throws IOException {
		return "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=" + URLEncoder.encode(response.getTicket(), "UTF-8");
	}

	@Data
	@NoArgsConstructor
	public static class QRCodeRequest implements Serializable {

		private static final long serialVersionUID = -5901136915808614586L;
		private Integer expire_seconds;
		private String action_name;
		private Map<String, Map<String, Serializable>> action_info = new HashMap<>();

		public QRCodeRequest(int scene_id) {
			if (scene_id < 1 || scene_id > 100000)
				throw new IllegalArgumentException("永久二维码场景值只支持1-100000");
			this.action_name = "QR_LIMIT_SCENE";
			Map<String, Serializable> scene = new HashMap<>();
			scene.put("scene_id", scene_id);
			action_info.put("scene", scene);
		}

		public QRCodeRequest(String scene_str) {
			if (StringUtils.isBlank(scene_str) || scene_str.length() > 64)
				throw new IllegalArgumentException("永久二维码场景值长度只支持1-64");
			this.action_name = "QR_LIMIT_STR_SCENE";
			Map<String, Serializable> scene = new HashMap<>();
			scene.put("scene_str", scene_str);
			action_info.put("scene", scene);
		}

		public QRCodeRequest(int scene_id, int expire_seconds) {
			if (scene_id < 1)
				throw new IllegalArgumentException("二维码场景值必须大于0");
			if (expire_seconds < 1)
				throw new IllegalArgumentException("二维码有效时间必须大于0");
			if (expire_seconds < 1 || expire_seconds > 30 * 24 * 3600)
				throw new IllegalArgumentException("二维码有效时间最大30天");
			this.expire_seconds = expire_seconds;
			this.action_name = "QR_SCENE";
			Map<String, Serializable> scene = new HashMap<>();
			scene.put("scene_id", scene_id);
			action_info.put("scene", scene);
		}

		public String toString() {
			return JsonUtils.toJson(this);
		}

	}

	@Data
	public static class QRCodeResponse implements Serializable {

		private static final long serialVersionUID = -6354943254377767095L;

		private String ticket;
		private Integer expire_seconds;
		private String url;

		public String toString() {
			return JsonUtils.toJson(this);
		}
	}

}
