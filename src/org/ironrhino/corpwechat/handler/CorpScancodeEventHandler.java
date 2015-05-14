package org.ironrhino.corpwechat.handler;

import org.ironrhino.corpwechat.model.CorpWechatRequest;
import org.ironrhino.corpwechat.model.CorpWechatResponse;

public abstract class CorpScancodeEventHandler {

	public boolean takeover(String key) {
		return getClass().getName().equals(key);
	}

	public abstract CorpWechatResponse handle(String result, CorpWechatRequest request);

}
