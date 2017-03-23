package org.ironrhino.wechat.component;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.ironrhino.core.spring.security.LoginEntryPointHandler;
import org.ironrhino.core.util.RequestUtils;
import org.ironrhino.wechat.service.Wechat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WechatLoginEntryPointHandler implements LoginEntryPointHandler {

	@Autowired
	private Wechat wechat;

	@Override
	public String handle(HttpServletRequest request, String targetUrl) {
		String ua = request.getHeader("User-Agent");
		if (ua != null) {
			ua = ua.toLowerCase();
			if (ua.contains("micromessenger") || ua.contains("mqqbrowser")) {
				try {
					String redirect_uri = RequestUtils.getBaseUrl(request) + "/redirect";
					return wechat.buildAuthorizeUrl(redirect_uri, targetUrl);
				} catch (IOException e) {
					e.printStackTrace();
					return null;
				}
			}
		}
		return null;
	}

}
