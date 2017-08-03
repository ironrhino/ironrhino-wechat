package org.ironrhino.wechat.model;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class WechatNewsContent implements Serializable {

	private static final long serialVersionUID = -2261099758792346680L;

	private List<WechatNewsArticle> news_item;

}