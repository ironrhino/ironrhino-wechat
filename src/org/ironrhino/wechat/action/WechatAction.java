package org.ironrhino.wechat.action;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.ironrhino.core.metadata.AutoConfig;
import org.ironrhino.core.struts.BaseAction;
import org.ironrhino.wechat.service.Wechat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;

import com.qq.weixin.mp.aes.WXBizMsgCrypt;

import lombok.Getter;
import lombok.Setter;

@AutoConfig
@Order
public class WechatAction extends BaseAction {

	private static final long serialVersionUID = 6947874262092642404L;

	@Getter
	@Setter
	private String signature;

	@Getter
	@Setter
	private String msg_signature;

	@Getter
	@Setter
	private String timestamp;

	@Getter
	@Setter
	private String nonce;

	@Getter
	@Setter
	private String echostr;

	@Autowired
	private Wechat _wechat;

	public Wechat getWechat() {
		return _wechat;
	}

	@Override
	public String execute() throws Exception {
		Wechat wechat = getWechat();
		if (!wechat.verifySignature(timestamp, nonce, signature))
			return NONE;
		if (StringUtils.isNotBlank(echostr)) {
			ServletActionContext.getResponse().getWriter().write(echostr);
			return NONE;
		}
		if (StringUtils.isNotBlank(requestBody)) {
			boolean encrypted = false;
			WXBizMsgCrypt wxBizMsgCrypt = null;
			if (msg_signature != null && timestamp != null && nonce != null) {
				encrypted = true;
				wxBizMsgCrypt = new WXBizMsgCrypt(wechat.getToken(), wechat.getEncodingAesKey(), wechat.getAppId());
				requestBody = wxBizMsgCrypt.decryptMsg(msg_signature, timestamp, nonce, requestBody);
			}
			String responseBody = wechat.reply(requestBody);
			if (encrypted) {
				responseBody = wxBizMsgCrypt.encryptMsg(responseBody, timestamp, nonce);
			}
			ServletActionContext.getResponse().getWriter().write(responseBody);
			return NONE;
		}
		return JSON;
	}

}
