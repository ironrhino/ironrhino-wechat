package org.ironrhino.corpwechat.model;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class CorpWechatMessage implements Serializable {

	private static final long serialVersionUID = -7515159900178523381L;

	public static final int CONTENT_MAX_BYTES = 2048;

	private String touser;
	private String toparty;
	private String totag;
	private Integer agentid;
	private Integer safe;
	private CorpWechatMessageType msgtype;
	private String content;
	private String media_id;
	private String title;
	private String description;
	private String musicurl;
	private String hqmusicurl;
	private String thumb_media_id;
	private List<CorpWechatArticle> articles;

	@Override
	public String toString() {
		if (msgtype == null)
			return "";
		return msgtype.toJson(this);
	}

}
