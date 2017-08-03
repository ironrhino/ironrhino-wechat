package org.ironrhino.wechat.model;

import java.io.Serializable;

import org.ironrhino.core.util.JsonUtils;
import org.springframework.beans.BeanUtils;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class WechatConditionalMenu extends WechatMenu {

	private static final long serialVersionUID = -5302441383748619250L;

	@Getter
	@Setter
	private MatchRule matchrule;

	public WechatConditionalMenu(String json) {
		try {
			WechatConditionalMenu wm = JsonUtils.fromJson(json, WechatConditionalMenu.class);
			BeanUtils.copyProperties(wm, this);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	@Data
	public static class MatchRule implements Serializable {

		private static final long serialVersionUID = 3600533330902295160L;

		private String tag_id;
		private String sex;
		private String country;
		private String province;
		private String city;
		private String client_platform_type;
		private String language;

	}

}
