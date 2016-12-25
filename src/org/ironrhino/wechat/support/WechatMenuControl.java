package org.ironrhino.wechat.support;

import java.io.IOException;
import java.util.List;

import org.ironrhino.core.util.ErrorMessage;
import org.ironrhino.core.util.JsonUtils;
import org.ironrhino.wechat.model.WechatConditionalMenu;
import org.ironrhino.wechat.model.WechatMenu;
import org.ironrhino.wechat.service.Wechat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;

@Component
@Retryable(include = IOException.class, backoff = @Backoff(delay = 1000, maxDelay = 5000, multiplier = 2))
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
			throw new ErrorMessage("errcode:{0},errmsg:{1}",
					new Object[] { node.get("errcode").asText(), node.get("errmsg").asText() });
	}

	public void create(String json) throws IOException {
		String result = wechat.invoke("/menu/create", json);
		JsonNode node = JsonUtils.fromJson(result, JsonNode.class);
		int errcode = node.get("errcode").asInt();
		if (errcode > 0)
			throw new ErrorMessage("errcode:{0},errmsg:{1}",
					new Object[] { node.get("errcode").asText(), node.get("errmsg").asText() });
	}

	public WechatMenu get() throws IOException {
		String result = wechat.invoke("/menu/get", null);
		JsonNode node = JsonUtils.fromJson(result, JsonNode.class);
		if (node.has("errcode"))
			throw new ErrorMessage("errcode:{0},errmsg:{1}",
					new Object[] { node.get("errcode").asText(), node.get("errmsg").asText() });
		return new WechatMenu(node.get("menu").toString());
	}

	public String getAsText() throws IOException {
		String result = wechat.invoke("/menu/get", null);
		JsonNode node = JsonUtils.fromJson(result, JsonNode.class);
		if (node.has("errcode"))
			throw new ErrorMessage("errcode:{0},errmsg:{1}",
					new Object[] { node.get("errcode").asText(), node.get("errmsg").asText() });
		return node.get("menu").toString();
	}

	public void delete() throws IOException {
		String result = wechat.invoke("/menu/delete", null);
		JsonNode node = JsonUtils.fromJson(result, JsonNode.class);
		int errcode = node.get("errcode").asInt();
		if (errcode > 0)
			throw new ErrorMessage("errcode:{0},errmsg:{1}",
					new Object[] { node.get("errcode").asText(), node.get("errmsg").asText() });
	}

	public Integer createConditional(WechatConditionalMenu menu) throws IOException {
		menu.validate();
		String result = wechat.invoke("/menu/addconditional", JsonUtils.toJson(menu));
		JsonNode node = JsonUtils.fromJson(result, JsonNode.class);
		int errcode = node.get("errcode").asInt();
		if (errcode > 0)
			throw new ErrorMessage("errcode:{0},errmsg:{1}",
					new Object[] { node.get("errcode").asText(), node.get("errmsg").asText() });
		return node.get("menuid").asInt();
	}

	public List<WechatConditionalMenu> getConditionals() throws IOException {
		String result = wechat.invoke("/menu/get", null);
		JsonNode node = JsonUtils.fromJson(result, JsonNode.class);
		if (node.has("errcode"))
			throw new ErrorMessage("errcode:{0},errmsg:{1}",
					new Object[] { node.get("errcode").asText(), node.get("errmsg").asText() });
		return JsonUtils.fromJson(node.get("conditionalmenu").toString(),
				new TypeReference<List<WechatConditionalMenu>>() {
				});
	}

	public void deleteConditional(Integer menuid) throws IOException {
		String result = wechat.invoke("/menu/delconditional", "{\"menuid\":" + menuid + "}");
		JsonNode node = JsonUtils.fromJson(result, JsonNode.class);
		if (node.has("errcode"))
			throw new ErrorMessage("errcode:{0},errmsg:{1}",
					new Object[] { node.get("errcode").asText(), node.get("errmsg").asText() });
	}

	public WechatMenu tryMatchConditional(String user_id) throws IOException {
		String result = wechat.invoke("/menu/trymatch", "{\"user_id\":\"" + user_id + "\"}");
		JsonNode node = JsonUtils.fromJson(result, JsonNode.class);
		if (node.has("errcode"))
			throw new ErrorMessage("errcode:{0},errmsg:{1}",
					new Object[] { node.get("errcode").asText(), node.get("errmsg").asText() });
		return new WechatMenu(result);
	}

}
