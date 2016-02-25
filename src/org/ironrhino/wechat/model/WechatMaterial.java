package org.ironrhino.wechat.model;

public class WechatMaterial extends WechatNews {

	private static final long serialVersionUID = -6541948486891391341L;

	private String name;
	private String url;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}