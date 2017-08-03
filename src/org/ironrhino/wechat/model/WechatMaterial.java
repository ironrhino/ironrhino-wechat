package org.ironrhino.wechat.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class WechatMaterial extends WechatNews {

	private static final long serialVersionUID = -6541948486891391341L;

	private String name;
	private String url;

}