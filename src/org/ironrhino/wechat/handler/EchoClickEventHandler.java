package org.ironrhino.wechat.handler;

import org.ironrhino.core.spring.configuration.StageConditional;
import org.ironrhino.core.util.AppInfo.Stage;
import org.ironrhino.wechat.model.WechatRequest;
import org.ironrhino.wechat.model.WechatResponse;
import org.springframework.stereotype.Component;

@Component
@StageConditional(Stage.DEVELOPMENT)
public class EchoClickEventHandler extends ClickEventHandler {

	@Override
	public WechatResponse handle(String key, WechatRequest request) {
		return WechatResponse.replyTo(request, key);
	}

}
