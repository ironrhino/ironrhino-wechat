package org.ironrhino.corpwechat.support;

import java.io.IOException;
import java.util.List;

import org.ironrhino.core.util.ErrorMessage;
import org.ironrhino.core.util.JsonUtils;
import org.ironrhino.corpwechat.model.CorpWechatDepartment;
import org.ironrhino.corpwechat.service.CorpWechat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;

@Component
public class CorpWechatDepartmentControl {

	@Autowired
	private CorpWechat wechat;

	public CorpWechatDepartment create(CorpWechatDepartment department)
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

	public void update(CorpWechatDepartment department) throws IOException {
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

	public List<CorpWechatDepartment> list(Integer id) throws IOException {
		String url = "/department/list";
		if (id != null)
			url += "?id=" + id;
		String result = wechat.invoke(url, null);
		JsonNode node = JsonUtils.fromJson(result, JsonNode.class);
		int errcode = node.get("errcode").asInt();
		if (errcode != 0)
			throw new ErrorMessage("errcode:{0},errmsg:{1}", new Object[] {
					node.get("errcode").asText(), node.get("errmsg").asText() });
		return JsonUtils.fromJson(JsonUtils.toJson(node.get("department")),
				new TypeReference<List<CorpWechatDepartment>>() {
				});
	}

}
