package org.ironrhino.wechat.support;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.ironrhino.core.util.ErrorMessage;
import org.ironrhino.core.util.JsonUtils;
import org.ironrhino.wechat.model.WechatUser;
import org.ironrhino.wechat.service.Wechat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;

@Component
public class WechatUserControl {

	@Autowired
	private Wechat wechat;

	public WechatUser get(String openid) throws Exception {
		String result = wechat.invoke("/user/info?lang=zh_CN&openid=" + openid,
				null);
		JsonNode node = JsonUtils.fromJson(result, JsonNode.class);
		if (node.has("errcode"))
			throw new ErrorMessage("errcode:{0},errmsg:{1}", new Object[] {
					node.get("errcode").asText(), node.get("errmsg").asText() });
		return new WechatUser(result);
	}

	public List<String> list() throws Exception {
		List<String> list = new ArrayList<String>();
		String result = wechat.invoke("/user/get", null);
		JsonNode node = JsonUtils.fromJson(result, JsonNode.class);
		if (node.has("errcode"))
			throw new ErrorMessage("errcode:{0},errmsg:{1}", new Object[] {
					node.get("errcode").asText(), node.get("errmsg").asText() });
		Iterator<JsonNode> it = node.get("data").get("openid").iterator();
		while (it.hasNext())
			list.add(it.next().textValue());
		String nextopenid;
		if (node.has("next_openid"))
			nextopenid = node.get("next_openid").textValue();
		else
			nextopenid = null;
		while (StringUtils.isNotBlank(nextopenid)) {
			result = wechat.invoke("/user/get?next_openid=" + nextopenid, null);
			node = JsonUtils.fromJson(result, JsonNode.class);
			it = node.get("data").get("openid").iterator();
			while (it.hasNext())
				list.add(it.next().textValue());
			if (node.has("next_openid"))
				nextopenid = node.get("next_openid").textValue();
			else
				nextopenid = null;
		}
		return list;
	}

}
