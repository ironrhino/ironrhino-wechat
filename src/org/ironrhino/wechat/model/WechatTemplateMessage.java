package org.ironrhino.wechat.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.ironrhino.core.util.JsonUtils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class WechatTemplateMessage implements Serializable {

	private static final long serialVersionUID = 5187632158323302008L;

	private String touser;
	private String template_id;
	private String url;
	private String topcolor;
	private Map<String, Data> data = new HashMap<>();

	@Override
	public String toString() {
		return JsonUtils.toJson(this);
	}

	@lombok.Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Data implements Serializable {

		private static final long serialVersionUID = -2303910638152884237L;

		private String value;
		private String color;

		@Override
		public String toString() {
			return JsonUtils.toJson(this);
		}
	}

}
