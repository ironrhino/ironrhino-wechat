package org.ironrhino.corpwechat.support;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ironrhino.core.util.ErrorMessage;
import org.ironrhino.core.util.JsonUtils;
import org.ironrhino.corpwechat.model.CorpWechatTag;
import org.ironrhino.corpwechat.model.CorpWechatUser;
import org.ironrhino.corpwechat.service.CorpWechat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;

@Component
@Retryable(include = IOException.class, backoff = @Backoff(delay = 1000, maxDelay = 5000, multiplier = 2))
public class CorpWechatTagControl {

	@Autowired
	private CorpWechat wechat;

	public CorpWechatTag create(String tagname) throws IOException {
		CorpWechatTag tag = new CorpWechatTag();
		tag.setTagname(tagname);
		String request = JsonUtils.toJson(tag);
		String result = wechat.invoke("/tag/create", request);
		JsonNode node = JsonUtils.fromJson(result, JsonNode.class);
		int errcode = node.get("errcode").asInt();
		if (errcode != 0)
			throw new ErrorMessage("errcode:{0},errmsg:{1}", new Object[] {
					node.get("errcode").asText(), node.get("errmsg").asText() });
		int id = node.get("tagid").asInt();
		tag.setTagid(id);
		return tag;
	}

	public void rename(Integer tagid, String tagname) throws IOException {
		CorpWechatTag tag = new CorpWechatTag();
		tag.setTagid(tagid);
		tag.setTagname(tagname);
		String request = JsonUtils.toJson(tag);
		String result = wechat.invoke("/tag/update", request);
		JsonNode node = JsonUtils.fromJson(result, JsonNode.class);
		int errcode = node.get("errcode").asInt();
		if (errcode != 0)
			throw new ErrorMessage("errcode:{0},errmsg:{1}", new Object[] {
					node.get("errcode").asText(), node.get("errmsg").asText() });
	}

	public void delete(Integer tagid) throws IOException {
		String result = wechat.invoke("/tag/delete?tagid=" + tagid, null);
		JsonNode node = JsonUtils.fromJson(result, JsonNode.class);
		int errcode = node.get("errcode").asInt();
		if (errcode != 0)
			throw new ErrorMessage("errcode:{0},errmsg:{1}", new Object[] {
					node.get("errcode").asText(), node.get("errmsg").asText() });
	}

	public List<CorpWechatTag> list() throws IOException {
		String result = wechat.invoke("/tag/list", null);
		JsonNode node = JsonUtils.fromJson(result, JsonNode.class);
		int errcode = node.get("errcode").asInt();
		if (errcode != 0)
			throw new ErrorMessage("errcode:{0},errmsg:{1}", new Object[] {
					node.get("errcode").asText(), node.get("errmsg").asText() });
		return JsonUtils.fromJson(node.get("taglist").toString(),
				new TypeReference<List<CorpWechatTag>>() {
				});
	}

	public List<CorpWechatUser> getTagUsers(Integer tagid) throws IOException {
		String result = wechat.invoke("/tag/get?tagid=" + tagid, null);
		JsonNode node = JsonUtils.fromJson(result, JsonNode.class);
		int errcode = node.get("errcode").asInt();
		if (errcode != 0)
			throw new ErrorMessage("errcode:{0},errmsg:{1}", new Object[] {
					node.get("errcode").asText(), node.get("errmsg").asText() });
		return JsonUtils.fromJson(node.get("userlist").toString(),
				new TypeReference<List<CorpWechatUser>>() {
				});
	}

	public void addTagUsers(Integer tagid, List<String> userlist,
			List<Integer> partylist) throws IOException {
		Map<String, Object> map = new HashMap<>();
		map.put("tagid", tagid);
		map.put("userlist", userlist);
		map.put("partylist", partylist);
		String result = wechat
				.invoke("/tag/addtagusers", JsonUtils.toJson(map));
		JsonNode node = JsonUtils.fromJson(result, JsonNode.class);
		int errcode = node.get("errcode").asInt();
		if (errcode != 0)
			throw new ErrorMessage("errcode:{0},errmsg:{1}", new Object[] {
					node.get("errcode").asText(), node.get("errmsg").asText() });
	}

	public void deleteTagUsers(Integer tagid, List<String> userlist,
			List<Integer> partylist) throws IOException {
		Map<String, Object> map = new HashMap<>();
		map.put("tagid", tagid);
		map.put("userlist", userlist);
		map.put("partylist", partylist);
		String result = wechat
				.invoke("/tag/deltagusers", JsonUtils.toJson(map));
		JsonNode node = JsonUtils.fromJson(result, JsonNode.class);
		int errcode = node.get("errcode").asInt();
		if (errcode != 0)
			throw new ErrorMessage("errcode:{0},errmsg:{1}", new Object[] {
					node.get("errcode").asText(), node.get("errmsg").asText() });
	}
}
