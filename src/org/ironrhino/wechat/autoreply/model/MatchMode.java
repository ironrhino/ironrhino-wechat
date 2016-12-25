package org.ironrhino.wechat.autoreply.model;

import org.ironrhino.core.model.Displayable;

public enum MatchMode implements Displayable {

	START, END, CONTAINS, REGEX, ANY;

	@Override
	public String getName() {
		return Displayable.super.getName();
	}

	@Override
	public String getDisplayName() {
		return Displayable.super.getDisplayName();
	}

	@Override
	public String toString() {
		return getDisplayName();
	}
}
