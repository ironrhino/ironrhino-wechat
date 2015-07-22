package org.ironrhino.corpwechat.service;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.ironrhino.core.cache.CacheManager;
import org.ironrhino.core.util.CodecUtils;
import org.ironrhino.core.util.ErrorMessage;
import org.ironrhino.core.util.HttpClientUtils;
import org.ironrhino.core.util.JsonUtils;
import org.ironrhino.corpwechat.handler.CorpWechatRequestHandler;
import org.ironrhino.corpwechat.model.CorpWechatMedia;
import org.ironrhino.corpwechat.model.CorpWechatMediaType;
import org.ironrhino.corpwechat.model.CorpWechatMessage;
import org.ironrhino.corpwechat.model.CorpWechatRequest;
import org.ironrhino.corpwechat.model.CorpWechatResponse;
import org.ironrhino.corpwechat.model.CorpWechatUserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;

@Component
public class CorpWechat {

	private static final String CACHE_NAMESPACE_ACCESSTOKEN = "ACCESSTOKEN";
	private static final String CACHE_NAMESPACE_JSAPITICKET = "JSAPITOKEN";

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Value("${corpWechat.apiBaseUrl:https://qyapi.weixin.qq.com/cgi-bin}")
	protected String apiBaseUrl;

	@Value("${corpWechat.token:token}")
	private String token;

	@Value("${corpWechat.encodingAesKey:}")
	private String encodingAesKey;

	@Value("${corpWechat.corpId:id}")
	private String corpId;

	@Value("${corpWechat.corpSecret:secret}")
	private String corpSecret;

	@Autowired
	private List<CorpWechatRequestHandler> handlers;

	@Autowired
	private CacheManager cacheManager;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getEncodingAesKey() {
		return encodingAesKey;
	}

	public void setEncodingAesKey(String encodingAesKey) {
		this.encodingAesKey = encodingAesKey;
	}

	public String getCorpId() {
		return corpId;
	}

	public void setCorpId(String corpId) {
		this.corpId = corpId;
	}

	public String getCorpSecret() {
		return corpSecret;
	}

	public void setCorpSecret(String corpSecret) {
		this.corpSecret = corpSecret;
	}

	public String reply(String request) {
		logger.info("received:\n{}", request);
		String response = "";
		CorpWechatRequest msg = new CorpWechatRequest(request);
		if (handlers != null) {
			for (CorpWechatRequestHandler handler : handlers) {
				CorpWechatResponse rmsg = handler.handle(msg);
				if (rmsg != null) {
					if (rmsg != CorpWechatResponse.EMPTY)
						response = rmsg.toString();
					break;
				}
			}
		}
		logger.info("replied:\n{}", response);
		return response;
	}

	@Retryable(include = IOException.class, backoff = @Backoff(delay = 1000, maxDelay = 5000, multiplier = 2))
	public void send(CorpWechatMessage msg) throws IOException {
		String json = msg.toString();
		logger.info("sending: {}", json);
		String result = invoke("/message/send", json);
		logger.info("received: {}", result);
		JsonNode node = JsonUtils.fromJson(result, JsonNode.class);
		int errcode = node.get("errcode").asInt();
		if (errcode != 0)
			throw new ErrorMessage("errcode:{0},errmsg:{1}", new Object[] { node.get("errcode").asText(),
					node.get("errmsg").asText() });
	}

	public CorpWechatMedia upload(File file, CorpWechatMediaType mediaType) throws IOException {
		if (!file.isFile())
			throw new ErrorMessage(file + " is not a file");
		if (file.length() > mediaType.getMaxFileLength())
			throw new ErrorMessage(file + " is large than " + mediaType.getMaxFileLength());
		StringBuilder sb = new StringBuilder(apiBaseUrl);
		sb.append("/media/upload?access_token=");
		sb.append(fetchAccessToken()).append("&type=").append(mediaType.name());
		HttpPost httppost = new HttpPost(sb.toString());
		FileBody media = new FileBody(file);
		HttpEntity reqEntity = MultipartEntityBuilder.create().setMode(HttpMultipartMode.RFC6532)
				.addPart("media", media).build();
		httppost.setEntity(reqEntity);
		logger.info("uploading: " + file);
		CloseableHttpClient httpClient = HttpClientUtils.create(true, 20000);
		String result = httpClient.execute(httppost, new BasicResponseHandler());
		logger.info("received: " + result);
		JsonNode node = JsonUtils.fromJson(result, JsonNode.class);
		httpClient.close();
		if (node.has("errcode"))
			throw new ErrorMessage("errcode:{0},errmsg:{1}", new Object[] { node.get("errcode").asText(),
					node.get("errmsg").asText() });
		return new CorpWechatMedia(result);
	}

