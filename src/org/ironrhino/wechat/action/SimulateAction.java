package org.ironrhino.wechat.action;

import org.ironrhino.core.metadata.AutoConfig;
import org.ironrhino.core.spring.configuration.StageConditional;
import org.ironrhino.core.struts.BaseAction;
import org.ironrhino.core.util.AppInfo.Stage;
import org.ironrhino.wechat.service.Wechat;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.interceptor.annotations.InputConfig;

import lombok.Getter;
import lombok.Setter;

@AutoConfig
@StageConditional(Stage.DEVELOPMENT)
public class SimulateAction extends BaseAction {

	private static final long serialVersionUID = -5256536021264076400L;

	@Autowired
	private Wechat wechat;

	@Getter
	@Setter
	private String text;

	@Getter
	private String result;

	@Override
	@InputConfig(resultName = SUCCESS)
	public String execute() throws Exception {
		result = wechat.reply(text);
		return SUCCESS;
	}

}
