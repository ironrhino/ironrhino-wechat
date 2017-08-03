package org.ironrhino.wechat.action;

import java.util.EnumSet;

import org.ironrhino.core.metadata.AutoConfig;
import org.ironrhino.core.struts.BaseAction;
import org.ironrhino.core.util.JsonUtils;
import org.ironrhino.wechat.model.WechatMenu;
import org.ironrhino.wechat.model.WechatMenu.WechatButtonType;
import org.ironrhino.wechat.support.WechatMenuControl;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.interceptor.annotations.InputConfig;

import lombok.Getter;
import lombok.Setter;

@AutoConfig
public class WechatMenuAction extends BaseAction {

	private static final long serialVersionUID = -5256536021264076400L;

	@Autowired
	private WechatMenuControl wechatMenuControl;

	@Getter
	@Setter
	private boolean viewOnly;

	@Getter
	@Setter
	private String json;

	@Getter
	@Setter
	private WechatMenu menu;

	public EnumSet<WechatButtonType> getButtonTypes() {
		return viewOnly ? EnumSet.of(WechatButtonType.view) : EnumSet.allOf(WechatButtonType.class);
	}

	@Override
	@InputConfig(methodName = INPUT)
	public String execute() throws Exception {
		wechatMenuControl.create(menu);
		addActionMessage(getText("operate.success"));
		return SUCCESS;
	}

	public String input() throws Exception {
		menu = wechatMenuControl.get();
		if (menu == null)
			menu = new WechatMenu();
		return SUCCESS;
	}

	@InputConfig(methodName = "inputraw")
	public String raw() throws Exception {
		if (!JsonUtils.isValidJson(json)) {
			addActionError("不是合法的JSON");
		} else {
			wechatMenuControl.create(json);
			addActionMessage(getText("operate.success"));
		}
		return SUCCESS;
	}

	public String inputraw() throws Exception {
		json = wechatMenuControl.getAsText();
		json = JsonUtils.prettify(json);
		return "raw";
	}

}
