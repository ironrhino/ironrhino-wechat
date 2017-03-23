package org.ironrhino.wechat.support;

import java.io.IOException;
import java.util.List;

import org.ironrhino.core.util.ErrorMessage;
import org.ironrhino.core.util.JsonUtils;
import org.ironrhino.wechat.model.WechatTemplate;
import org.ironrhino.wechat.service.Wechat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;

@Component
@Retryable(include = IOException.class, backoff = @Backoff(delay = 1000, maxDelay = 5000, multiplier = 2))
public class WechatTemplateControl {

	@Autowired
	private Wechat wechat;

	public List<WechatTemplate> list() throws IOException {
		String result = wechat.invoke("/template/get_all_private_template", null);
		JsonNode node = JsonUtils.fromJson(result, JsonNode.class);
		if (node.has("errcode"))
			throw new ErrorMessage("errcode:{0},errmsg:{1}",
					new Object[] { node.get("errcode").asText(), node.get("errmsg").asText() });
		return JsonUtils.fromJson(node.get("template_list").toString(), new TypeReference<List<WechatTemplate>>() {
		});
	}

	public String add(String template_id_short) throws IOException {
		String result = wechat.invoke("/template/api_add_template",
				"{\"template_id_short\":\"" + template_id_short + "\"}");
		JsonNode node = JsonUtils.fromJson(result, JsonNode.class);
		if (node.has("errcode") && node.get("errcode").asInt() > 0)
			throw new ErrorMessage("errcode:{0},errmsg:{1}",
					new Object[] { node.get("errcode").asText(), node.get("errmsg").asText() });
		return node.get("template_id").asText();
	}

	public void delete(String template_id) throws IOException {
		String result = wechat.invoke("/template/del_private_template", "{\"template_id\":\"" + template_id + "\"}");
		JsonNode node = JsonUtils.fromJson(result, JsonNode.class);
		if (node.has("errcode") && node.get("errcode").asInt() > 0)
			throw new ErrorMessage("errcode:{0},errmsg:{1}",
					new Object[] { node.get("errcode").asText(), node.get("errmsg").asText() });
	}
}
