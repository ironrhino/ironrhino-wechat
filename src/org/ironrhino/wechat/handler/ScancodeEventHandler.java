package org.ironrhino.wechat.handler;

import org.ironrhino.wechat.model.WechatRequest;
import org.ironrhino.wechat.model.WechatResponse;

public abstract class ScancodeEventHandler {

	public boolean takeover(String key) {
		return getClass().getName().equals(key);
	}

	public abstract WechatResponse handle(String result, WechatRequest request);

}
