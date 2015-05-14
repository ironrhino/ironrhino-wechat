package org.ironrhino.corpwechat.handler;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.ironrhino.corpwechat.model.CorpWechatRequest;
import org.ironrhino.corpwechat.model.CorpWechatRequestType;
import org.ironrhino.corpwechat.model.CorpWechatResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
public class CorpEventWechatRequestHandler implements CorpWechatRequestHandler {

	@Autowired(required = false)
	private List<CorpScanEventHandler> scanEventHandlers;

	@Autowired(required = false)
	private List<CorpClickEventHandler> clickEventHandlers;

	@Autowired(required = false)
	private List<CorpLocationEventHandler> locationEventHandlers;

	@Autowired(required = false)
	private List<CorpScancodeEventHandler> scancodeEventHandlers;

	@Autowired(required = false)
	private List<CorpPhotoEventHandler> photoEventHandlers;

	@Autowired(required = false)
	private List<CorpUnsubscribeEventHandler> unsubscribeEventHandlers;

	@Override
	public CorpWechatResponse handle(CorpWechatRequest request) {
		if (request.getMsgType() != CorpWechatRequestType.event)
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
				for (CorpScanEventHandler seh : scanEventHandlers)
					if (seh.takeover(key)) {
						CorpWechatResponse wr = seh.handle(key, request);
						return wr != null ? wr : CorpWechatResponse.EMPTY;
					}
			break;
		case CLICK:
			if (clickEventHandlers != null)
				for (CorpClickEventHandler ceh : clickEventHandlers)
					if (ceh.takeover(eventKey)) {
						CorpWechatResponse wr = ceh.handle(eventKey, request);
						return wr != null ? wr : CorpWechatResponse.EMPTY;
					}

			break;
		case LOCATION:
		case location_select:
			if (locationEventHandlers != null)
				for (CorpLocationEventHandler leh : locationEventHandlers)
					if (leh.takeover(eventKey)) {
						CorpWechatResponse wr = leh.handle(request.getLatitude(),
								request.getLongitude(), request);
						return wr != null ? wr : CorpWechatResponse.EMPTY;
					}

			break;
		case scancode_push:
		case scancode_waitmsg:
			if (scancodeEventHandlers != null)
				for (CorpScancodeEventHandler seh : scancodeEventHandlers)
					if (seh.takeover(eventKey)) {
						CorpWechatResponse wr = seh.handle(request.getScanResult(),
								request);
						return wr != null ? wr : CorpWechatResponse.EMPTY;
					}
			break;
		case pic_photo_or_album:
		case pic_sysphoto:
			if (photoEventHandlers != null)
				for (CorpPhotoEventHandler peh : photoEventHandlers)
					if (peh.takeover(eventKey)) {
						CorpWechatResponse wr = peh.handle(request);
						return wr != null ? wr : CorpWechatResponse.EMPTY;
					}
			break;

		case unsubscribe:
			if (unsubscribeEventHandlers != null)
				for (CorpUnsubscribeEventHandler ueh : unsubscribeEventHandlers) {
					CorpWechatResponse wr = ueh.handle(request);
					if (wr != null)
						return wr;
				}
		default:
			break;
		}
		return null;
	}

}
