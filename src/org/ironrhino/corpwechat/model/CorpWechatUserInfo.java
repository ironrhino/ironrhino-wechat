package org.ironrhino.corpwechat.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class CorpWechatUserInfo implements Serializable {

	private static final long serialVersionUID = -2244954252146981392L;

	private String userid;

	private String openid;

	private String deviceid;

}
