package org.ironrhino.wechat.model;

import java.io.Serializable;

public class WechatNews implements Serializable {

	private static final long serialVersionUID = -6541948486891391341L;

	private String media_id;
	private WechatNewsContent content;
	private String update_time;

	public String getMedia_id() {
		return media_id;
	}

	public void setMedia_id(String media_id) {
		this.media_id = media_id;
	}

	public WechatNewsContent getContent() {
		return content;
	}

	public void setContent(WechatNewsContent content) {
		this.content = content;
	}

	public String getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(String update_time) {
		this.update_time = update_time;
	}

}