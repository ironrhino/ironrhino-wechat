package org.ironrhino.wechat.autoreply.service;

import java.util.List;

import org.ironrhino.core.service.BaseManager;
import org.ironrhino.wechat.autoreply.model.AutoreplyRule;

public interface AutoreplyRuleManager extends BaseManager<AutoreplyRule> {
	
	public List<AutoreplyRule> findRules();

}
