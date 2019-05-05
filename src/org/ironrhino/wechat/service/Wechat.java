package org.ironrhino.wechat.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.ironrhino.core.cache.CacheManager;
import org.ironrhino.core.metrics.Metrics;
import org.ironrhino.core.servlet.RequestContext;
import org.ironrhino.core.util.AppInfo;
import org.ironrhino.core.util.CodecUtils;
import org.ironrhino.core.util.ErrorMessage;
import org.ironrhino.core.util.JsonUtils;
import org.ironrhino.core.util.RequestUtils;
import org.ironrhino.wechat.handler.WechatRequestHandler;
import org.ironrhino.wechat.model.WechatAllMessage;
import org.ironrhino.wechat.model.WechatMedia;
import org.ironrhino.wechat.model.WechatMediaType;
import org.ironrhino.wechat.model.WechatMessage;
import org.ironrhino.wechat.model.WechatNewsMessage;
import org.ironrhino.wechat.model.WechatRequest;
import org.ironrhino.wechat.model.WechatResponse;
import org.ironrhino.wechat.model.WechatTemplateMessage;
import org.ironrhino.wechat.model.WechatUserInfo;
import org.ironrhino.wechat.util.HttpClientHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.Getter;
import lombok.Setter;

@Component
public class Wechat {

	private static final String CACHE_NAMESPACE_ACCESSTOKEN = "ACCESSTOKEN";
	private static final String CACHE_NAMESPACE_JSAPITICKET = "JSAPITOKEN";

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Value("${wechat.baseUrl:}")
	protected String baseUrl;

	@Value("${base:}")
	private String base;

	@Getter
	@Value("${wechat.apiBaseUrl:https://api.weixin.qq.com/cgi-bin}")
	protected String apiBaseUrl;

	@Getter
	@Setter
	@Value("${wechat.token:token}")
	private String token;

	@Getter
	@Setter
	@Value("${wechat.encodingAesKey:}")
	private String encodingAesKey;

	@Getter
	@Setter
	@Value("${wechat.appId:id}")
	private String appId;

	@Getter
	@Setter
	@Value("${wechat.appSecret:secret}")
	private String appSecret;

	@Value("${wechat.industryId1:1}")
	private String industryId1 = "1";

	@Value("${wechat.industryId2:4}")
	private String industryId2 = "4";

	@Autowired
	private List<WechatRequestHandler> handlers;

	@Autowired
	private CacheManager cacheManager;

	@Value("${wechat.connectionRequestTimeout:5000}")
	private int connectionRequestTimeout = 5000;

	@Value("${wechat.connectTimeout:5000}")
	private int connectTimeout = 5000;

	@Value("${wechat.socketTimeout:10000}")
	private int socketTimeout;

	@Getter
	private CloseableHttpClient httpClient;

	@PostConstruct
	private void init() {
		RequestConfig requestConfig = RequestConfig.custom().setCircularRedirectsAllowed(true)
				.setConnectionRequestTimeout(connectionRequestTimeout).setConnectTimeout(connectTimeout)
				.setSocketTimeout(socketTimeout).setExpectContinueEnabled(true).build();
		httpClient = HttpClients.custom().disableAuthCaching().disableAutomaticRetries().disableConnectionState()
				.disableCookieManagement().setConnectionTimeToLive(60, TimeUnit.SECONDS)
				.setRetryHandler(
						(e, executionCount, httpCtx) -> executionCount < 3 && e instanceof NoHttpResponseException)
				.setDefaultRequestConfig(requestConfig).build();
	}

