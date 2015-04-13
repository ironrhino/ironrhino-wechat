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

	@Autowired(required = false)
	private List<ScancodeEventHandler> scancodeEventHandlers;

	@Autowired(required = false)
	private List<PhotoEventHandler> photoEventHandlers;

	@Autowired(required = false)
	private List<UnsubscribeEventHandler> unsubscribeEventHandlers;

	@Override
	public WechatResponse handle(WechatRequest request) {
		if (request.getMsgType() != WechatRequestType.event)
			return null;
		String eventKey = request.getEventKey();
		switch (request.getEvent()) {
		case SCAN:
		case subscribe:
			int key = 0;
			if (StringUtils.isNotBlank(eventKey)) {
				if (eventKey.startsWith("qrscene_"))
					eventKey = eventKey.substring(eventKey.indexOf('_') + 1);
				if (StringUtils.isNumeric(eventKey)) {
					key = Integer.valueOf(eventKey);
				}
			}
			if (scanEventHandlers != null)
				for (ScanEventHandler seh : scanEventHandlers)
					if (seh.takeover(key)) {
						WechatResponse wr = seh.handle(key, request);
						return wr != null ? wr : WechatResponse.EMPTY;
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
		case scancode_push:
		case scancode_waitmsg:
			if (scancodeEventHandlers != null)
				for (ScancodeEventHandler seh : scancodeEventHandlers)
					if (seh.takeover(eventKey)) {
						WechatResponse wr = seh.handle(request.getScanResult(),
								request);
						return wr != null ? wr : WechatResponse.EMPTY;
					}
			break;
		case pic_photo_or_album:
		case pic_sysphoto:
			if (photoEventHandlers != null)
				for (PhotoEventHandler peh : photoEventHandlers)
					if (peh.takeover(eventKey)) {
						WechatResponse wr = peh.handle(request);
						return wr != null ? wr : WechatResponse.EMPTY;
					}
			break;
		case LOCATION:
			break;
		case unsubscribe:
			if (unsubscribeEventHandlers != null)
				for (UnsubscribeEventHandler ueh : unsubscribeEventHandlers) {
					WechatResponse wr = ueh.handle(request);
					if (wr != null)
						return wr;
				}
		default:
			break;
		}
		return null;
	}

}
