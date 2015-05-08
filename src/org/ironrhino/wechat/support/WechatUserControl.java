package org.ironrhino.wechat.support;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.ironrhino.core.util.ErrorMessage;
import org.ironrhino.core.util.JsonUtils;
import org.ironrhino.wechat.model.WechatUser;
import org.ironrhino.wechat.model.WechatUserList;
import org.ironrhino.wechat.service.Wechat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;

@Component
public class WechatUserControl {

	@Autowired
	private Wechat wechat;

	public WechatUser get(String openid) throws IOException {
		String result = wechat.invoke("/user/info?lang=zh_CN&openid=" + openid,
				null);
		JsonNode node = JsonUtils.fromJson(result, JsonNode.class);
		if (node.has("errcode"))
			throw new ErrorMessage("errcode:{0},errmsg:{1}", new Object[] {
					node.get("errcode").asText(), node.get("errmsg").asText() });
		return new WechatUser(result);
	}

	public List<String> list() throws IOException {
		WechatUserList wul = list(null);
		if (wul.getTotal() == wul.getOpenids().size())
			return wul.getOpenids();
		List<String> list = new ArrayList<String>();
		list.addAll(wul.getOpenids());
		while (StringUtils.isNotBlank(wul.getNext_openid())) {
			wul = list(wul.getNext_openid());
			list.addAll(wul.getOpenids());
		}
		return list;
	}

	public WechatUserList list(String next_openid) throws IOException {
		String uri = "/user/get";
		if (StringUtils.isNotBlank(next_openid))
			uri += "?next_openid=" + next_openid;
		String result = wechat.invoke(uri, null);
		JsonNode node = JsonUtils.fromJson(result, JsonNode.class);
		if (node.has("errcode"))
			throw new ErrorMessage("errcode:{0},errmsg:{1}", new Object[] {
					node.get("errcode").asText(), node.get("errmsg").asText() });
		WechatUserList wul = new WechatUserList();
		int total = node.get("total").asInt();
		int count = node.get("count").asInt();
		if (count == 0)
			return wul;
		next_openid = node.get("next_openid").asText();
		List<String> list = new ArrayList<String>();
		Iterator<JsonNode> it = node.get("data").get("openid").iterator();
		while (it.hasNext())
			list.add(it.next().textValue());
		wul.setTotal(total);
		wul.setOpenids(list);
		wul.setNext_openid(next_openid);
		return wul;
	}
}
