package org.ironrhino.wechat.model;

import java.io.Serializable;
import java.util.List;

import org.ironrhino.core.util.JsonUtils;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class WechatAllMessage implements Serializable {

	private static final long serialVersionUID = 5187632158323302008L;

	private Filter filter;
	private List<String> touser;
	private WechatMessageType msgtype;
	private String content;
	private String media_id;

	public Filter getFilter() {
		return filter;
	}

	public void setFilter(Filter filter) {
		this.filter = filter;
	}

	public List<String> getTouser() {
		return touser;
	}

	public void setTouser(List<String> touser) {
		this.touser = touser;
	}

	public WechatMessageType getMsgtype() {
		return msgtype;
	}

	public void setMsgtype(WechatMessageType msgtype) {
		this.msgtype = msgtype;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getMedia_id() {
		return media_id;
	}

	public void setMedia_id(String media_id) {
		this.media_id = media_id;
	}

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

	public static class Filter implements Serializable {

		private static final long serialVersionUID = -2303910638152884237L;

		private boolean is_to_all;
		private String group_id;

		public Filter() {

		}

		public Filter(boolean is_to_all, String group_id) {
			this.is_to_all = is_to_all;
			this.group_id = group_id;
		}

		public boolean isIs_to_all() {
			return is_to_all;
		}

		public void setIs_to_all(boolean is_to_all) {
			this.is_to_all = is_to_all;
		}

		public String getGroup_id() {
			return group_id;
		}

		public void setGroup_id(String group_id) {
			this.group_id = group_id;
		}

		@Override
		public String toString() {
			return JsonUtils.toJson(this);
		}
	}

}
