package org.ironrhino.wechat.handler;

import org.ironrhino.wechat.model.WechatRequest;
import org.ironrhino.wechat.model.WechatResponse;

public abstract class LocationEventHandler {

	public boolean takeover(String key) {
		return getClass().getName().equals(key);
	}

	public abstract WechatResponse handle(Double latitude, Double longitude,
			WechatRequest request);

}
