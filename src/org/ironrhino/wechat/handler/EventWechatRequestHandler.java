package org.ironrhino.wechat.handler;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.ironrhino.wechat.model.WechatRequest;
import org.ironrhino.wechat.model.WechatRequestType;
import org.ironrhino.wechat.model.WechatResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
public class EventWechatRequestHandler implements WechatRequestHandler {

	@Autowired(required = false)
	private List<ScanEventHandler> scanEventHandlers;

	@Autowired(required = false)
	private List<ClickEventHandler> clickEventHandlers;

	@Override
	public WechatResponse handle(WechatRequest request) {
		if (request.getMsgType() != WechatRequestType.event)
			return null;
		String eventKey = request.getEventKey();
		switch (request.getEvent()) {
		case SCAN:
		case subscribe:
			if (eventKey.startsWith("qrscene_"))
				eventKey = eventKey.substring(eventKey.indexOf('_') + 1);
			if (StringUtils.isNumeric(eventKey)) {
				int key = Integer.valueOf(eventKey);
				if (scanEventHandlers != null)
					for (ScanEventHandler seh : scanEventHandlers)
						if (seh.takeover(key)) {
							WechatResponse wr = seh.handle(key, request);
							return wr != null ? wr : WechatResponse.EMPTY;
						}
			}
			break;
		case CLICK:
			if (clickEventHandlers != null)
				for (ClickEventHandler ceh : clickEventHandlers)
					if (ceh.takeover(eventKey)) {
						WechatResponse wr = ceh.handle(eventKey, request);
						return wr != null ? wr : WechatResponse.EMPTY;
					}

			break;
		case LOCATION:
			break;
		case unsubscribe:
			break;
		default:
			break;
		}
		return WechatResponse.EMPTY;
	}

}
