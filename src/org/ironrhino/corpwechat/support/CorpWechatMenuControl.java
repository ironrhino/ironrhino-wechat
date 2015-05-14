package org.ironrhino.corpwechat.support;

import java.io.IOException;

import org.ironrhino.core.util.ErrorMessage;
import org.ironrhino.core.util.JsonUtils;
import org.ironrhino.corpwechat.model.CorpWechatMenu;
import org.ironrhino.corpwechat.service.CorpWechat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;

@Component
@Retryable(include = IOException.class, backoff = @Backoff(delay = 1000, maxDelay = 5000, multiplier = 2))
public class CorpWechatMenuControl {

	@Autowired
	private CorpWechat wechat;

	public void create(Integer agentid, CorpWechatMenu menu) throws IOException {
		menu.validate();
		try {
			if (get(agentid).equals(menu))
				return;
		} catch (Exception e) {
			e.printStackTrace();
		}
		String result = wechat.invoke("/menu/create?agentid=" + agentid,
				JsonUtils.toJson(menu));
		JsonNode node = JsonUtils.fromJson(result, JsonNode.class);
		int errcode = node.get("errcode").asInt();
		if (errcode > 0)
			throw new ErrorMessage("errcode:{0},errmsg:{1}", new Object[] {
					node.get("errcode").asText(), node.get("errmsg").asText() });
	}

	public CorpWechatMenu get(Integer agentid) throws IOException {
		String result = wechat.invoke("/menu/get?agentid=" + agentid, null);
		JsonNode node = JsonUtils.fromJson(result, JsonNode.class);
		if (node.has("errcode"))
			throw new ErrorMessage("errcode:{0},errmsg:{1}", new Object[] {
					node.get("errcode").asText(), node.get("errmsg").asText() });
		return new CorpWechatMenu(node.get("menu").toString());
	}

	public void delete(Integer agentid) throws IOException {
		String result = wechat.invoke("/menu/delete?agentid=" + agentid, null);
		JsonNode node = JsonUtils.fromJson(result, JsonNode.class);
		int errcode = node.get("errcode").asInt();
		if (errcode > 0)
			throw new ErrorMessage("errcode:{0},errmsg:{1}", new Object[] {
					node.get("errcode").asText(), node.get("errmsg").asText() });
	}

}
