package org.ironrhino.corpwechat.handler;

import java.util.concurrent.ThreadLocalRandom;

import org.ironrhino.corpwechat.model.CorpWechatRequest;
import org.ironrhino.corpwechat.model.CorpWechatResponse;

public abstract class CorpScanEventHandler {

	public abstract int getMinimalKey();

	public abstract int getMaximumKey();

	public int randomKey() {
		return getMinimalKey()
				+ ThreadLocalRandom.current().nextInt(
						getMaximumKey() - getMinimalKey() + 1);
	}

	public boolean takeover(int key) {
		return key >= getMinimalKey() && key <= getMaximumKey();
	}

	public abstract CorpWechatResponse handle(int key, CorpWechatRequest request);

}
