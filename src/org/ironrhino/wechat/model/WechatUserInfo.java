package org.ironrhino.wechat.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class WechatUserInfo implements Serializable {

	private static final long serialVersionUID = 6846281527791363672L;

	private String access_token;

	private int expires_in;

	private String refresh_token;

	private String openid;

	private String scope;

	private String unionid;

}
