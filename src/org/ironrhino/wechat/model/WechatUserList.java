package org.ironrhino.wechat.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class WechatUserList implements Serializable {

	private static final long serialVersionUID = 2658037597334634022L;

	private int total;

	private List<String> openids = Collections.emptyList();

	private String next_openid;

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public List<String> getOpenids() {
		return openids;
	}

	public void setOpenids(List<String> openids) {
		this.openids = openids;
	}

	public String getNext_openid() {
		return next_openid;
	}

	public void setNext_openid(String next_openid) {
		this.next_openid = next_openid;
	}

}
