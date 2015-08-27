package org.ironrhino.wechat.model;

import org.ironrhino.core.model.Displayable;

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
