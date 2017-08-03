package org.ironrhino.wechat.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class WechatArticle implements Serializable {

	private static final long serialVersionUID = -8559241258591348881L;

	private String title;
	private String description;
	private String picurl;
	private String url;

}