package org.ironrhino.corpwechat.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class CorpWechatTag implements Serializable {

	private static final long serialVersionUID = -2244954252146981392L;

	private Integer tagid;

	private String tagname;

}
