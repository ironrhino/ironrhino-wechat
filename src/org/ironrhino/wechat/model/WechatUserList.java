package org.ironrhino.wechat.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import lombok.Data;

@Data
public class WechatUserList implements Serializable {

	private static final long serialVersionUID = 2658037597334634022L;

	private int total;

	private List<String> openids = Collections.emptyList();

	private String next_openid;

}
