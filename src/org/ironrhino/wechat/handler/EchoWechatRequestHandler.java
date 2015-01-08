package org.ironrhino.wechat.handler;

import org.ironrhino.core.spring.configuration.StageConditional;
import org.ironrhino.core.util.AppInfo.Stage;
import org.ironrhino.wechat.model.WechatRequest;
import org.ironrhino.wechat.model.WechatResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(Ordered.LOWEST_PRECEDENCE)
@Component
@StageConditional(Stage.DEVELOPMENT)
public class EchoWechatRequestHandler implements WechatRequestHandler {

	@Override
	public WechatResponse handle(WechatRequest request) {
		return WechatResponse.replyTo(request, request.toString());
	}

}
