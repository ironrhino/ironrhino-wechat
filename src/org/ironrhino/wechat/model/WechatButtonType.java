package org.ironrhino.wechat.model;

import org.ironrhino.core.model.Displayable;

public enum WechatButtonType implements Displayable {

	click, view, scancode_push, scancode_waitmsg, pic_sysphoto, pic_photo_or_album, pic_weixin, location_select, media_id, view_limited;

	@Override
	public String getName() {
		return Displayable.super.getName();
	}

	@Override
	public String getDisplayName() {
		return Displayable.super.getDisplayName();
	}

	@Override
	public String toString() {
		return getDisplayName();
	}
}