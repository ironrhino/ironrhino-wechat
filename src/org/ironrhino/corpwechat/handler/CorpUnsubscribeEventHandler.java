package org.ironrhino.corpwechat.handler;

import org.ironrhino.corpwechat.model.CorpWechatRequest;
import org.ironrhino.corpwechat.model.CorpWechatResponse;

public interface CorpUnsubscribeEventHandler {

	public CorpWechatResponse handle(CorpWechatRequest request);

}
