package org.ironrhino.wechat.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class WechatGroup implements Serializable {

	private static final long serialVersionUID = -2244954252146981392L;

	private Integer id;

	private String name;

	private int count;

}
