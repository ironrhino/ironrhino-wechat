package org.ironrhino.wechat.model;

import java.io.Serializable;
import java.util.List;

import org.ironrhino.wechat.model.WechatNewsMessage.Article;

public class WechatNewsList implements Serializable {

	private static final long serialVersionUID = 2658037597334634022L;

	private int total_count;

	private int item_count;

	private List<WechatNews> item;

	public int getTotal_count() {
		return total_count;
	}

	public void setTotal_count(int total_count) {
		this.total_count = total_count;
	}

	public int getItem_count() {
		return item_count;
	}

	public void setItem_count(int item_count) {
		this.item_count = item_count;
	}

	public List<WechatNews> getItem() {
		return item;
	}

	public void setItem(List<WechatNews> item) {
		this.item = item;
	}

	public static class WechatNews implements Serializable {

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

	public static class WechatNewsContent implements Serializable {

		private static final long serialVersionUID = -2261099758792346680L;

		private List<Article> news_item;

		public List<Article> getNews_item() {
			return news_item;
		}

		public void setNews_item(List<Article> news_item) {
			this.news_item = news_item;
		}

	}

}
