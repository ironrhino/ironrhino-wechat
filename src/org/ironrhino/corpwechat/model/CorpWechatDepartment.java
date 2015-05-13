package org.ironrhino.corpwechat.model;

import java.io.Serializable;

public class CorpWechatDepartment implements Serializable {

	private static final long serialVersionUID = -2244954252146981392L;

	private Integer id;

	private String name;

	private Integer parentid;

	private Integer order;

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

	public Integer getParentid() {
		return parentid;
	}

	public void setParentid(Integer parentid) {
		this.parentid = parentid;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

}
