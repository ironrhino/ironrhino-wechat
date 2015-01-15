package org.ironrhino.wechat.support;

import java.io.IOException;

import org.ironrhino.core.util.ErrorMessage;
import org.ironrhino.core.util.JsonUtils;
import org.ironrhino.wechat.model.WechatMenu;
import org.ironrhino.wechat.service.Wechat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;

@Component
public class WechatMenuControl {

	@Autowired
	private Wechat wechat;

	public void create(WechatMenu menu) throws IOException {
		menu.validate();
		try {
			if (get().equals(menu))
				return;
		} catch (Exception e) {
			e.printStackTrace();
		}
		String result = wechat.invoke("/menu/create", JsonUtils.toJson(menu));
		JsonNode node = JsonUtils.fromJson(result, JsonNode.class);
		int errcode = node.get("errcode").asInt();
		if (errcode > 0)
			throw new ErrorMessage("errcode:{0},errmsg:{1}", new Object[] {
					node.get("errcode").asText(), node.get("errmsg").asText() });
	}

	public WechatMenu get() throws IOException {
		String result = wechat.invoke("/menu/get", null);
		JsonNode node = JsonUtils.fromJson(result, JsonNode.class);
		if (node.has("errcode"))
			throw new ErrorMessage("errcode:{0},errmsg:{1}", new Object[] {
					node.get("errcode").asText(), node.get("errmsg").asText() });
		return new WechatMenu(node.get("menu").toString());
	}

	public void delete() throws IOException {
		String result = wechat.invoke("/menu/delete", null);
		JsonNode node = JsonUtils.fromJson(result, JsonNode.class);
		int errcode = node.get("errcode").asInt();
		if (errcode > 0)
			throw new ErrorMessage("errcode:{0},errmsg:{1}", new Object[] {
					node.get("errcode").asText(), node.get("errmsg").asText() });
	}

}
