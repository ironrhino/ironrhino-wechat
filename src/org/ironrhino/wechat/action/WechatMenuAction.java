package org.ironrhino.wechat.action;

import org.ironrhino.core.metadata.AutoConfig;
import org.ironrhino.core.struts.BaseAction;
import org.ironrhino.core.util.JsonUtils;
import org.ironrhino.wechat.support.WechatMenuControl;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.interceptor.annotations.InputConfig;

@AutoConfig
public class WechatMenuAction extends BaseAction {

	private static final long serialVersionUID = -5256536021264076400L;

	@Autowired
	private WechatMenuControl wechatMenuControl;

	private String json;

	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}

	@Override
	@InputConfig(methodName = INPUT)
	public String execute() throws Exception {
		if (!JsonUtils.isValidJson(json)) {
			addActionError("不是合法的JSON");
		} else {
			wechatMenuControl.create(json);
			addActionMessage(getText("operate.success"));
		}
		return SUCCESS;
	}

	public String input() throws Exception {
		json = wechatMenuControl.getAsText();
		json = JsonUtils.prettify(json);
		return SUCCESS;
	}

}
