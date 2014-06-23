package org.ironrhino.wechat.model;

import org.ironrhino.core.model.Displayable;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.util.LocalizedTextUtil;

public enum WechatRequestType implements Displayable {

	text, image, voice, video, location, link, event;

	@Override
	public String getName() {
		return this.name();
	}

	@Override
	public String getDisplayName() {
		try {
			return LocalizedTextUtil.findText(getClass(), name(), ActionContext
					.getContext().getLocale(), name(), null);
		} catch (Exception e) {
			return name();
		}
	}

	@Override
	public String toString() {
		return getDisplayName();
	}

}
