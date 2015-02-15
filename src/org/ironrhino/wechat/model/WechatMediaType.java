package org.ironrhino.wechat.model;

import org.ironrhino.core.model.Displayable;
import org.ironrhino.core.struts.I18N;

public enum WechatMediaType implements Displayable {

	image(128 * 1024), voice(256 * 1024), video(1024 * 1024), thumb(64 * 1024), news(
			Integer.MAX_VALUE);

	private int maxFileLength;

	private WechatMediaType(int maxFileLength) {
		this.maxFileLength = maxFileLength;
	}

	public int getMaxFileLength() {
		return maxFileLength;
	}

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
