package org.ironrhino.corpwechat.handler;

import org.ironrhino.core.spring.configuration.StageConditional;
import org.ironrhino.core.util.AppInfo.Stage;
import org.ironrhino.corpwechat.model.CorpWechatRequest;
import org.ironrhino.corpwechat.model.CorpWechatResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(Ordered.LOWEST_PRECEDENCE)
@Component
@StageConditional(Stage.DEVELOPMENT)
public class CorpEchoWechatRequestHandler implements CorpWechatRequestHandler {

	@Override
	public CorpWechatResponse handle(CorpWechatRequest request) {
		return CorpWechatResponse.replyTo(request, request.toString());
	}

}
