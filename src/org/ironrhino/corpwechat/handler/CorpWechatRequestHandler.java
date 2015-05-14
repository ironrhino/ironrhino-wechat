package org.ironrhino.corpwechat.handler;

import org.ironrhino.corpwechat.model.CorpWechatRequest;
import org.ironrhino.corpwechat.model.CorpWechatResponse;

public interface CorpWechatRequestHandler {

	public CorpWechatResponse handle(CorpWechatRequest request);

}
