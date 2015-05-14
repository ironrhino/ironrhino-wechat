package org.ironrhino.corpwechat.handler;

import org.ironrhino.corpwechat.model.CorpWechatRequest;
import org.ironrhino.corpwechat.model.CorpWechatResponse;

public abstract class CorpPhotoEventHandler {

	public boolean takeover(String key) {
		return getClass().getName().equals(key);
	}

	public abstract CorpWechatResponse handle(CorpWechatRequest request);

}
