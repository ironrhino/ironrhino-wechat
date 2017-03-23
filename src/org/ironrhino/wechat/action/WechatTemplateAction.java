package org.ironrhino.wechat.action;

import java.util.List;

import org.ironrhino.core.metadata.AutoConfig;
import org.ironrhino.core.struts.BaseAction;
import org.ironrhino.wechat.model.WechatTemplate;
import org.ironrhino.wechat.support.WechatTemplateControl;
import org.springframework.beans.factory.annotation.Autowired;

@AutoConfig
public class WechatTemplateAction extends BaseAction {

	private static final long serialVersionUID = -5256536021264076400L;

	@Autowired
	private WechatTemplateControl wechatTemplateControl;

	private List<WechatTemplate> list;

	public List<WechatTemplate> getList() {
		return list;
	}

	@Override
	public String execute() throws Exception {
		list = wechatTemplateControl.list();
		return LIST;
	}

	@Override
	public String save() throws Exception {
		wechatTemplateControl.add(getUid());
		return SUCCESS;
	}

	@Override
	public String delete() throws Exception {
		wechatTemplateControl.delete(getUid());
		return SUCCESS;
	}

}