	public void download(String mediaId, OutputStream os) throws IOException {
		StringBuilder sb = new StringBuilder(apiBaseUrl);
		sb.append("/media/get?access_token=");
		sb.append(fetchAccessToken()).append("&media_id=").append(mediaId);
		HttpGet httpGet = new HttpGet(sb.toString());
		CloseableHttpClient httpClient = HttpClientUtils.create(true, 20000);
		CloseableHttpResponse response = httpClient.execute(httpGet);
		HttpEntity entity = response.getEntity();
		Header header = entity.getContentType();
		String contentType = "";
		if (header != null && header.getValue() != null)
			contentType = header.getValue();
		if (contentType.startsWith("text/")) {
			String result = StringUtils.join(IOUtils.readLines(entity.getContent()), "\n");
			logger.info("received: " + result);
			JsonNode node = JsonUtils.fromJson(result, JsonNode.class);
			response.close();
			httpClient.close();
			if (node.has("errcode"))
				throw new ErrorMessage("errcode:{0},errmsg:{1}", new Object[] { node.get("errcode").asText(),
						node.get("errmsg").asText() });
		}
		IOUtils.copy(entity.getContent(), os);
		response.close();
		httpClient.close();
	}

	public void download(String mediaId, HttpServletResponse resp) throws IOException {
		StringBuilder sb = new StringBuilder(apiBaseUrl);
		sb.append("/media/get?access_token=");
		sb.append(fetchAccessToken()).append("&media_id=").append(mediaId);
		HttpGet httpGet = new HttpGet(sb.toString());
		CloseableHttpClient httpClient = HttpClientUtils.create(true, 20000);
		CloseableHttpResponse response = httpClient.execute(httpGet);
		HttpEntity entity = response.getEntity();
		Header header = entity.getContentType();
		if (header != null && header.getValue() != null)
			resp.setHeader(header.getName(), header.getValue());
		long contentLength = entity.getContentLength();
		if (contentLength > 0)
			resp.setIntHeader("Content-Length", (int) contentLength);
		resp.setHeader("Cache-Control", "max-age=86400");
		IOUtils.copy(entity.getContent(), resp.getOutputStream());
		response.close();
		httpClient.close();
	}

	protected String invoke(String path, String request, int retryTimes) throws IOException {
		StringBuilder sb = new StringBuilder(apiBaseUrl);
		sb.append(path).append(path.indexOf('?') > -1 ? "&" : "?").append("access_token=").append(fetchAccessToken());
		String url = sb.toString();
		String result;
		try {
			if (request != null) {
				logger.info("post to {}: {}", url, request);
				result = HttpClientUtils.post(url, request);
				logger.info("post received: {}", result);
			} else {
				logger.info("get  {}", url);
				result = HttpClientUtils.get(url);
				logger.info("get received: {}", result);
			}
			if (retryTimes > 0 && result.indexOf("\"errcode\"") > -1) {
				try {
					JsonNode node = JsonUtils.fromJson(result, JsonNode.class);
					int errcode = node.get("errcode").asInt();
					if (errcode == -1) {
						return invoke(path, request, --retryTimes);
					} else if (errcode == 40001) {
						cacheManager.delete(getCorpId(), CACHE_NAMESPACE_ACCESSTOKEN);
						return invoke(path, request, --retryTimes);
					}
				} catch (Exception e) {

				}
			}
			return result;
		} catch (SocketTimeoutException e) {
			if (retryTimes > 0)
				return invoke(path, request, --retryTimes);
			else
				throw e;
		}

	}

	public String invoke(String path, String request) throws IOException {
		return invoke(path, request, 3);
	}

