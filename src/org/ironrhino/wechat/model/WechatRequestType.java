package org.ironrhino.wechat.model;

import org.ironrhino.core.model.Displayable;
import org.ironrhino.core.struts.I18N;

public enum WechatRequestType implements Displayable {

	text, image, voice, video, location, link, event;

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
