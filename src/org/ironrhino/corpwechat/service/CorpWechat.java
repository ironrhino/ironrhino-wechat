package org.ironrhino.corpwechat.service;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
import org.ironrhino.core.util.AppInfo;
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
import org.springframework.util.ClassUtils;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.Getter;
import lombok.Setter;

@Component
public class CorpWechat {

	private static final String CACHE_NAMESPACE_ACCESSTOKEN = "ACCESSTOKEN";
	private static final String CACHE_NAMESPACE_JSAPITICKET = "JSAPITOKEN";

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Value("${corpWechat.baseUrl:}")
	protected String baseUrl;

	@Value("${base:}")
	private String base;

	@Value("${corpWechat.apiBaseUrl:https://qyapi.weixin.qq.com/cgi-bin}")
	protected String apiBaseUrl;

	@Getter
	@Setter
	@Value("${corpWechat.token:token}")
	private String token;

	@Getter
	@Setter
	@Value("${corpWechat.encodingAesKey:}")
	private String encodingAesKey;

	@Getter
	@Setter
	@Value("${corpWechat.corpId:id}")
	private String corpId;

	@Getter
	@Setter
	@Value("${corpWechat.corpSecret:secret}")
	private String corpSecret;

	@Autowired
	private List<CorpWechatRequestHandler> handlers;

	@Autowired
	private CacheManager cacheManager;

	private boolean micrometerPresent = ClassUtils.isPresent("io.micrometer.core.instrument.Metrics",
			getClass().getClassLoader());

