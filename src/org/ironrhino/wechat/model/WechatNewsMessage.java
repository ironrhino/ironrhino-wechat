package org.ironrhino.wechat.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.ironrhino.core.util.JsonUtils;

import lombok.Data;

@Data
public class WechatNewsMessage implements Serializable {

	private static final long serialVersionUID = 5187632158323302008L;

	private List<WechatNewsArticle> articles = new ArrayList<>();

	@Override
	public String toString() {
		return JsonUtils.toJson(this);
	}

}
