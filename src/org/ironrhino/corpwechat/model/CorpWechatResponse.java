package org.ironrhino.corpwechat.model;

import java.io.Serializable;
import java.util.List;

public class CorpWechatResponse implements Serializable {

	private static final long serialVersionUID = -7515159900178523381L;

	public static final int CONTENT_MAX_BYTES = 2048;

	public static final CorpWechatResponse EMPTY = new CorpWechatResponse();

	private String toUserName;
	private String fromUserName;
	private long createTime;
	private CorpWechatResponseType msgType;
	private String content;
	private String mediaId;
	private String title;
	private String description;
	private String musicUrl;
	private String HQMusicUrl;
	private String thumbMediaId;
	private List<CorpWechatArticle> articles;

	public String getToUserName() {
		return toUserName;
	}

	public void setToUserName(String toUserName) {
		this.toUserName = toUserName;
	}

	public String getFromUserName() {
		return fromUserName;
	}

	public void setFromUserName(String fromUserName) {
		this.fromUserName = fromUserName;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public CorpWechatResponseType getMsgType() {
		return msgType;
	}

	public void setMsgType(CorpWechatResponseType msgType) {
		this.msgType = msgType;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getMediaId() {
		return mediaId;
	}

	public void setMediaId(String mediaId) {
		this.mediaId = mediaId;
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

	public String getMusicUrl() {
		return musicUrl;
	}

	public void setMusicUrl(String musicUrl) {
		this.musicUrl = musicUrl;
	}

	public String getHQMusicUrl() {
		return HQMusicUrl;
	}

	public void setHQMusicUrl(String hQMusicUrl) {
		HQMusicUrl = hQMusicUrl;
	}

	public String getThumbMediaId() {
		return thumbMediaId;
	}

	public void setThumbMediaId(String thumbMediaId) {
		this.thumbMediaId = thumbMediaId;
	}

	public List<CorpWechatArticle> getArticles() {
		return articles;
	}

	public void setArticles(List<CorpWechatArticle> articles) {
		this.articles = articles;
	}

	public static CorpWechatResponse replyTo(CorpWechatRequest request,
			String content) {
		CorpWechatResponse response = new CorpWechatResponse();
		response.setMsgType(CorpWechatResponseType.text);
		response.setToUserName(request.getFromUserName());
		response.setFromUserName(request.getToUserName());
		response.setCreateTime(System.currentTimeMillis() / 1000);
		response.setContent(content);
		return response;
	}

	@Override
	public String toString() {
		if (msgType == null)
			return "";
		return msgType.toXml(this);
	}

}
