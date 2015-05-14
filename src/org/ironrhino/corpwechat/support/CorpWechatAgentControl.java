package org.ironrhino.corpwechat.support;

import java.io.IOException;
import java.util.List;

import org.ironrhino.core.util.ErrorMessage;
import org.ironrhino.core.util.JsonUtils;
import org.ironrhino.corpwechat.model.CorpWechatAgent;
import org.ironrhino.corpwechat.service.CorpWechat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;

@Component
@Retryable(include = IOException.class, backoff = @Backoff(delay = 1000, maxDelay = 5000, multiplier = 2))
public class CorpWechatAgentControl {

	@Autowired
	private CorpWechat wechat;

	public CorpWechatAgent get(Integer agentid) throws IOException {
		String result = wechat.invoke("/agent/get?agentid=" + agentid, null);
		JsonNode node = JsonUtils.fromJson(result, JsonNode.class);
		int errcode = node.get("errcode").asInt();
		if (errcode != 0)
			throw new ErrorMessage("errcode:{0},errmsg:{1}", new Object[] {
					node.get("errcode").asText(), node.get("errmsg").asText() });
		return JsonUtils.fromJson(result, CorpWechatAgent.class);
	}

	public void set(CorpWechatAgent agent) throws IOException {
		String request = JsonUtils.toJson(agent);
		String result = wechat.invoke("/agent/create", request);
		JsonNode node = JsonUtils.fromJson(result, JsonNode.class);
		int errcode = node.get("errcode").asInt();
		if (errcode != 0)
			throw new ErrorMessage("errcode:{0},errmsg:{1}", new Object[] {
					node.get("errcode").asText(), node.get("errmsg").asText() });
	}

	public List<CorpWechatAgent> list() throws IOException {
		String result = wechat.invoke("/agent/list", null);
		JsonNode node = JsonUtils.fromJson(result, JsonNode.class);
		int errcode = node.get("errcode").asInt();
		if (errcode != 0)
			throw new ErrorMessage("errcode:{0},errmsg:{1}", new Object[] {
					node.get("errcode").asText(), node.get("errmsg").asText() });
		return JsonUtils.fromJson(node.get("agentlist").toString(),
				new TypeReference<List<CorpWechatAgent>>() {
				});
	}

}
