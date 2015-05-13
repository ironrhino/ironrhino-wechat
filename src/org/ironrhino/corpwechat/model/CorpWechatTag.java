package org.ironrhino.corpwechat.model;

import java.io.Serializable;

public class CorpWechatTag implements Serializable {

	private static final long serialVersionUID = -2244954252146981392L;

	private Integer tagid;

	private String tagname;

	public Integer getTagid() {
		return tagid;
	}

	public void setTagid(Integer tagid) {
		this.tagid = tagid;
	}

	public String getTagname() {
		return tagname;
	}

	public void setTagname(String tagname) {
		this.tagname = tagname;
	}

}