	@PreDestroy
	private void destroy() {
		if (httpClient != null)
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

	public boolean verifySignature(String timestamp, String nonce, String signature) {
		if (StringUtils.isBlank(timestamp) || StringUtils.isBlank(nonce) || StringUtils.isBlank(signature))
			return false;
		TreeSet<String> set = new TreeSet<String>();
		set.add(getToken());
		set.add(timestamp);
		set.add(nonce);
		return CodecUtils.shaHex(StringUtils.join(set.toArray())).equals(signature);
	}

	public String reply(String request) {
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

	@Retryable(include = IOException.class, backoff = @Backoff(delay = 1000, maxDelay = 5000, multiplier = 2))
	public Long send(WechatMessage msg) throws IOException {
		String json = msg.toString();
		String result = invoke("/message/custom/send", json);
		JsonNode node = JsonUtils.fromJson(result, JsonNode.class);
		int errcode = node.get("errcode").asInt();
		if (errcode != 0)
			throw new ErrorMessage("errcode:{0},errmsg:{1}",
					new Object[] { node.get("errcode").asText(), node.get("errmsg").asText() });
		else if (node.get("msg_id") != null)
			return node.get("msg_id").asLong();
		else
			return null;
	}

	@Retryable(include = IOException.class, backoff = @Backoff(delay = 1000, maxDelay = 5000, multiplier = 2))
	public Long sendTemplate(WechatTemplateMessage msg) throws IOException {
		String json = msg.toString();
		String result = invoke("/message/template/send", json);
		JsonNode node = JsonUtils.fromJson(result, JsonNode.class);
		int errcode = node.get("errcode").asInt();
		if (errcode != 0)
			throw new ErrorMessage("errcode:{0},errmsg:{1}",
					new Object[] { node.get("errcode").asText(), node.get("errmsg").asText() });
		else if (node.get("msgid") != null)
			return node.get("msgid").asLong();
		else
			return null;
	}

	@Retryable(include = IOException.class, backoff = @Backoff(delay = 1000, maxDelay = 5000, multiplier = 2))
	public Long sendAll(WechatAllMessage msg) throws IOException {
		String json = msg.toString();
		String result = invoke("/message/mass/sendall", json);
		JsonNode node = JsonUtils.fromJson(result, JsonNode.class);
		int errcode = node.get("errcode").asInt();
		if (errcode != 0)
			throw new ErrorMessage("errcode:{0},errmsg:{1}",
					new Object[] { node.get("errcode").asText(), node.get("errmsg").asText() });
		else if (node.get("msg_id") != null)
			return node.get("msg_id").asLong();
		else
			return null;
	}

	@Retryable(include = IOException.class, backoff = @Backoff(delay = 1000, maxDelay = 5000, multiplier = 2))
	public void cancelSendAll(String msg_id) throws IOException {
		Map<String, String> map = new LinkedHashMap<>();
		map.put("msg_id", msg_id);
		String request = JsonUtils.toJson(map);
		String result = invoke("/message/mass/delete", request);
		JsonNode node = JsonUtils.fromJson(result, JsonNode.class);
		int errcode = node.get("errcode").asInt();
		if (errcode != 0)
			throw new ErrorMessage("errcode:{0},errmsg:{1}",
					new Object[] { node.get("errcode").asText(), node.get("errmsg").asText() });
	}

	public WechatMedia upload(File file, WechatMediaType mediaType) throws IOException {
		if (!file.isFile())
			throw new ErrorMessage(file + " is not a file");
		if (file.length() > mediaType.getMaxFileLength())
			throw new ErrorMessage(file + " is large than " + mediaType.getMaxFileLength());
		long time = System.currentTimeMillis();
		boolean timeout = false;
		try {
			int attempts = 3;
			while (attempts-- > 0) {
				StringBuilder sb = new StringBuilder(
						"http://file.api.weixin.qq.com/cgi-bin/media/upload?access_token=");
				sb.append(fetchAccessToken()).append("&type=").append(mediaType.name());
				HttpPost httppost = new HttpPost(sb.toString());
				FileBody media = new FileBody(file);
				HttpEntity reqEntity = MultipartEntityBuilder.create().setMode(HttpMultipartMode.RFC6532)
						.addPart("media", media).build();
				httppost.setEntity(reqEntity);
				logger.info("uploading: " + file);
				String result = httpClient.execute(httppost, new BasicResponseHandler());
				logger.info("received: " + result);
				JsonNode node = JsonUtils.fromJson(result, JsonNode.class);
				if (node.has("errcode")) {
					int errcode = node.get("errcode").asInt();
					if (errcode == 40001) {
						cacheManager.delete(getAppId(), CACHE_NAMESPACE_ACCESSTOKEN);
						continue;
					}
					throw new ErrorMessage("errcode:{0},errmsg:{1}",
							new Object[] { node.get("errcode").asText(), node.get("errmsg").asText() });
				}
				return new WechatMedia(result);
			}
			throw new ErrorMessage("max attempts reached");
		} catch (IOException e) {
			timeout = (e instanceof SocketTimeoutException || e.getCause() instanceof SocketTimeoutException);
			throw e;
		} finally {
			record(time, "post", "/media/upload", timeout);
		}
	}

	public WechatMedia uploadVideo(String media_id, String title, String description) throws IOException {
		long time = System.currentTimeMillis();
		boolean timeout = false;
		try {
			int attempts = 3;
			while (attempts-- > 0) {
				String url = "http://file.api.weixin.qq.com/cgi-bin/media/uploadvideo?access_token="
						+ fetchAccessToken();
				Map<String, String> map = new LinkedHashMap<>();
				map.put("media_id", media_id);
				map.put("title", title);
				map.put("description", description);
				String request = JsonUtils.toJson(map);
				logger.info("post to {}: {}", url, request);
				String result = HttpClientHelper.post(httpClient, url, request);
				logger.info("received: " + result);
				JsonNode node = JsonUtils.fromJson(result, JsonNode.class);
				if (node.has("errcode")) {
					int errcode = node.get("errcode").asInt();
					if (errcode == 40001) {
						cacheManager.delete(getAppId(), CACHE_NAMESPACE_ACCESSTOKEN);
						continue;
					}
					throw new ErrorMessage("errcode:{0},errmsg:{1}",
							new Object[] { node.get("errcode").asText(), node.get("errmsg").asText() });
				}
				return new WechatMedia(result);
			}
			throw new ErrorMessage("max attempts reached");
		} catch (IOException e) {
			timeout = (e instanceof SocketTimeoutException || e.getCause() instanceof SocketTimeoutException);
			throw e;
		} finally {
			record(time, "post", "/media/uploadvideo", timeout);
		}
	}

	protected void doDownload(String mediaId, OutputStream os, Consumer<HttpEntity> consumer) throws IOException {
		long time = System.currentTimeMillis();
		boolean timeout = false;
		try {
			int attempts = 3;
			while (attempts-- > 0) {
				StringBuilder sb = new StringBuilder("http://file.api.weixin.qq.com/cgi-bin/media/get?access_token=");
				sb.append(fetchAccessToken()).append("&media_id=").append(mediaId);
				HttpGet httpGet = new HttpGet(sb.toString());
				CloseableHttpResponse response = httpClient.execute(httpGet);
				HttpEntity entity = response.getEntity();
				Header header = entity.getContentType();
				String contentType = "";
				if (header != null && header.getValue() != null)
					contentType = header.getValue();
				if (contentType.startsWith("text/plain") || contentType.startsWith("application/json")) {
					BufferedReader br = new BufferedReader(
							new InputStreamReader(entity.getContent(), StandardCharsets.UTF_8));
					String result = br.lines().collect(Collectors.joining("\n"));
					logger.info("received: " + result);
					try {
						JsonNode node = JsonUtils.fromJson(result, JsonNode.class);
						response.close();
						if (node.has("errcode")) {
							int errcode = node.get("errcode").asInt();
							if (errcode == 40001) {
								cacheManager.delete(getAppId(), CACHE_NAMESPACE_ACCESSTOKEN);
								continue;
							}
							throw new ErrorMessage("errcode:{0},errmsg:{1}",
									new Object[] { node.get("errcode").asText(), node.get("errmsg").asText() });
						}
					} catch (JsonProcessingException e) {
						// if file is normal text
					}
				}
				if (consumer != null)
					consumer.accept(entity);
				StreamUtils.copy(entity.getContent(), os);
				response.close();
				return;
			}
			throw new ErrorMessage("max attempts reached");
		} catch (IOException e) {
			timeout = (e instanceof SocketTimeoutException || e.getCause() instanceof SocketTimeoutException);
			throw e;
		} finally {
			record(time, "get", "/media/get", timeout);
		}
	}

	public void download(String mediaId, OutputStream os) throws IOException {
		doDownload(mediaId, os, entity -> {
		});
	}

	public void download(String mediaId, HttpServletResponse resp) throws IOException {
		doDownload(mediaId, resp.getOutputStream(), entity -> {
			Header header = entity.getContentType();
			if (header != null && header.getValue() != null)
				resp.setHeader(header.getName(), header.getValue());
			long contentLength = entity.getContentLength();
			if (contentLength > 0)
				resp.setIntHeader("Content-Length", (int) contentLength);
			resp.setHeader("Cache-Control", "max-age=86400");
		});
	}

	@Retryable(include = IOException.class, backoff = @Backoff(delay = 1000, maxDelay = 5000, multiplier = 2))
	public WechatMedia uploadNews(WechatNewsMessage msg) throws IOException {
		String json = msg.toString();
		String result = invoke("/media/uploadnews", json);
		JsonNode node = JsonUtils.fromJson(result, JsonNode.class);
		if (node.has("errcode"))
			throw new ErrorMessage("errcode:{0},errmsg:{1}",
					new Object[] { node.get("errcode").asText(), node.get("errmsg").asText() });
		return new WechatMedia(result);
	}

	public void setIndustry() throws IOException {
		Map<String, String> msg = new LinkedHashMap<String, String>();
		if (StringUtils.isNotBlank(industryId1))
			msg.put("industry_id1", industryId1);
		if (StringUtils.isNotBlank(industryId2))
			msg.put("industry_id2", industryId2);
		if (!msg.isEmpty()) {
			String json = JsonUtils.toJson(msg);
			String result = invoke("/template/api_set_industry", json);
			JsonNode node = JsonUtils.fromJson(result, JsonNode.class);
			int errcode = node.get("errcode").asInt();
			if (errcode != 0)
				throw new ErrorMessage("errcode:{0},errmsg:{1}",
						new Object[] { node.get("errcode").asText(), node.get("errmsg").asText() });
		}
	}

	protected String invoke(String path, String request, int retryTimes) throws IOException {
		StringBuilder sb = new StringBuilder(apiBaseUrl);
		sb.append(path).append(path.indexOf('?') > -1 ? "&" : "?").append("access_token=").append(fetchAccessToken());
		String url = sb.toString();
		String result;
		long time = System.currentTimeMillis();
		boolean timeout = false;
		try {
			if (request != null) {
				logger.info("post to {}: {}", url, request);
				result = HttpClientHelper.post(httpClient, url, request);
				logger.info("post received: {}", result);
			} else {
				logger.info("get  {}", url);
				result = HttpClientHelper.get(httpClient, url);
				logger.info("get received: {}", result);
			}
			if (retryTimes > 0 && result.indexOf("\"errcode\"") > -1) {
				try {
					JsonNode node = JsonUtils.fromJson(result, JsonNode.class);
					int errcode = node.get("errcode").asInt();
					if (errcode == -1) {
						return invoke(path, request, --retryTimes);
					} else if (errcode == 40001) {
						cacheManager.delete(getAppId(), CACHE_NAMESPACE_ACCESSTOKEN);
						return invoke(path, request, --retryTimes);
					}
				} catch (Exception e) {

				}
			}
			return result;
		} catch (IOException e) {
			timeout = (e instanceof SocketTimeoutException || e.getCause() instanceof SocketTimeoutException);
			if (retryTimes > 0)
				return invoke(path, request, --retryTimes);
			else
				throw e;
		} finally {
			record(time, (request != null) ? "post" : "get",
					path.indexOf('?') > 0 ? path.substring(0, path.indexOf('?')) : path, timeout);
		}
	}

	public String invoke(String path, String request) throws IOException {
		return invoke(path, request, 3);
	}

	public String fetchAccessToken() throws IOException {
		String accessToken = (String) cacheManager.get(getAppId(), CACHE_NAMESPACE_ACCESSTOKEN);
		if (accessToken != null)
			return accessToken;
		Map<String, String> params = new HashMap<>();
		params.put("grant_type", "client_credential");
		params.put("appid", getAppId());
		params.put("secret", getAppSecret());
		String path = "/token";
		long time = System.currentTimeMillis();
		boolean timeout = false;
		try {
			String result = HttpClientHelper.getResponseText(httpClient, apiBaseUrl + path, params);
			logger.info("fetchAccessToken received: {}", result);
			JsonNode node = JsonUtils.fromJson(result, JsonNode.class);
			if (node.has("errcode"))
				throw new ErrorMessage("errcode:{0},errmsg:{1}",
						new Object[] { node.get("errcode").asText(), node.get("errmsg").asText() });
			accessToken = node.get("access_token").textValue();
			int expiresIn = node.get("expires_in").asInt();
			cacheManager.put(getAppId(), accessToken, expiresIn > 60 ? expiresIn - 60 : expiresIn, TimeUnit.SECONDS,
					CACHE_NAMESPACE_ACCESSTOKEN);
			return accessToken;
		} catch (IOException e) {
			timeout = (e instanceof SocketTimeoutException || e.getCause() instanceof SocketTimeoutException);
			throw e;
		} finally {
			record(time, "get", path, timeout);
		}
	}

	public String getJsApiTicket() throws IOException {
		String jsApiTicket = (String) cacheManager.get(getAppId(), CACHE_NAMESPACE_JSAPITICKET);
		if (jsApiTicket != null)
			return jsApiTicket;
		Map<String, String> params = new HashMap<>();
		params.put("access_token", fetchAccessToken());
		params.put("type", "jsapi");
		String path = "/ticket/getticket";
		long time = System.currentTimeMillis();
		boolean timeout = false;
		try {
			String result = HttpClientHelper.getResponseText(httpClient, apiBaseUrl + path, params);
			logger.info("getJsApiToken received: {}", result);
			JsonNode node = JsonUtils.fromJson(result, JsonNode.class);
			if (node.has("errcode") && node.get("errcode").asInt() > 0)
				throw new ErrorMessage("errcode:{0},errmsg:{1}",
						new Object[] { node.get("errcode").asText(), node.get("errmsg").asText() });
			jsApiTicket = node.get("ticket").textValue();
			int expiresIn = node.get("expires_in").asInt();
			cacheManager.put(getAppId(), jsApiTicket, expiresIn > 60 ? expiresIn - 60 : expiresIn, TimeUnit.SECONDS,
					CACHE_NAMESPACE_JSAPITICKET);
			return jsApiTicket;
		} catch (IOException e) {
			timeout = (e instanceof SocketTimeoutException || e.getCause() instanceof SocketTimeoutException);
			throw e;
		} finally {
			record(time, "get", path, timeout);
		}
	}

	public String getJsApiSignature(String jsapi_ticket, String noncestr, String timestamp, String url) {
		StringBuilder sb = new StringBuilder("jsapi_ticket=");
		sb.append(jsapi_ticket).append("&noncestr=").append(noncestr).append("&timestamp=").append(timestamp)
				.append("&url=").append(url);
		return CodecUtils.shaHex(sb.toString());
	}

	public String absolutizeUri(String uri) {
		if (uri.indexOf("://") < 0) {
			StringBuilder rurl = new StringBuilder();
			if (StringUtils.isNotBlank(baseUrl))
				rurl.append(baseUrl);
			else if (StringUtils.isNotBlank(base))
				rurl.append(base);
			else if (RequestContext.getRequest() != null)
				rurl.append(RequestUtils.getBaseUrl(RequestContext.getRequest()));
			else
				rurl.append("http://").append(AppInfo.getHostAddress());
			rurl.append(uri);
			uri = rurl.toString();
		}
		return uri;
	}

	public String buildAuthorizeUrl(String redirect_uri) throws IOException {
		return buildAuthorizeUrl(redirect_uri, null);
	}

	public String buildAuthorizeUrl(String redirect_uri, String state) throws IOException {
		StringBuilder sb = new StringBuilder();
		sb.append("https://open.weixin.qq.com/connect/oauth2/authorize?appid=").append(getAppId());
		sb.append("&redirect_uri=").append(URLEncoder.encode(absolutizeUri(redirect_uri), "utf-8"));
		sb.append("&response_type=code&scope=snsapi_base");
		if (StringUtils.isNotBlank(state))
			sb.append("&state=").append(URLEncoder.encode(state, "utf-8"));
		sb.append("#wechat_redirect");
		return sb.toString();
	}

	public WechatUserInfo getUserInfoByCode(String code) throws IOException {
		long time = System.currentTimeMillis();
		boolean timeout = false;
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("https://api.weixin.qq.com/sns/oauth2/access_token?appid=").append(getAppId());
			sb.append("&secret=").append(getAppSecret());
			sb.append("&code=").append(code);
			sb.append("&grant_type=authorization_code");
			String result = HttpClientHelper.getResponseText(httpClient, sb.toString());
			logger.info("getUserInfoByCode received: {}", result);
			JsonNode node = JsonUtils.fromJson(result, JsonNode.class);
			if (node.has("errcode"))
				throw new ErrorMessage("errcode:{0},errmsg:{1}",
						new Object[] { node.get("errcode").asText(), node.get("errmsg").asText() });
			return JsonUtils.fromJson(result, WechatUserInfo.class);
		} catch (IOException e) {
			timeout = (e instanceof SocketTimeoutException || e.getCause() instanceof SocketTimeoutException);
			throw e;
		} finally {
			record(time, "get", "/sns/oauth2/access_token", timeout);
		}
	}

	private static void record(long startTimestamp, String method, String path, boolean timeout) {
		Metrics.recordTimer(micrometerTimerName, System.currentTimeMillis() - startTimestamp, TimeUnit.MILLISECONDS,
				"method", method, "path", path, "timeout", String.valueOf(timeout));
	}

	private static String micrometerTimerName = "wechat.calls";

}
