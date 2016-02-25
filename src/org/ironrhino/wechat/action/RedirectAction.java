package org.ironrhino.wechat.action;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.ironrhino.core.metadata.AutoConfig;
import org.ironrhino.core.struts.BaseAction;
import org.ironrhino.core.util.AppInfo;
import org.ironrhino.core.util.AppInfo.Stage;
import org.ironrhino.core.util.AuthzUtils;
import org.ironrhino.wechat.service.Wechat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@AutoConfig
public class RedirectAction extends BaseAction {

	private static final long serialVersionUID = 29792886600858873L;

	private static Logger logger = LoggerFactory.getLogger(RedirectAction.class);

	@Autowired
	private Wechat wechat;

	@Autowired
	protected UserDetailsService userDetailsService;

	@Value("${wechat.oauth.fallbackUrl:}")
	private String fallbackUrl;

	private String code;

	private String state;

	public void setCode(String code) {
		this.code = code;
	}

	public void setState(String state) {
		this.state = state;
	}

	@Override
	public String execute() throws IOException {
		UserDetails userDetails = AuthzUtils.getUserDetails();
		if (userDetails != null)
			return REDIRECT;
		if (StringUtils.isNotBlank(state)) {
			if (StringUtils.isNotBlank(code)) {
				String openid = null;
				try {
					openid = wechat.getUserInfoByCode(code).getOpenid();
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					if (AppInfo.getStage() == Stage.DEVELOPMENT) {
						openid = code;
					} else {
						addActionError("获取openid失败: " + e.getLocalizedMessage());
						return ERROR;
					}
				}
				try {
					userDetails = userDetailsService.loadUserByUsername(openid);
					AuthzUtils.autoLogin(userDetails);
					targetUrl = state;
				} catch (UsernameNotFoundException e) {
					if (StringUtils.isNotBlank(fallbackUrl)) {
						targetUrl = fallbackUrl;
						return REDIRECT;
					}
					addActionError("请先关注公众号");
					return ERROR;
				}
			} else {
				if (StringUtils.isNotBlank(fallbackUrl)) {
					targetUrl = fallbackUrl;
					return REDIRECT;
				}
				addActionError("请先关注公众号");
				return ERROR;
			}
		} else {
			if (StringUtils.isBlank(targetUrl))
				return ERROR;
			userDetails = AuthzUtils.getUserDetails();
			if (userDetails == null)
				targetUrl = wechat.buildAuthorizeUrl("/redirect", targetUrl);
		}
		return REDIRECT;
	}
}
