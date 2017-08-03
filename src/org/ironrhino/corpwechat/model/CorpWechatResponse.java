package org.ironrhino.corpwechat.model;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
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

	public static CorpWechatResponse replyTo(CorpWechatRequest request, String content) {
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