	private String micrometerTimerName = "corpWechat.calls";

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
			throw new ErrorMessage("errcode:{0},errmsg:{1}",
					new Object[] { node.get("errcode").asText(), node.get("errmsg").asText() });
	}

	public CorpWechatMedia upload(File file, CorpWechatMediaType mediaType) throws IOException {
		if (!file.isFile())
			throw new ErrorMessage(file + " is not a file");
		if (file.length() > mediaType.getMaxFileLength())
			throw new ErrorMessage(file + " is large than " + mediaType.getMaxFileLength());
		long time = System.currentTimeMillis();
		boolean timeout = false;
		try {
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
				throw new ErrorMessage("errcode:{0},errmsg:{1}",
						new Object[] { node.get("errcode").asText(), node.get("errmsg").asText() });
			return new CorpWechatMedia(result);
		} catch (SocketTimeoutException e) {
			timeout = true;
			throw e;
		} finally {
			if (micrometerPresent) {
				io.micrometer.core.instrument.Metrics
						.timer(micrometerTimerName, "method", "post", "path", "/media/upload", "timeout",
								String.valueOf(timeout))
						.record(System.currentTimeMillis() - time, TimeUnit.MILLISECONDS);
			}
		}
	}

	public void download(String mediaId, OutputStream os) throws IOException {
		long time = System.currentTimeMillis();
		boolean timeout = false;
		try {
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
				String result = StringUtils.join(IOUtils.readLines(entity.getContent(), StandardCharsets.UTF_8), "\n");
				logger.info("received: " + result);
				JsonNode node = JsonUtils.fromJson(result, JsonNode.class);
				response.close();
				httpClient.close();
				if (node.has("errcode"))
					throw new ErrorMessage("errcode:{0},errmsg:{1}",
							new Object[] { node.get("errcode").asText(), node.get("errmsg").asText() });
			}
			IOUtils.copy(entity.getContent(), os);
			response.close();
			httpClient.close();
		} catch (SocketTimeoutException e) {
			timeout = true;
			throw e;
		} finally {
			if (micrometerPresent) {
				io.micrometer.core.instrument.Metrics
						.timer(micrometerTimerName, "method", "get", "path", "/media/get", "timeout",
								String.valueOf(timeout))
						.record(System.currentTimeMillis() - time, TimeUnit.MILLISECONDS);
			}
		}
	}

	public void download(String mediaId, HttpServletResponse resp) throws IOException {
		long time = System.currentTimeMillis();
		boolean timeout = false;
		try {
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
		} catch (SocketTimeoutException e) {
			timeout = true;
			throw e;
		} finally {
			if (micrometerPresent) {
				io.micrometer.core.instrument.Metrics
						.timer(micrometerTimerName, "method", "get", "path", "/media/get", "timeout",
								String.valueOf(timeout))
						.record(System.currentTimeMillis() - time, TimeUnit.MILLISECONDS);
			}
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
			timeout = true;
			if (retryTimes > 0)
				return invoke(path, request, --retryTimes);
			else
				throw e;
		} finally {
			if (micrometerPresent) {
				io.micrometer.core.instrument.Metrics
						.timer(micrometerTimerName, "method", (request != null) ? "post" : "get", "path",
								path.indexOf('?') > 0 ? path.substring(0, path.indexOf('?')) : path, "timeout",
								String.valueOf(timeout))
						.record(System.currentTimeMillis() - time, TimeUnit.MILLISECONDS);
			}
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
		String path = "/gettoken";
		long time = System.currentTimeMillis();
		boolean timeout = false;
		try {
			String result = HttpClientUtils.getResponseText(apiBaseUrl + path, params);
			logger.info("fetchAccessToken received: {}", result);
			JsonNode node = JsonUtils.fromJson(result, JsonNode.class);
			if (node.has("errcode"))
				throw new ErrorMessage("errcode:{0},errmsg:{1}",
						new Object[] { node.get("errcode").asText(), node.get("errmsg").asText() });
			accessToken = node.get("access_token").textValue();
			int expiresIn = node.get("expires_in").asInt();
			cacheManager.put(getCorpId(), accessToken, expiresIn > 60 ? expiresIn - 60 : expiresIn, TimeUnit.SECONDS,
					CACHE_NAMESPACE_ACCESSTOKEN);
			return accessToken;
		} catch (SocketTimeoutException e) {
			timeout = true;
			throw e;
		} finally {
			if (micrometerPresent) {
				io.micrometer.core.instrument.Metrics
						.timer(micrometerTimerName, "method", "get", "path", path, "timeout", String.valueOf(timeout))
						.record(System.currentTimeMillis() - time, TimeUnit.MILLISECONDS);
			}
		}
	}

	public String getJsApiTicket() throws IOException {
		String jsApiTicket = (String) cacheManager.get(getCorpId(), CACHE_NAMESPACE_JSAPITICKET);
		if (jsApiTicket != null)
			return jsApiTicket;
		Map<String, String> params = new HashMap<>();
		params.put("access_token", fetchAccessToken());
		params.put("type", "jsapi");
		String path = "/get_jsapi_ticket";
		long time = System.currentTimeMillis();
		boolean timeout = false;
		try {
			String result = HttpClientUtils.getResponseText(apiBaseUrl + path, params);
			logger.info("getJsApiToken received: {}", result);
			JsonNode node = JsonUtils.fromJson(result, JsonNode.class);
			if (node.has("errcode") && node.get("errcode").asInt() > 0)
				throw new ErrorMessage("errcode:{0},errmsg:{1}",
						new Object[] { node.get("errcode").asText(), node.get("errmsg").asText() });
			jsApiTicket = node.get("ticket").textValue();
			int expiresIn = node.get("expires_in").asInt();
			cacheManager.put(getCorpId(), jsApiTicket, expiresIn > 60 ? expiresIn - 60 : expiresIn, TimeUnit.SECONDS,
					CACHE_NAMESPACE_JSAPITICKET);
			return jsApiTicket;
		} catch (SocketTimeoutException e) {
			timeout = true;
			throw e;
		} finally {
			if (micrometerPresent) {
				io.micrometer.core.instrument.Metrics
						.timer(micrometerTimerName, "method", "get", "path", path, "timeout", String.valueOf(timeout))
						.record(System.currentTimeMillis() - time, TimeUnit.MILLISECONDS);
			}
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
		sb.append("https://open.weixin.qq.com/connect/oauth2/authorize?appid=").append(getCorpId());
		sb.append("&redirect_uri=").append(URLEncoder.encode(absolutizeUri(redirect_uri), "utf-8"));
		sb.append("&response_type=code&scope=snsapi_base");
		if (StringUtils.isNotBlank(state))
			sb.append("&state=").append(URLEncoder.encode(state, "utf-8"));
		sb.append("#wechat_redirect");
		return sb.toString();
	}

	public CorpWechatUserInfo getUserInfoByCode(String code) throws IOException {
		long time = System.currentTimeMillis();
		boolean timeout = false;
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("https://qyapi.weixin.qq.com/cgi-bin/user/getuserinfo");
			sb.append("?access_token=").append(fetchAccessToken());
			sb.append("&code=").append(code);
			String result = HttpClientUtils.getResponseText(sb.toString());
			logger.info("getUserInfoByCode received: {}", result);
			JsonNode node = JsonUtils.fromJson(result, JsonNode.class);
			if (node.has("errcode"))
				throw new ErrorMessage("errcode:{0},errmsg:{1}",
						new Object[] { node.get("errcode").asText(), node.get("errmsg").asText() });
			CorpWechatUserInfo info = new CorpWechatUserInfo();
			if (node.has("UserId"))
				info.setUserid(node.get("UserId").asText());
			if (node.has("OpenId"))
				info.setOpenid(node.get("OpenId").asText());
			if (node.has("DeviceId"))
				info.setDeviceid(node.get("DeviceId").asText());
			return info;
		} catch (SocketTimeoutException e) {
			timeout = true;
			throw e;
		} finally {
			if (micrometerPresent) {
				io.micrometer.core.instrument.Metrics
						.timer(micrometerTimerName, "method", "get", "path", "/user/getuserinfo", "timeout",
								String.valueOf(timeout))
						.record(System.currentTimeMillis() - time, TimeUnit.MILLISECONDS);
			}
		}
	}

}
