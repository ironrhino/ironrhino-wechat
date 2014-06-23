package org.ironrhino.wechat.support;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.ironrhino.core.util.ErrorMessage;
import org.ironrhino.core.util.JsonUtils;
import org.ironrhino.wechat.model.WechatGroup;
import org.ironrhino.wechat.service.Wechat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;

@Component
public class WechatGroupControl {

	@Autowired
	private Wechat wechat;

	public WechatGroup create(String name) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("{\"group\":{\"name\":\"");
		sb.append(name);
		sb.append("\"}}");
		String request = sb.toString();
		String result = wechat.invoke("/groups/create", request);
		JsonNode node = JsonUtils.fromJson(result, JsonNode.class);
		if (node.has("errcode"))
			throw new ErrorMessage("errcode:{0},errmsg:{1}", new Object[] {
					node.get("errcode").asText(), node.get("errmsg").asText() });
		int id = node.get("group").get("id").asInt();
		WechatGroup group = new WechatGroup();
		group.setId(id);
		group.setName(name);
		return group;
	}

	public void rename(int id, String name) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("{\"group\":{\"id\":");
		sb.append(id);
		sb.append(",\"name\":\"");
		sb.append(name);
		sb.append("\"}}");
		String request = sb.toString();
		String result = wechat.invoke("/groups/update", request);
		JsonNode node = JsonUtils.fromJson(result, JsonNode.class);
		if (node.has("errcode"))
			throw new ErrorMessage("errcode:{0},errmsg:{1}", new Object[] {
					node.get("errcode").asText(), node.get("errmsg").asText() });
	}

	public List<WechatGroup> get() throws Exception {
		String result = wechat.invoke("/groups/get", null);
		JsonNode node = JsonUtils.fromJson(result, JsonNode.class);
		if (node.has("errcode"))
			throw new ErrorMessage("errcode:{0},errmsg:{1}", new Object[] {
					node.get("errcode").asText(), node.get("errmsg").asText() });
		JsonNode groups = node.get("groups");
		int size = groups.size();
		List<WechatGroup> list = new ArrayList<>(size);
		Iterator<JsonNode> it = groups.iterator();
		while (it.hasNext()) {
			JsonNode jn = it.next();
			WechatGroup group = new WechatGroup();
			group.setId(jn.get("id").asInt());
			group.setName(jn.get("name").asText());
			group.setCount(jn.get("count").asInt());
			list.add(group);
		}
		return list;
	}

	public int getGroupId(String openid) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("{\"openid\":\"");
		sb.append(openid);
		sb.append("\"}");
		String request = sb.toString();
		String result = wechat.invoke("/groups/getid", request);
		JsonNode node = JsonUtils.fromJson(result, JsonNode.class);
		if (node.has("errcode"))
			throw new ErrorMessage("errcode:{0},errmsg:{1}", new Object[] {
					node.get("errcode").asText(), node.get("errmsg").asText() });
		return node.get("groupid").asInt();
	}

	public void move(String openid, String toGroupId) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("{\"openid\":\"");
		sb.append(openid);
		sb.append("\",\"to_groupid\":");
		sb.append(toGroupId);
		sb.append("}");
		String request = sb.toString();
		String result = wechat.invoke("/groups/members/update", request);
		JsonNode node = JsonUtils.fromJson(result, JsonNode.class);
		if (node.has("errcode"))
			throw new ErrorMessage("errcode:{0},errmsg:{1}", new Object[] {
					node.get("errcode").asText(), node.get("errmsg").asText() });
	}

}
