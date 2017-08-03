package org.ironrhino.wechat.model;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class WechatResponse implements Serializable {

	private static final long serialVersionUID = -7515159900178523381L;

	public static final int CONTENT_MAX_BYTES = 2048;

	public static final WechatResponse EMPTY = new WechatResponse();

	private String toUserName;
	private String fromUserName;
	private long createTime;
	private WechatResponseType msgType;
	private String content;
	private String mediaId;
	private String title;
	private String description;
	private String musicUrl;
	private String HQMusicUrl;
	private String thumbMediaId;
	private List<WechatArticle> articles;
	private String kfAccount;

	public static WechatResponse replyTo(WechatRequest request, String content) {
		WechatResponse response = new WechatResponse();
		response.setMsgType(WechatResponseType.text);
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
