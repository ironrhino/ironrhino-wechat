package org.ironrhino.corpwechat.action;

import org.ironrhino.core.metadata.AutoConfig;
import org.ironrhino.core.spring.configuration.StageConditional;
import org.ironrhino.core.struts.BaseAction;
import org.ironrhino.core.util.AppInfo.Stage;
import org.ironrhino.corpwechat.service.CorpWechat;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.interceptor.annotations.InputConfig;

@AutoConfig
@StageConditional(Stage.DEVELOPMENT)
public class CorpSimulateAction extends BaseAction {

	private static final long serialVersionUID = -5256536021264076400L;

	@Autowired
	private CorpWechat wechat;

	private String text;

	private String result;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getResult() {
		return result;
	}

	@Override
	@InputConfig(resultName = SUCCESS)
	public String execute() throws Exception {
		result = wechat.reply(text);
		return SUCCESS;
	}

}
