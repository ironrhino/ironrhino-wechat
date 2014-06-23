package org.ironrhino.wechat.model;

import java.io.Serializable;

public class WechatGroup implements Serializable {

	private static final long serialVersionUID = -2244954252146981392L;

	private int id;

	private String name;

	private int count;

	public int getId() {
		return id;
	}

	public void setId(int id) {
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
