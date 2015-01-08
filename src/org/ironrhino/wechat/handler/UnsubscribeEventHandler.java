package org.ironrhino.wechat.handler;

import org.ironrhino.wechat.model.WechatRequest;
import org.ironrhino.wechat.model.WechatResponse;

public interface UnsubscribeEventHandler {

	public WechatResponse handle(WechatRequest request);

}
