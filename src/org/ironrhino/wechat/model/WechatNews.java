package org.ironrhino.wechat.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class WechatNews implements Serializable {

	private static final long serialVersionUID = -6541948486891391341L;

	private String media_id;
	private WechatNewsContent content;
	private String update_time;

}