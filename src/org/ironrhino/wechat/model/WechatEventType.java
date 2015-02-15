package org.ironrhino.wechat.model;

import org.ironrhino.core.model.Displayable;
import org.ironrhino.core.struts.I18N;

public enum WechatEventType implements Displayable {

	subscribe, unsubscribe, SCAN, LOCATION, CLICK, VIEW, scancode_push, scancode_waitmsg, pic_sysphoto, pic_photo_or_album, pic_weixin, location_select, TEMPLATESENDJOBFINISH, MASSSENDJOBFINISH;

	@Override
	public String getName() {
		return this.name();
	}

	@Override
	public String getDisplayName() {
		try {
			return I18N.getText(getClass(), name());
		} catch (Exception e) {
			return name();
		}
	}

	@Override
	public String toString() {
		return getDisplayName();
	}

}
