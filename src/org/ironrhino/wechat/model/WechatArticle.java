package org.ironrhino.wechat.model;

import java.io.Serializable;

public class WechatArticle implements Serializable {

	private static final long serialVersionUID = -8559241258591348881L;

	private String title;
	private String description;
	private String picurl;
	private String url;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPicurl() {
		return picurl;
	}

	public void setPicurl(String picurl) {
		this.picurl = picurl;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}