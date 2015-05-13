package org.ironrhino.corpwechat.support;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ironrhino.core.util.ErrorMessage;
import org.ironrhino.core.util.JsonUtils;
import org.ironrhino.corpwechat.model.CorpWechatUser;
import org.ironrhino.corpwechat.service.CorpWechat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;

@Component
public class CorpWechatUserControl {

	@Autowired
	private CorpWechat wechat;

	public void create(CorpWechatUser user) throws IOException {
		String request = JsonUtils.toJson(user);
		String result = wechat.invoke("/user/create", request);
		JsonNode node = JsonUtils.fromJson(result, JsonNode.class);
		int errcode = node.get("errcode").asInt();
		if (errcode != 0)
			throw new ErrorMessage("errcode:{0},errmsg:{1}", new Object[] {
					node.get("errcode").asText(), node.get("errmsg").asText() });
	}

	public void update(CorpWechatUser user) throws IOException {
		String request = JsonUtils.toJson(user);
		String result = wechat.invoke("/user/update", request);
		JsonNode node = JsonUtils.fromJson(result, JsonNode.class);
		int errcode = node.get("errcode").asInt();
		if (errcode != 0)
			throw new ErrorMessage("errcode:{0},errmsg:{1}", new Object[] {
					node.get("errcode").asText(), node.get("errmsg").asText() });
	}

	public void delete(String userid) throws IOException {
		String result = wechat.invoke("/user/delete?userid=" + userid, null);
		JsonNode node = JsonUtils.fromJson(result, JsonNode.class);
		int errcode = node.get("errcode").asInt();
		if (errcode != 0)
			throw new ErrorMessage("errcode:{0},errmsg:{1}", new Object[] {
					node.get("errcode").asText(), node.get("errmsg").asText() });
	}

	public void batchDelete(List<String> userids) throws IOException {
		Map<String, List<String>> map = new HashMap<>();
		map.put("useridlist", userids);
		String request = JsonUtils.toJson(map);
		String result = wechat.invoke("/user/batchdelete", request);
		JsonNode node = JsonUtils.fromJson(result, JsonNode.class);
		int errcode = node.get("errcode").asInt();
		if (errcode != 0)
			throw new ErrorMessage("errcode:{0},errmsg:{1}", new Object[] {
					node.get("errcode").asText(), node.get("errmsg").asText() });
	}

	public CorpWechatUser get(String userid) throws IOException {
		String result = wechat.invoke("/user/get?userid=" + userid, null);
		JsonNode node = JsonUtils.fromJson(result, JsonNode.class);
		int errcode = node.get("errcode").asInt();
		if (errcode != 0)
			throw new ErrorMessage("errcode:{0},errmsg:{1}", new Object[] {
					node.get("errcode").asText(), node.get("errmsg").asText() });
		return JsonUtils.fromJson(result, CorpWechatUser.class);
	}

	public List<CorpWechatUser> list(Integer department_id,
			boolean fetch_detail, boolean fetch_child, Integer[] status)
			throws IOException {
		StringBuilder sb = new StringBuilder("/user/");
		sb.append(fetch_detail ? "list" : "simplelist");
		sb.append("?department_id=");
		sb.append(department_id);
		if (fetch_child)
			sb.append("&fetch_child=1");
		if (status != null)
			for (Integer st : status)
				sb.append("&status=" + st);
		String result = wechat.invoke(sb.toString(), null);
		JsonNode node = JsonUtils.fromJson(result, JsonNode.class);
		int errcode = node.get("errcode").asInt();
		if (errcode != 0)
			throw new ErrorMessage("errcode:{0},errmsg:{1}", new Object[] {
					node.get("errcode").asText(), node.get("errmsg").asText() });
		return JsonUtils.fromJson(JsonUtils.toJson(node.get("userlist")),
				new TypeReference<List<CorpWechatUser>>() {
				});
	}

	public int invite(String userid, String invite_tips) throws IOException {
		Map<String, String> map = new HashMap<>();
		map.put("userid", userid);
		map.put("invite_tips", invite_tips);
		String result = wechat.invoke("/invite/send", JsonUtils.toJson(map));
		JsonNode node = JsonUtils.fromJson(result, JsonNode.class);
		int errcode = node.get("errcode").asInt();
		if (errcode != 0)
			throw new ErrorMessage("errcode:{0},errmsg:{1}", new Object[] {
					node.get("errcode").asText(), node.get("errmsg").asText() });
		return node.get("type").asInt();
	}

}
