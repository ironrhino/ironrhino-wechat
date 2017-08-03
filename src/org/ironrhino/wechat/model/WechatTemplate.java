package org.ironrhino.wechat.model;

import java.io.Serializable;

import org.ironrhino.core.util.JsonUtils;

import lombok.Data;

@Data
public class WechatTemplate implements Serializable {

	private static final long serialVersionUID = -6193446815496996747L;

	private String template_id;
	private String title;
	private String primary_industry;
	private String deputy_industry;
	private String content;
	private String example;

	@Override
	public String toString() {
		return JsonUtils.toJson(this);
	}

}
