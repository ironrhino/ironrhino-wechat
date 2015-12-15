package org.ironrhino.wechat.model;

import java.io.Serializable;
import java.util.List;

public class WechatMaterialList implements Serializable {

	private static final long serialVersionUID = 2658037597334634022L;

	private int total_count;

	private int item_count;

	private List<WechatMaterial> item;

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

	public List<WechatMaterial> getItem() {
		return item;
	}

	public void setItem(List<WechatMaterial> item) {
		this.item = item;
	}

	public static class WechatMaterial implements Serializable {

		private static final long serialVersionUID = -6541948486891391341L;

		private String media_id;
		private String name;
		private String url;
		private String update_time;

		public String getMedia_id() {
			return media_id;
		}

		public void setMedia_id(String media_id) {
			this.media_id = media_id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getUpdate_time() {
			return update_time;
		}

		public void setUpdate_time(String update_time) {
			this.update_time = update_time;
		}

	}

}
