package org.ironrhino.wechat.util;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class HttpClientHelper {

	public static String getResponseText(CloseableHttpClient httpClient, String url) throws IOException {
		return getResponseText(httpClient, url, null, "UTF-8");
	}

	public static String getResponseText(CloseableHttpClient httpClient, String url, Map<String, String> params)
			throws IOException {
		return getResponseText(httpClient, url, params, "UTF-8");
	}

	public static String getResponseText(CloseableHttpClient httpClient, String url, Map<String, String> params,
			Map<String, String> headers) throws IOException {
		return getResponseText(httpClient, url, params, headers, "UTF-8");
	}

	public static String getResponseText(CloseableHttpClient httpClient, String url, Map<String, String> params,
			String charset) throws IOException {
		return getResponseText(httpClient, url, params, null, charset);
	}

	public static String getResponseText(CloseableHttpClient httpClient, String url, Map<String, String> params,
			Map<String, String> headers, String charset) throws IOException {
		HttpGet httpRequest = null;
		StringBuilder sb = new StringBuilder(url);
		if (params != null && params.size() > 0) {
			sb.append(url.indexOf('?') < 0 ? '?' : '&');
			for (Map.Entry<String, String> entry : params.entrySet()) {
				sb.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), charset)).append("&");
			}
			sb.deleteCharAt(sb.length() - 1);
		}
		httpRequest = new HttpGet(sb.toString());
		if (headers != null && headers.size() > 0)
			for (Map.Entry<String, String> entry : headers.entrySet())
				httpRequest.addHeader(entry.getKey(), entry.getValue());
		return httpClient.execute(httpRequest, new BasicResponseHandler(charset));
	}

	public static String postResponseText(CloseableHttpClient httpClient, String url, Map<String, String> params)
			throws IOException {
		return postResponseText(httpClient, url, params, "UTF-8");
	}

	public static String postResponseText(CloseableHttpClient httpClient, String url, Map<String, String> params,
			Map<String, String> headers) throws IOException {
		return postResponseText(httpClient, url, params, headers, "UTF-8");
	}

	public static String postResponseText(CloseableHttpClient httpClient, String url, Map<String, String> params,
			String charset) throws IOException {
		return postResponseText(httpClient, url, params, null, charset);
	}

	public static String postResponseText(CloseableHttpClient httpClient, String url, Map<String, String> params,
			Map<String, String> headers, String charset) throws IOException {
		HttpPost httpRequest = new HttpPost(url);
		if (params != null && params.size() > 0) {
			List<NameValuePair> nvps = new ArrayList<>();
			for (Map.Entry<String, String> entry : params.entrySet())
				nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			httpRequest.setEntity(new UrlEncodedFormEntity(nvps, charset));
		}
		if (headers != null && headers.size() > 0)
			for (Map.Entry<String, String> entry : headers.entrySet())
				httpRequest.addHeader(entry.getKey(), entry.getValue());
		return httpClient.execute(httpRequest, new BasicResponseHandler(charset));
	}

	public static String postResponseText(CloseableHttpClient httpClient, String url, String body,
			Map<String, String> headers, String charset) throws IOException {
		HttpPost httpRequest = new HttpPost(url);
		httpRequest.setEntity(new StringEntity(body, charset));
		if (headers != null && headers.size() > 0)
			for (Map.Entry<String, String> entry : headers.entrySet())
				httpRequest.addHeader(entry.getKey(), entry.getValue());
		return httpClient.execute(httpRequest, new BasicResponseHandler(charset));
	}

	public static String post(CloseableHttpClient httpClient, String url, String entity) throws IOException {
		return invoke(httpClient, "POST", url, entity);
	}

	public static String post(CloseableHttpClient httpClient, String url, String entity, String charset)
			throws IOException {
		return invoke(httpClient, "POST", url, entity, charset);
	}

	public static String put(CloseableHttpClient httpClient, String url, String entity) throws IOException {
		return invoke(httpClient, "PUT", url, entity);
	}

	public static String put(CloseableHttpClient httpClient, String url, String entity, String charset)
			throws IOException {
		return invoke(httpClient, "PUT", url, entity, charset);
	}

	public static String delete(CloseableHttpClient httpClient, String url) throws IOException {
		return invoke(httpClient, "DELETE", url, null);
	}

	public static String get(CloseableHttpClient httpClient, String url) throws IOException {
		return invoke(httpClient, "GET", url, null);
	}

	private static String invoke(CloseableHttpClient httpClient, String method, String url, String entity)
			throws IOException {
		return invoke(httpClient, method, url, entity, "UTF-8");
	}

	private static String invoke(CloseableHttpClient httpClient, String method, String url, String entity,
			String charset) throws IOException {
		HttpRequestBase httpRequest = null;
		if (method.equalsIgnoreCase("GET"))
			httpRequest = new HttpGet(url);
		else if (method.equalsIgnoreCase("POST"))
			httpRequest = new HttpPost(url);
		else if (method.equalsIgnoreCase("PUT"))
			httpRequest = new HttpPut(url);
		else if (method.equalsIgnoreCase("DELETE"))
			httpRequest = new HttpDelete(url);
		if (entity != null)
			((HttpEntityEnclosingRequestBase) httpRequest).setEntity(new StringEntity(entity, charset));
		return httpClient.execute(httpRequest, new BasicResponseHandler(charset));
	}

	static class BasicResponseHandler implements ResponseHandler<String> {

		private String charset;

		public String getCharset() {
			return charset;
		}

		public void setCharset(String charset) {
			this.charset = charset;
		}

		BasicResponseHandler(String charset) {
			this.charset = charset;
		}

		@Override
		public String handleResponse(HttpResponse response) throws HttpResponseException, IOException {
			try {
				StatusLine statusLine = response.getStatusLine();
				HttpEntity entity = response.getEntity();
				if (statusLine.getStatusCode() >= 300) {
					EntityUtils.consume(entity);
					throw new HttpResponseException(statusLine.getStatusCode(), statusLine.getReasonPhrase());
				}
				return entity == null ? null : EntityUtils.toString(entity, charset);
			} finally {
				((CloseableHttpResponse) response).close();
			}
		}

	}

}
