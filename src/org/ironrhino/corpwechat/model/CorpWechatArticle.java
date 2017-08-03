package org.ironrhino.corpwechat.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class CorpWechatArticle implements Serializable {

	private static final long serialVersionUID = -8559241258591348881L;

	private String title;
	private String description;
	private String picurl;
	private String url;
	private String thumb_media_id;
	private String author;
	private String content_source_url;
	private String content;
	private String digest;
	private Integer show_cover_pic;

}