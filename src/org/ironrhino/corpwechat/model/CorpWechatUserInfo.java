package org.ironrhino.corpwechat.model;

import java.io.Serializable;

public class CorpWechatUserInfo implements Serializable {

	private static final long serialVersionUID = -2244954252146981392L;

	private String userid;

	private String openid;

	private String deviceid;

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getDeviceid() {
		return deviceid;
	}

	public void setDeviceid(String deviceid) {
		this.deviceid = deviceid;
	}

}
