package org.ironrhino.wechat.service;

import java.io.File;
import java.io.OutputStream;
import java.net.SocketTimeoutException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.ironrhino.core.cache.CacheManager;
import org.ironrhino.core.util.CodecUtils;
import org.ironrhino.core.util.ErrorMessage;
import org.ironrhino.core.util.HttpClientUtils;
import org.ironrhino.core.util.JsonUtils;
import org.ironrhino.wechat.handler.WechatRequestHandler;
import org.ironrhino.wechat.model.WechatMedia;
import org.ironrhino.wechat.model.WechatMediaType;
import org.ironrhino.wechat.model.WechatMessage;
import org.ironrhino.wechat.model.WechatRequest;
import org.ironrhino.wechat.model.WechatResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;

@Component
public class Wechat {

	private static final String CACHE_NAMESPACE_ACCESSTOKEN = "ACCESSTOKEN";

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Value("${wechat.apiBaseUrl:https://api.weixin.qq.com/cgi-bin}")
	protected String apiBaseUrl;

	@Value("${wechat.token:token}")
	private String token;

	@Value("${wechat.encodingAesKey:}")
	private String encodingAesKey;

	@Value("${wechat.appId:id}")
	private String appId;

	@Value("${wechat.appSecret:secret}")
	private String appSecret;

	@Autowired
	private List<WechatRequestHandler> handlers;

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

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getAppSecret() {
		return appSecret;
	}

	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
	}

	public boolean verifySignature(String timestamp, String nonce,
			String signature) {
		if (StringUtils.isBlank(timestamp) || StringUtils.isBlank(nonce)
				|| StringUtils.isBlank(signature))
			return false;
		TreeSet<String> set = new TreeSet<String>();
		set.add(getToken());
		set.add(timestamp);
		set.add(nonce);
		return CodecUtils.shaHex(StringUtils.join(set.toArray())).equals(
				signature);
	}

	public String reply(String request) throws Exception {
		logger.info("received:\n{}", request);
		String response = "";
		WechatRequest msg = new WechatRequest(request);
		if (handlers != null) {
			for (WechatRequestHandler handler : handlers) {
				WechatResponse rmsg = handler.handle(msg);
				if (rmsg != null) {
					if (rmsg != WechatResponse.EMPTY)
						response = rmsg.toString();
					break;
				}
			}
		}
		logger.info("replied:\n{}", response);
		return response;
	}

	public void send(WechatMessage msg) throws Exception {
		String json = msg.toString();
		logger.info("sending: {}", json);
		String result = invoke("/message/custom/send", json);
		logger.info("received: {}", result);
		JsonNode node = JsonUtils.fromJson(result, JsonNode.class);
		int errcode = node.get("errcode").asInt();
		if (errcode != 0)
			throw new ErrorMessage("errcode:{0},errmsg:{1}", new Object[] {
					node.get("errcode").asText(), node.get("errmsg").asText() });
	}

	public WechatMedia upload(File file, WechatMediaType mediaType)
			throws Exception {
		if (!file.isFile())
			throw new ErrorMessage(file + " is not a file");
		if (file.length() > mediaType.getMaxFileLength())
			throw new ErrorMessage(file + " is large than "
					+ mediaType.getMaxFileLength());
		StringBuilder sb = new StringBuilder(
				"http://file.api.weixin.qq.com/cgi-bin/media/upload?access_token=");
		sb.append(fetchAccessToken()).append("&type=").append(mediaType.name());
		HttpPost httppost = new HttpPost(sb.toString());
		FileBody media = new FileBody(file);
		HttpEntity reqEntity = MultipartEntityBuilder.create()
				.addPart("media", media).build();
		httppost.setEntity(reqEntity);
		logger.info("uploading: " + file);
		CloseableHttpClient httpClient = HttpClientUtils.create(true, 20000);
		String result = httpClient
				.execute(httppost, new BasicResponseHandler());
		logger.info("received: " + result);
		JsonNode node = JsonUtils.fromJson(result, JsonNode.class);
		httpClient.close();
		if (node.has("errcode"))
			throw new ErrorMessage("errcode:{0},errmsg:{1}", new Object[] {
					node.get("errcode").asText(), node.get("errmsg").asText() });
		return new WechatMedia(result);
	}

	public void download(String mediaId, OutputStream os) throws Exception {
		StringBuilder sb = new StringBuilder(
				"http://file.api.weixin.qq.com/cgi-bin/media/get?access_token=");
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
			String result = StringUtils.join(
					IOUtils.readLines(entity.getContent()), "\n");
			logger.info("received: " + result);
			JsonNode node = JsonUtils.fromJson(result, JsonNode.class);
			response.close();
			httpClient.close();
			if (node.has("errcode"))
				throw new ErrorMessage("errcode:{0},errmsg:{1}", new Object[] {
						node.get("errcode").asText(),
						node.get("errmsg").asText() });
		}
		IOUtils.copy(entity.getContent(), os);
		response.close();
		httpClient.close();
	}

	public void download(String mediaId, HttpServletResponse resp)
			throws Exception {
		StringBuilder sb = new StringBuilder(
				"http://file.api.weixin.qq.com/cgi-bin/media/get?access_token=");
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

	protected String invoke(String path, String request, int retryTimes)
			throws Exception {
		StringBuilder sb = new StringBuilder(apiBaseUrl);
		sb.append(path).append(path.indexOf('?') > -1 ? "&" : "?")
				.append("access_token=").append(fetchAccessToken());
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
						cacheManager.delete(getAppId(),
								CACHE_NAMESPACE_ACCESSTOKEN);
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

	public String invoke(String path, String request) throws Exception {
		return invoke(path, request, 3);
	}

	protected String fetchAccessToken() throws Exception {
		String accessToken = (String) cacheManager.get(getAppId(),
				CACHE_NAMESPACE_ACCESSTOKEN);
		if (accessToken != null)
			return accessToken;
		Map<String, String> params = new HashMap<>();
		params.put("grant_type", "client_credential");
		params.put("appid", getAppId());
		params.put("secret", getAppSecret());
		String result = HttpClientUtils.getResponseText(apiBaseUrl + "/token",
				params);
		logger.info("fetchAccessToken received: {}", result);
		JsonNode node = JsonUtils.fromJson(result, JsonNode.class);
		if (node.has("errcode"))
			throw new ErrorMessage("errcode:{0},errmsg:{1}", new Object[] {
					node.get("errcode").asText(), node.get("errmsg").asText() });
		accessToken = node.get("access_token").textValue();
		int expiresIn = node.get("expires_in").asInt();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.SECOND, expiresIn - 60);
		cacheManager.put(getAppId(), accessToken, expiresIn - 60,
				TimeUnit.SECONDS, CACHE_NAMESPACE_ACCESSTOKEN);
		return accessToken;
	}

}