	protected String fetchAccessToken() throws IOException {
		String accessToken = (String) cacheManager.get(getCorpId(), CACHE_NAMESPACE_ACCESSTOKEN);
		if (accessToken != null)
			return accessToken;
		Map<String, String> params = new HashMap<>();
		params.put("corpid", getCorpId());
		params.put("corpsecret", getCorpSecret());
		String result = HttpClientUtils.getResponseText(apiBaseUrl + "/gettoken", params);
		logger.info("fetchAccessToken received: {}", result);
		JsonNode node = JsonUtils.fromJson(result, JsonNode.class);
		if (node.has("errcode"))
			throw new ErrorMessage("errcode:{0},errmsg:{1}", new Object[] { node.get("errcode").asText(),
					node.get("errmsg").asText() });
		accessToken = node.get("access_token").textValue();
		int expiresIn = node.get("expires_in").asInt();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.SECOND, expiresIn - 60);
		cacheManager.put(getCorpId(), accessToken, expiresIn - 60, TimeUnit.SECONDS, CACHE_NAMESPACE_ACCESSTOKEN);
		return accessToken;
	}

	public String getJsApiTicket() throws IOException {
		String jsApiTicket = (String) cacheManager.get(getCorpId(), CACHE_NAMESPACE_JSAPITICKET);
		if (jsApiTicket != null)
			return jsApiTicket;
		Map<String, String> params = new HashMap<>();
		params.put("access_token", fetchAccessToken());
		String result = HttpClientUtils.getResponseText(apiBaseUrl + "/get_jsapi_ticket", params);
		logger.info("getJsApiToken received: {}", result);
		JsonNode node = JsonUtils.fromJson(result, JsonNode.class);
		if (node.has("errcode"))
			throw new ErrorMessage("errcode:{0},errmsg:{1}", new Object[] { node.get("errcode").asText(),
					node.get("errmsg").asText() });
		jsApiTicket = node.get("ticket").textValue();
		int expiresIn = node.get("expires_in").asInt();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.SECOND, expiresIn - 60);
		cacheManager.put(getCorpId(), jsApiTicket, expiresIn - 60, TimeUnit.SECONDS, CACHE_NAMESPACE_JSAPITICKET);
		return jsApiTicket;
	}

	public String getJsApiSignature(String jsapi_ticket, String noncestr, String timestamp, String url) {
		StringBuilder sb = new StringBuilder("jsapi_ticket=");
		sb.append(jsapi_ticket).append("&noncestr=").append(noncestr).append("&timestamp=").append(timestamp)
				.append("&url=").append(url);
		return CodecUtils.shaHex(sb.toString());
	}

	public String buildAuthorizeUrl(String redirect_uri) throws IOException {
		return buildAuthorizeUrl(redirect_uri, null);
	}

	public String buildAuthorizeUrl(String redirect_uri, String state) throws IOException {
		StringBuilder sb = new StringBuilder();
		sb.append("https://open.weixin.qq.com/connect/oauth2/authorize?response_type=code&scope=snsapi_base");
		sb.append("&appid=").append(getCorpId());
		sb.append("&redirect_uri=").append(URLEncoder.encode(redirect_uri, "utf-8"));
		if (StringUtils.isNotBlank(state))
			sb.append("&state=").append(URLEncoder.encode(state, "utf-8"));
		sb.append("#wechat_redirect");
		return sb.toString();
	}

	public CorpWechatUserInfo getUserInfoByCode(String code) throws IOException {
		StringBuilder sb = new StringBuilder();
		sb.append("https://qyapi.weixin.qq.com/cgi-bin/user/getuserinfo");
		sb.append("?access_token=").append(fetchAccessToken());
		sb.append("&code=").append(code);
		String result = HttpClientUtils.getResponseText(sb.toString());
		logger.info("getUserInfoByCode received: {}", result);
		JsonNode node = JsonUtils.fromJson(result, JsonNode.class);
		if (node.has("errcode"))
			throw new ErrorMessage("errcode:{0},errmsg:{1}", new Object[] { node.get("errcode").asText(),
					node.get("errmsg").asText() });
		CorpWechatUserInfo info = new CorpWechatUserInfo();
		if (node.has("UserId"))
			info.setUserid(node.get("UserId").asText());
		if (node.has("OpenId"))
			info.setOpenid(node.get("OpenId").asText());
		if (node.has("DeviceId"))
			info.setDeviceid(node.get("DeviceId").asText());
		return info;
	}

}
