package org.ironrhino.wechat.handler;

import java.util.concurrent.ThreadLocalRandom;

import org.ironrhino.wechat.model.WechatRequest;
import org.ironrhino.wechat.model.WechatResponse;

public abstract class ScanEventHandler {

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

	public abstract WechatResponse handle(int key, WechatRequest request);

}
