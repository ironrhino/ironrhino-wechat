package org.ironrhino.corpwechat.support;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.ironrhino.core.util.ErrorMessage;
import org.ironrhino.core.util.JsonUtils;
import org.ironrhino.corpwechat.model.WechatDepartment;
import org.ironrhino.corpwechat.service.CorpWechat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;

@Component
public class WechatDepartmentControl {

	@Autowired
	private CorpWechat wechat;

	public WechatDepartment create(WechatDepartment department)
			throws IOException {
		String request = JsonUtils.toJson(department);
		String result = wechat.invoke("/department/create", request);
		JsonNode node = JsonUtils.fromJson(result, JsonNode.class);
		int errcode = node.get("errcode").asInt();
		if (errcode != 0)
			throw new ErrorMessage("errcode:{0},errmsg:{1}", new Object[] {
					node.get("errcode").asText(), node.get("errmsg").asText() });
		int id = node.get("id").asInt();
		department.setId(id);
		return department;
	}

	public void update(WechatDepartment department) throws IOException {
		String request = JsonUtils.toJson(department);
		String result = wechat.invoke("/department/update", request);
		JsonNode node = JsonUtils.fromJson(result, JsonNode.class);
		int errcode = node.get("errcode").asInt();
		if (errcode != 0)
			throw new ErrorMessage("errcode:{0},errmsg:{1}", new Object[] {
					node.get("errcode").asText(), node.get("errmsg").asText() });
	}

	public void delete(Integer id) throws IOException {
		String result = wechat.invoke("/department/delete?id=" + id, null);
		JsonNode node = JsonUtils.fromJson(result, JsonNode.class);
		int errcode = node.get("errcode").asInt();
		if (errcode != 0)
			throw new ErrorMessage("errcode:{0},errmsg:{1}", new Object[] {
					node.get("errcode").asText(), node.get("errmsg").asText() });
	}

	public List<WechatDepartment> list(Integer id) throws IOException {
		String url = "/department/list";
		if (id != null)
			url += "?id=" + id;
		String result = wechat.invoke(url, null);
		JsonNode node = JsonUtils.fromJson(result, JsonNode.class);
		int errcode = node.get("errcode").asInt();
		if (errcode != 0)
			throw new ErrorMessage("errcode:{0},errmsg:{1}", new Object[] {
					node.get("errcode").asText(), node.get("errmsg").asText() });
		JsonNode departments = node.get("department");
		int size = departments.size();
		List<WechatDepartment> list = new ArrayList<>(size);
		Iterator<JsonNode> it = departments.iterator();
		while (it.hasNext()) {
			JsonNode jn = it.next();
			WechatDepartment department = new WechatDepartment();
			department.setId(jn.get("id").asInt());
			department.setName(jn.get("name").asText());
			if (jn.has("parentid"))
				department.setParentid(jn.get("parentid").asInt());
			if (jn.has("order"))
				department.setOrder(jn.get("order").asInt());
			list.add(department);
		}
		return list;
	}

}
