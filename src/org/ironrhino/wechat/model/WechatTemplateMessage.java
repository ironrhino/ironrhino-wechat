package org.ironrhino.wechat.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.ironrhino.core.util.JsonUtils;

public class WechatTemplateMessage implements Serializable {

	private static final long serialVersionUID = 5187632158323302008L;

	private String touser;
	private String template_id;
	private String url;
	private String topcolor;
	private Map<String, Data> data = new HashMap<>();

	public String getTouser() {
		return touser;
	}

	public void setTouser(String touser) {
		this.touser = touser;
	}

	public String getTemplate_id() {
		return template_id;
	}

	public void setTemplate_id(String template_id) {
		this.template_id = template_id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTopcolor() {
		return topcolor;
	}

	public void setTopcolor(String topcolor) {
		this.topcolor = topcolor;
	}

	public Map<String, Data> getData() {
		return data;
	}

	public void setData(Map<String, Data> data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return JsonUtils.toJson(this);
	}

	public static class Data implements Serializable {

		private static final long serialVersionUID = -2303910638152884237L;

		private String value;
		private String color;

		public Data() {

		}

		public Data(String value, String color) {
			super();
			this.value = value;
			this.color = color;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public String getColor() {
			return color;
		}

		public void setColor(String color) {
			this.color = color;
		}

		@Override
		public String toString() {
			return JsonUtils.toJson(this);
		}
	}

}
