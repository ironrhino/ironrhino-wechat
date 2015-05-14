package org.ironrhino.corpwechat.handler;

import org.ironrhino.corpwechat.model.CorpWechatRequest;
import org.ironrhino.corpwechat.model.CorpWechatResponse;

public abstract class CorpLocationEventHandler {

	public boolean takeover(String key) {
		return getClass().getName().equals(key);
	}

	public abstract CorpWechatResponse handle(Double latitude, Double longitude,
			CorpWechatRequest request);

}
