package org.ironrhino.wechat.action;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.ironrhino.core.metadata.AutoConfig;
import org.ironrhino.core.struts.BaseAction;
import org.ironrhino.wechat.service.Wechat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;

@AutoConfig
@Order
public class WechatAction extends BaseAction {

	private static final long serialVersionUID = 6947874262092642404L;

	private String signature;

	private String timestamp;

	private String nonce;

	private String echostr;

	@Autowired
	private Wechat _wechat;

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getNonce() {
		return nonce;
	}

	public void setNonce(String nonce) {
		this.nonce = nonce;
	}

	public String getEchostr() {
		return echostr;
	}

	public void setEchostr(String echostr) {
		this.echostr = echostr;
	}

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
			ServletActionContext.getResponse().getWriter()
					.write(wechat.reply(requestBody));
			return NONE;
		}
		return JSON;
	}

}
