package org.ironrhino.wechat.model;

import java.io.Serializable;

public class WechatGroup implements Serializable {

	private static final long serialVersionUID = -2244954252146981392L;

	private Integer id;

	private String name;

	private int count;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

}
