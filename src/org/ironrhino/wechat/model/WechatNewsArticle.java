package org.ironrhino.wechat.model;

import java.io.Serializable;

import org.ironrhino.core.util.JsonUtils;

import lombok.Data;

@Data
public class WechatNewsArticle implements Serializable {

	private static final long serialVersionUID = 1693245064834739596L;

	private String thumb_media_id;
	private String thumb_url;
	private String author;
	private String title;
	private String content_source_url;
	private String content;
	private String digest;
	private String show_cover_pic;

	@Override
	public String toString() {
		return JsonUtils.toJson(this);
	}
}