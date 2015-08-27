package org.ironrhino.wechat.model;

import org.ironrhino.core.model.Displayable;

public enum WechatEventType implements Displayable {

	subscribe, unsubscribe, SCAN, LOCATION, CLICK, VIEW, scancode_push, scancode_waitmsg, pic_sysphoto, pic_photo_or_album, pic_weixin, location_select, TEMPLATESENDJOBFINISH, MASSSENDJOBFINISH;

	public String getName() {
		return name();
	}

	public String getDisplayName() {
		return Displayable.super.getDisplayName();
	}

	@Override
	public String toString() {
		return getDisplayName();
	}

}
