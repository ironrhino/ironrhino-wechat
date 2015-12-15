package org.ironrhino.wechat.support;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.ironrhino.core.util.ErrorMessage;
import org.ironrhino.core.util.HttpClientUtils;
import org.ironrhino.core.util.JsonUtils;
import org.ironrhino.wechat.model.WechatMaterialCount;
import org.ironrhino.wechat.model.WechatMaterialList;
import org.ironrhino.wechat.model.WechatMedia;
import org.ironrhino.wechat.model.WechatMediaType;
import org.ironrhino.wechat.model.WechatNewsList;
import org.ironrhino.wechat.model.WechatNewsMessage.Article;
import org.ironrhino.wechat.model.WechatVideo;
import org.ironrhino.wechat.service.Wechat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;

@Component
@Retryable(include = IOException.class, backoff = @Backoff(delay = 1000, maxDelay = 5000, multiplier = 2) )
public class WechatMaterialControl {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private Wechat wechat;

	public String uploadImage(File file) throws IOException {
		WechatMediaType mediaType = WechatMediaType.image;
		if (!file.isFile())
			throw new ErrorMessage(file + " is not a file");
		if (file.length() > mediaType.getMaxFileLength())
			throw new ErrorMessage(file + " is large than " + mediaType.getMaxFileLength());
		StringBuilder sb = new StringBuilder(wechat.getApiBaseUrl());
		sb.append("/media/uploadimg?access_token=");
		sb.append(wechat.fetchAccessToken());
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
		return node.get("url").textValue();
	}

	public String uploadNews(List<Article> articles) throws IOException {
		Map<String, List<Article>> map = new HashMap<>();
		map.put("articles", articles);
		String request = JsonUtils.toJson(map);
		String result = wechat.invoke("/material/add_news", request);
		JsonNode node = JsonUtils.fromJson(result, JsonNode.class);
		if (node.has("errcode"))
			throw new ErrorMessage("errcode:{0},errmsg:{1}",
					new Object[] { node.get("errcode").asText(), node.get("errmsg").asText() });
		return node.get("media_id").textValue();
	}

	public void updateNews(String media_id, int index, Article article) throws IOException {
		Map<String, Object> map = new HashMap<>();
		map.put("media_id", media_id);
		map.put("index", index);
		map.put("articles", article);
		String request = JsonUtils.toJson(map);
		String result = wechat.invoke("/material/update_news", request);
		JsonNode node = JsonUtils.fromJson(result, JsonNode.class);
		if (node.has("errcode") && node.get("errcode").asInt() > 0)
			throw new ErrorMessage("errcode:{0},errmsg:{1}",
					new Object[] { node.get("errcode").asText(), node.get("errmsg").asText() });
	}

	public List<Article> getNews(String media_id) throws IOException {
		String result = getMaterial(media_id);
		JsonNode node = JsonUtils.fromJson(result, JsonNode.class);
		node = node.get("news_item");
		if (node == null)
			return null;
		return JsonUtils.fromJson(node.toString(), new TypeReference<List<Article>>() {
		});
	}

	public WechatMedia uploadMaterial(File file, WechatMediaType mediaType) throws IOException {
		return uploadMaterial(file, mediaType, null, null);
	}

	public String uploadVideo(File file, String title, String introduction) throws IOException {
		return uploadMaterial(file, WechatMediaType.video, title, introduction).getMedia_id();
	}

	public WechatVideo getVideo(String media_id) throws IOException {
		String result = getMaterial(media_id);
		return new WechatVideo(result);
	}

	protected void deleteMaterial(String media_id) throws IOException {
		Map<String, String> map = new HashMap<>();
		map.put("media_id", media_id);
		String result = wechat.invoke("/material/del_material", JsonUtils.toJson(map));
		JsonNode node = JsonUtils.fromJson(result, JsonNode.class);
		if (node.has("errcode") && node.get("errcode").asInt() > 0)
			throw new ErrorMessage("errcode:{0},errmsg:{1}",
					new Object[] { node.get("errcode").asText(), node.get("errmsg").asText() });
	}

	protected WechatMedia uploadMaterial(File file, WechatMediaType mediaType, String title, String introduction)
			throws IOException {
		if (!file.isFile())
			throw new ErrorMessage(file + " is not a file");
		if (file.length() > mediaType.getMaxFileLength())
			throw new ErrorMessage(file + " is large than " + mediaType.getMaxFileLength());
		StringBuilder sb = new StringBuilder(wechat.getApiBaseUrl());
		sb.append("/meterial/add_meterial?access_token=");
		sb.append(wechat.fetchAccessToken());
		HttpPost httppost = new HttpPost(sb.toString());
		FileBody media = new FileBody(file);
		MultipartEntityBuilder builder = MultipartEntityBuilder.create().setMode(HttpMultipartMode.RFC6532)
				.addPart("media", media);
		if (mediaType == WechatMediaType.video && title != null) {
			Map<String, String> map = new HashMap<>();
			map.put("title", title);
			map.put("introduction", introduction);
			builder.addTextBody("description", JsonUtils.toJson(map));
		}
		HttpEntity reqEntity = builder.build();
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
		return new WechatMedia(result);
	}

	protected String getMaterial(String media_id) throws IOException {
		Map<String, String> map = new HashMap<>();
		map.put("media_id", media_id);
		String result = wechat.invoke("/material/get_material", JsonUtils.toJson(map));
		JsonNode node = JsonUtils.fromJson(result, JsonNode.class);
		if (node.has("errcode"))
			throw new ErrorMessage("errcode:{0},errmsg:{1}",
					new Object[] { node.get("errcode").asText(), node.get("errmsg").asText() });
		return result;
	}

	public WechatMaterialCount countMaterial() throws IOException {
		String result = wechat.invoke("/material/get_materialcount", null);
		JsonNode node = JsonUtils.fromJson(result, JsonNode.class);
		if (node.has("errcode"))
			throw new ErrorMessage("errcode:{0},errmsg:{1}",
					new Object[] { node.get("errcode").asText(), node.get("errmsg").asText() });
		return new WechatMaterialCount(result);
	}

	protected WechatNewsList listNews(int offset, int limit) throws IOException {
		return JsonUtils.fromJson(batchgetMaterial(WechatMediaType.news, offset, limit), WechatNewsList.class);
	}

	protected WechatMaterialList listMaterial(WechatMediaType mediaType, int offset, int limit) throws IOException {
		if (mediaType == WechatMediaType.news)
			throw new IllegalArgumentException("Unsupported type 'news', use listNews instead");
		return JsonUtils.fromJson(batchgetMaterial(mediaType, offset, limit), WechatMaterialList.class);
	}

	protected String batchgetMaterial(WechatMediaType mediaType, int offset, int limit) throws IOException {
		Map<String, Object> map = new HashMap<>();
		map.put("type", mediaType.name());
		map.put("offset", offset);
		map.put("count", limit);
		String result = wechat.invoke("/material/batchget_material", JsonUtils.toJson(map));
		JsonNode node = JsonUtils.fromJson(result, JsonNode.class);
		if (node.has("errcode"))
			throw new ErrorMessage("errcode:{0},errmsg:{1}",
					new Object[] { node.get("errcode").asText(), node.get("errmsg").asText() });
		return result;
	}

}
