package org.ironrhino.wechat.model;

import java.io.Serializable;
import java.util.List;

public class WechatMessage implements Serializable {

	private static final long serialVersionUID = -7515159900178523381L;

	public static final int CONTENT_MAX_BYTES = 2048;

	private String touser;
	private WechatMessageType msgtype;
	private String content;
	private String media_id;
	private String title;
	private String description;
	private String musicurl;
	private String hqmusicurl;
	private String thumb_media_id;
	private List<WechatArticle> articles;

	public WechatMessage() {

	}

	public WechatMessage(String touser, String content) {
		this.touser = touser;
		this.content = content;
		this.msgtype = WechatMessageType.text;
	}

	public String getTouser() {
		return touser;
	}

	public void setTouser(String touser) {
		this.touser = touser;
	}

	public WechatMessageType getMsgtype() {
		return msgtype;
	}

	public void setMsgtype(WechatMessageType msgtype) {
		this.msgtype = msgtype;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getMedia_id() {
		return media_id;
	}

	public void setMedia_id(String media_id) {
		this.media_id = media_id;
	}

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

	public String getMusicurl() {
		return musicurl;
	}

	public void setMusicurl(String musicurl) {
		this.musicurl = musicurl;
	}

	public String getHqmusicurl() {
		return hqmusicurl;
	}

	public void setHqmusicurl(String hqmusicurl) {
		this.hqmusicurl = hqmusicurl;
	}

	public String getThumb_media_id() {
		return thumb_media_id;
	}

	public void setThumb_media_id(String thumb_media_id) {
		this.thumb_media_id = thumb_media_id;
	}

	public List<WechatArticle> getArticles() {
		return articles;
	}

	public void setArticles(List<WechatArticle> articles) {
		this.articles = articles;
	}

	@Override
	public String toString() {
		if (msgtype == null)
			return "";
		return msgtype.toJson(this);
	}

}
