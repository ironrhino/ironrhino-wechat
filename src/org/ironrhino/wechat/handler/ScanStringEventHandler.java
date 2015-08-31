package org.ironrhino.wechat.handler;

import org.ironrhino.wechat.model.WechatRequest;
import org.ironrhino.wechat.model.WechatResponse;

public abstract class ScanStringEventHandler {

	public abstract WechatResponse handle(String key, WechatRequest request);

}
