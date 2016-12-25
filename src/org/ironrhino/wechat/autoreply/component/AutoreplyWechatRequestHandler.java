package org.ironrhino.wechat.autoreply.component;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ironrhino.wechat.autoreply.model.AutoreplyRule;
import org.ironrhino.wechat.autoreply.model.MatchMode;
import org.ironrhino.wechat.autoreply.service.AutoreplyRuleManager;
import org.ironrhino.wechat.handler.WechatRequestHandler;
import org.ironrhino.wechat.model.WechatRequest;
import org.ironrhino.wechat.model.WechatRequestType;
import org.ironrhino.wechat.model.WechatResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(Ordered.LOWEST_PRECEDENCE + 1)
@Component
public class AutoreplyWechatRequestHandler implements WechatRequestHandler {

	@Autowired
	private AutoreplyRuleManager autoreplyRuleManager;

	@Override
	public WechatResponse handle(WechatRequest request) {
		List<AutoreplyRule> rules;
		WechatRequestType msgType = request.getMsgType();
		switch (msgType) {
		case location:
		case link:
		case event:
			return null;
		case text:
			rules = autoreplyRuleManager.findRules();
			String msg = request.getContent();
			for (AutoreplyRule rule : rules) {
				if (rule.getMatchMode() == MatchMode.START && msg.startsWith(rule.getKeyword()))
					return WechatResponse.replyTo(request, rule.getContent());
				else if (rule.getMatchMode() == MatchMode.END && msg.endsWith(rule.getKeyword()))
					return WechatResponse.replyTo(request, rule.getContent());
				else if (rule.getMatchMode() == MatchMode.CONTAINS && msg.contains(rule.getKeyword()))
					return WechatResponse.replyTo(request, rule.getContent());
				else if (rule.getMatchMode() == MatchMode.REGEX) {
					String regex = rule.getKeyword();
					Pattern p = Pattern.compile(regex);
					Matcher m = p.matcher(msg);
					if (m.find()) {
						String content = rule.getContent();
						for (int i = 1; i <= m.groupCount(); i++)
							content = content.replaceAll("\\$" + i, m.group(i));
						return WechatResponse.replyTo(request, content);
					}
				} else if (rule.getMatchMode() == MatchMode.ANY)
					return WechatResponse.replyTo(request, rule.getContent());
			}
			break;
		case image:
		case voice:
		case video:
			rules = autoreplyRuleManager.findRules();
			for (AutoreplyRule rule : rules)
				if (rule.getMatchMode() == MatchMode.ANY)
					return WechatResponse.replyTo(request, rule.getContent());
		default:
		}
		return null;
	}

}
