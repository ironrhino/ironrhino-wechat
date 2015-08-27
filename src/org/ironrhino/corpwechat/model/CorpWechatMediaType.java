package org.ironrhino.corpwechat.model;

import org.ironrhino.core.model.Displayable;

public enum CorpWechatMediaType implements Displayable {

	image(1024 * 1024), voice(2 * 1024 * 1024), video(10 * 1024 * 1024), file(10 * 1024 * 1024);

	private int maxFileLength;

	private CorpWechatMediaType(int maxFileLength) {
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
