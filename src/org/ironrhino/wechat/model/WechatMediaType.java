package org.ironrhino.wechat.model;

import org.ironrhino.core.model.Displayable;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.util.LocalizedTextUtil;

public enum WechatMediaType implements Displayable {

	image(128 * 1024), voice(256 * 1024), video(1024 * 1024), thumb(64 * 1024);

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