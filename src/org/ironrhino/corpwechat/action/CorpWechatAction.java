package org.ironrhino.corpwechat.action;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.ironrhino.core.metadata.AutoConfig;
import org.ironrhino.core.struts.BaseAction;
import org.ironrhino.corpwechat.service.CorpWechat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;

import com.qq.weixin.corp.aes.WXBizMsgCrypt;

@AutoConfig
@Order
public class CorpWechatAction extends BaseAction {

	private static final long serialVersionUID = 6947874262092642404L;

	private String msg_signature;

	private String timestamp;

	private String nonce;

	private String echostr;

	@Autowired
	private CorpWechat _wechat;

	public String getMsg_signature() {
		return msg_signature;
	}

	public void setMsg_signature(String msg_signature) {
		this.msg_signature = msg_signature;
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

	public CorpWechat getWechat() {
		return _wechat;
	}

	@Override
	public String execute() throws Exception {
		CorpWechat wechat = getWechat();
		WXBizMsgCrypt wxBizMsgCrypt = new WXBizMsgCrypt(wechat.getToken(),
				wechat.getEncodingAesKey(), wechat.getCorpId());
		if (StringUtils.isNotBlank(echostr)) {
			echostr = wxBizMsgCrypt.verifyURL(msg_signature, timestamp, nonce,
					echostr);
			ServletActionContext.getResponse().getWriter().write(echostr);
			return NONE;
		} else if (StringUtils.isNotBlank(requestBody)) {
			requestBody = wxBizMsgCrypt.decryptMsg(msg_signature, timestamp,
					nonce, requestBody);
			String responseBody = wechat.reply(requestBody);
			responseBody = wxBizMsgCrypt.encryptMsg(responseBody, timestamp,
					nonce);
			ServletActionContext.getResponse().getWriter().write(responseBody);
			return NONE;
		}
		return JSON;
	}

}
