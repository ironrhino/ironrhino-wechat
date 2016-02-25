package org.ironrhino.wechat.model;

import java.io.Serializable;
import java.util.List;

public class WechatNewsList implements Serializable {

	private static final long serialVersionUID = 2658037597334634022L;

	private int total_count;

	private int item_count;

	private List<WechatNews> item;

	public int getTotal_count() {
		return total_count;
	}

	public void setTotal_count(int total_count) {
		this.total_count = total_count;
	}

	public int getItem_count() {
		return item_count;
	}

	public void setItem_count(int item_count) {
		this.item_count = item_count;
	}

	public List<WechatNews> getItem() {
		return item;
	}

	public void setItem(List<WechatNews> item) {
		this.item = item;
	}

}
