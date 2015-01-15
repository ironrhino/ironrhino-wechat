package org.ironrhino.wechat.support;

import java.io.IOException;
import java.net.URLEncoder;

import org.ironrhino.core.util.ErrorMessage;
import org.ironrhino.core.util.JsonUtils;
import org.ironrhino.wechat.service.Wechat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;

@Component
public class WechatQRCodeControl {

	@Autowired
	private Wechat wechat;

	public String createTemporary(int scene_id, int expire_seconds)
			throws IOException {
		if (scene_id < 1)
			throw new IllegalArgumentException(
					"scene_id should be large than 0, but was " + scene_id);
		StringBuilder sb = new StringBuilder();
		sb.append("{\"expire_seconds\":");
		sb.append(expire_seconds);
		sb.append(",\"action_name\":\"QR_SCENE\",\"action_info\":{\"scene\":{\"scene_id\":");
		sb.append(scene_id);
		sb.append("}}}");
		String request = sb.toString();
		return generateQRCodeImageUrl(wechat.invoke("/qrcode/create", request));
	}

	public String createPermanent(int scene_id) throws IOException {
		if (scene_id < 1 || scene_id > 100000)
			throw new IllegalArgumentException(
					"scene_id should be 1-100000, but was " + scene_id);
		StringBuilder sb = new StringBuilder();
		sb.append("{\"action_name\":\"QR_LIMIT_SCENE\",\"action_info\":{\"scene\":{\"scene_id\":");
		sb.append(scene_id);
		sb.append("}}}");
		String request = sb.toString();
		return generateQRCodeImageUrl(wechat.invoke("/qrcode/create", request));

	}

	private static String generateQRCodeImageUrl(String result)
			throws IOException {
		JsonNode node = JsonUtils.fromJson(result, JsonNode.class);
		if (node.has("errcode"))
			throw new ErrorMessage("errcode:{0},errmsg:{1}", new Object[] {
					node.get("errcode").asText(), node.get("errmsg").asText() });
		String ticket = node.get("ticket").textValue();
		return "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket="
				+ URLEncoder.encode(ticket, "UTF-8");
	}

}
