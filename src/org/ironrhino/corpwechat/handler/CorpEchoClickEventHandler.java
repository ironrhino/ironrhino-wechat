package org.ironrhino.corpwechat.handler;

import org.ironrhino.core.spring.configuration.StageConditional;
import org.ironrhino.core.util.AppInfo.Stage;
import org.ironrhino.corpwechat.model.CorpWechatRequest;
import org.ironrhino.corpwechat.model.CorpWechatResponse;
import org.springframework.stereotype.Component;

@Component
@StageConditional(Stage.DEVELOPMENT)
public class CorpEchoClickEventHandler extends CorpClickEventHandler {

	@Override
	public CorpWechatResponse handle(String key, CorpWechatRequest request) {
		return CorpWechatResponse.replyTo(request, key);
	}

}
