package org.ironrhino.wechat.model;

import java.io.Serializable;
import java.util.List;

import org.ironrhino.core.util.JsonUtils;

import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class WechatAllMessage implements Serializable {

	private static final long serialVersionUID = 5187632158323302008L;

	private Filter filter;
	private List<String> touser;
	private WechatMessageType msgtype;
	private String content;
	private String media_id;

	@Override
	public String toString() {
		ObjectNode on = JsonUtils.getObjectMapper().createObjectNode();
		if (filter != null)
			on.putPOJO("filter", filter);
		else if (touser != null && !touser.isEmpty())
			on.putPOJO("touser", touser);
		String type = msgtype.name();
		if (type.equals("news"))
			type = "mpnews";
		else if (type.equals("video"))
			type = "mpvideo";
		on.put("msgtype", type);
		ObjectNode msg = on.putObject(type);
		if (msgtype == WechatMessageType.text)
			msg.put("content", content);
		else
			msg.put("media_id", media_id);
		return JsonUtils.toJson(on);
	}

	@Data
	@NoArgsConstructor
	public static class Filter implements Serializable {

		private static final long serialVersionUID = -2303910638152884237L;

		private boolean is_to_all;
		private String group_id;

		public Filter(boolean is_to_all, String group_id) {
			this.is_to_all = is_to_all;
			this.group_id = group_id;
		}

		@Override
		public String toString() {
			return JsonUtils.toJson(this);
		}
	}

}
