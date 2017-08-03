package org.ironrhino.corpwechat.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class CorpWechatDepartment implements Serializable {

	private static final long serialVersionUID = -2244954252146981392L;

	private Integer id;

	private String name;

	private Integer parentid;

	private Integer order;

}
