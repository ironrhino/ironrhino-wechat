package org.ironrhino.wechat.handler;

import org.ironrhino.wechat.model.WechatRequest;
import org.ironrhino.wechat.model.WechatResponse;
import org.springframework.stereotype.Component;

@Component
public class EchoClickEventHandler extends ClickEventHandler {

	@Override
	public WechatResponse handle(String key, WechatRequest request) {
		return WechatResponse.replyTo(request, key);
	}

}
