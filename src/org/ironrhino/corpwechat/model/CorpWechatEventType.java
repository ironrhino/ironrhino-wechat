package org.ironrhino.corpwechat.model;

import org.ironrhino.core.model.Displayable;

public enum CorpWechatEventType implements Displayable {

	subscribe, unsubscribe, scan, location, click, view, scancode_push, scancode_waitmsg, pic_sysphoto, pic_photo_or_album, pic_weixin, location_select;

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
