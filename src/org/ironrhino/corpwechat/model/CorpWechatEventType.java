package org.ironrhino.corpwechat.model;

import org.ironrhino.core.model.Displayable;
import org.ironrhino.core.struts.I18N;

public enum CorpWechatEventType implements Displayable {

	subscribe, unsubscribe, scan, location, click, view, scancode_push, scancode_waitmsg, pic_sysphoto, pic_photo_or_album, pic_weixin, location_select;

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
