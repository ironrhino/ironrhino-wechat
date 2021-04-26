package org.ironrhino.wechat.model;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class WechatMessage implements Serializable {

	private static final long serialVersionUID = -7515159900178523381L;

	public static final int CONTENT_MAX_BYTES = 2048;

	private String touser;
	private WechatMessageType msgtype;
	private String content;
	private String media_id;
	private String card_id;
	private String title;
	private String description;
	private String musicurl;
	private String hqmusicurl;
	private String thumb_media_id;
	private String url;
	private String picurl;
	private String appid;
	private String pagepath;
	private String kf_account;
	private List<WechatArticle> articles;

	public WechatMessage(String touser, String content) {
		this.touser = touser;
		this.content = content;
		this.msgtype = WechatMessageType.text;
	}

	@Override
	public String toString() {
		if (msgtype == null)
			return "";
		return msgtype.toJson(this);
	}

}
