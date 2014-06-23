package org.ironrhino.wechat.handler;

import org.ironrhino.wechat.model.WechatRequest;
import org.ironrhino.wechat.model.WechatResponse;

public interface WechatRequestHandler {
	
	public WechatResponse handle(WechatRequest request);

}
