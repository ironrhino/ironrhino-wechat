package org.ironrhino.wechat.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.ironrhino.core.util.JsonUtils;

public class WechatNewsMessage implements Serializable {

	private static final long serialVersionUID = 5187632158323302008L;

	private List<Article> articles = new ArrayList<>();

	public List<Article> getArticles() {
		return articles;
	}

	public void setArticles(List<Article> articles) {
		this.articles = articles;
	}

	@Override
	public String toString() {
		return JsonUtils.toJson(this);
	}

	public static class Article implements Serializable {

		private static final long serialVersionUID = 1693245064834739596L;

		private String thumb_media_id;
		private String author;
		private String title;
		private String content_source_url;
		private String content;
		private String digest;
		private String show_cover_pic;

		public String getThumb_media_id() {
			return thumb_media_id;
		}

		public void setThumb_media_id(String thumb_media_id) {
			this.thumb_media_id = thumb_media_id;
		}

		public String getAuthor() {
			return author;
		}

		public void setAuthor(String author) {
			this.author = author;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getContent_source_url() {
			return content_source_url;
		}

		public void setContent_source_url(String content_source_url) {
			this.content_source_url = content_source_url;
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}

		public String getDigest() {
			return digest;
		}

		public void setDigest(String digest) {
			this.digest = digest;
		}

		public String getShow_cover_pic() {
			return show_cover_pic;
		}

		public void setShow_cover_pic(String show_cover_pic) {
			this.show_cover_pic = show_cover_pic;
		}

		@Override
		public String toString() {
			return JsonUtils.toJson(this);
		}
	}

}
