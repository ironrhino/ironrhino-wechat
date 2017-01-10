package org.ironrhino.wechat.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.ironrhino.core.util.JsonUtils;
import org.springframework.beans.BeanUtils;

public class WechatMenu implements Serializable {

	private static final long serialVersionUID = -7522732554408635903L;

	private Integer menuid;

	private List<WechatButton> button = new ArrayList<>();

	public WechatMenu() {

	}

	public WechatMenu(String json) {
		try {
			WechatMenu wm = JsonUtils.fromJson(json, WechatMenu.class);
			BeanUtils.copyProperties(wm, this);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public Integer getMenuid() {
		return menuid;
	}

	public void setMenuid(Integer menuid) {
		this.menuid = menuid;
	}

	public List<WechatButton> getButton() {
		return button;
	}

	public void setButton(List<WechatButton> button) {
		this.button = button;
	}

	public void validate() {
		int size = button.size();
		if (size > 3)
			throw new IllegalArgumentException("一级菜单最多允许3个");
		for (WechatButton b : button)
			b.validate(false);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (!(obj instanceof WechatMenu))
			return false;
		WechatMenu that = (WechatMenu) obj;
		return this.button.equals(that.button);
	}

	@Override
	public int hashCode() {
		return this.button != null ? this.button.hashCode() : 0;
	}

	public static class WechatButton implements Serializable {

		private static final long serialVersionUID = 4413492219682548885L;

		private String name;
		private WechatButtonType type;
		private String key;
		private String url;
		private String media_id;
		private List<WechatButton> sub_button = new ArrayList<WechatMenu.WechatButton>();

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public WechatButtonType getType() {
			return type;
		}

		public void setType(WechatButtonType type) {
			this.type = type;
		}

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getMedia_id() {
			return media_id;
		}

		public void setMedia_id(String media_id) {
			this.media_id = media_id;
		}

		public List<WechatButton> getSub_button() {
			return sub_button;
		}

		public void setSub_button(List<WechatButton> sub_button) {
			this.sub_button = sub_button;
		}

		public void validate(boolean sub) {
			if (StringUtils.isBlank(name)) {
				throw new IllegalArgumentException("name shouldn't be blank");
			} else {
				int maxlength = sub ? 40 : 16;
				if (name.getBytes().length > maxlength)
					throw new IllegalArgumentException("name shouldn't be more than " + maxlength + " bytes");
			}
			if (sub_button != null && sub_button.size() > 0) {
				if (type != null)
					throw new IllegalArgumentException("type should be blank,but was " + type);
				if (StringUtils.isNotBlank(key))
					throw new IllegalArgumentException("key should be blank,but was " + key);
				if (StringUtils.isNotBlank(url))
					throw new IllegalArgumentException("url should be blank,but was " + url);
				int size = sub_button.size();
				if (size > 5)
					throw new IllegalArgumentException("二级菜单最多允许5个");
				for (WechatButton button : sub_button)
					button.validate(true);
			} else {
				if (type == null)
					throw new IllegalArgumentException("type shouldn't be blank");
				switch (type) {
				case view:
					if (StringUtils.isBlank(url))
						throw new IllegalArgumentException("url shouldn't be blank");
					else if (url.getBytes().length > 1024)
						throw new IllegalArgumentException("url shouldn't be more than 1024 bytes");
					break;

				case click:
				case scancode_push:
				case scancode_waitmsg:
				case pic_sysphoto:
				case pic_photo_or_album:
				case pic_weixin:
				case location_select:
					if (StringUtils.isBlank(key))
						throw new IllegalArgumentException("key shouldn't be blank");
					else if (key.getBytes().length > 128)
						throw new IllegalArgumentException("key shouldn't be more than 128 bytes");
					break;
				case media_id:
				case view_limited:
					if (StringUtils.isBlank(media_id))
						throw new IllegalArgumentException("media_id shouldn't be blank");
					break;
				default:
					break;
				}

			}
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == this)
				return true;
			if (!(obj instanceof WechatButton))
				return false;
			WechatButton that = (WechatButton) obj;
			return EqualsBuilder.reflectionEquals(this, that);
		}

		@Override
		public int hashCode() {
			return HashCodeBuilder.reflectionHashCode(this);
		}

	}

}
