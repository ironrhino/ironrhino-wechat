package org.ironrhino.wechat.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.ironrhino.core.util.JsonUtils;
import org.springframework.beans.BeanUtils;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class WechatMenu implements Serializable {

	private static final long serialVersionUID = -7522732554408635903L;

	private Integer menuid;

	private List<WechatButton> button = new ArrayList<>();

	public WechatMenu(String json) {
		try {
			WechatMenu wm = JsonUtils.fromJson(json, WechatMenu.class);
			BeanUtils.copyProperties(wm, this);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public void validate() {
		int size = button.size();
		if (size > 3)
			throw new IllegalArgumentException("一级菜单最多允许3个");
		for (WechatButton b : button)
			b.validate(false);
	}

	@Data
	public static class WechatButton implements Serializable {

		private static final long serialVersionUID = 4413492219682548885L;

		private String name;
		private WechatButtonType type;
		private String key;
		private String url;
		private String media_id;
		private List<WechatButton> sub_button = new ArrayList<WechatMenu.WechatButton>();

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

	}

	public static enum WechatButtonType {
		click("点击"), view("跳转"), scancode_push("扫码"), scancode_waitmsg("扫码等待"), pic_sysphoto(
				"拍照发图"), pic_photo_or_album("拍照或相册发图"), pic_weixin(
						"微信相册发图"), location_select("地理位置选择"), media_id("永久素材"), view_limited("查看图文");

		private String displayName;

		private WechatButtonType(String displayName) {
			this.displayName = displayName;
		}

		public String getName() {
			return name();
		}

		public String getDisplayName() {
			return displayName;
		}

		public String toString() {
			return displayName;
		}
	}

}
