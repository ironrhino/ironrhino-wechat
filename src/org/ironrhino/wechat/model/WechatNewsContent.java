package org.ironrhino.wechat.model;

import java.io.Serializable;
import java.util.List;

public class WechatNewsContent implements Serializable {

	private static final long serialVersionUID = -2261099758792346680L;

	private List<WechatNewsArticle> news_item;

	public List<WechatNewsArticle> getNews_item() {
		return news_item;
	}

	public void setNews_item(List<WechatNewsArticle> news_item) {
		this.news_item = news_item;
	}

}