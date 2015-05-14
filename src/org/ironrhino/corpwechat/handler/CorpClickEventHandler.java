package org.ironrhino.corpwechat.handler;

import org.ironrhino.corpwechat.model.CorpWechatRequest;
import org.ironrhino.corpwechat.model.CorpWechatResponse;

public abstract class CorpClickEventHandler {

	public boolean takeover(String key) {
		return getClass().getName().equals(key);
	}

	public abstract CorpWechatResponse handle(String key, CorpWechatRequest request);

}
