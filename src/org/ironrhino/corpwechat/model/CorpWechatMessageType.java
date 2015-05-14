package org.ironrhino.corpwechat.model;

import java.io.UnsupportedEncodingException;

import org.apache.commons.lang3.StringUtils;
import org.ironrhino.core.model.Displayable;
import org.ironrhino.core.struts.I18N;
import org.ironrhino.wechat.model.WechatResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public enum CorpWechatMessageType implements Displayable {

	text {
		@Override
		protected void build(ObjectNode object, CorpWechatMessage msg) {
			String content = msg.getContent();
			try {
				if (content.getBytes("UTF-8").length > WechatResponse.CONTENT_MAX_BYTES)
					content = content.substring(0,
							WechatResponse.CONTENT_MAX_BYTES / 3);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			object.with(name()).put("content", content);
		}
	},
	image {
		@Override
		protected void build(ObjectNode object, CorpWechatMessage msg) {
			object.with(name()).put("media_id", msg.getMedia_id());
		}
	},
	voice {
		@Override
		protected void build(ObjectNode object, CorpWechatMessage msg) {
			object.with(name()).put("media_id", msg.getMedia_id());
		}
	},
	video {
		@Override
		protected void build(ObjectNode object, CorpWechatMessage msg) {
			ObjectNode video = object.with(name()).put("media_id",
					msg.getMedia_id());
			if (StringUtils.isNotBlank(msg.getTitle()))
				video.put("title", msg.getTitle());
			if (StringUtils.isNotBlank(msg.getDescription()))
				video.put("description", msg.getDescription());
		}
	},
	file {
		@Override
		protected void build(ObjectNode object, CorpWechatMessage msg) {
			object.with(name()).put("media_id", msg.getMedia_id());
		}
	},
	news {
		@Override
		protected void build(ObjectNode object, CorpWechatMessage msg) {
			if (msg.getArticles().size() > 10)
				throw new IllegalArgumentException(
						"article size can not large than 10");
			ArrayNode articles = object.with(name()).putArray("articles");
			for (CorpWechatArticle article : msg.getArticles()) {
				ObjectNode item = mapper.createObjectNode()
						.put("title", article.getTitle())
						.put("description", article.getDescription())
						.put("url", article.getUrl())
						.put("picurl", article.getPicurl());
				articles.add(item);
			}
		}
	},
	mpnews {
		@Override
		protected void build(ObjectNode object, CorpWechatMessage msg) {
			if (msg.getArticles().size() > 10)
				throw new IllegalArgumentException(
						"article size can not large than 10");
			ArrayNode articles = object.with(name()).putArray("articles");
			for (CorpWechatArticle article : msg.getArticles()) {
				ObjectNode item = mapper
						.createObjectNode()
						.put("title", article.getTitle())
						.put("thumb_media_id", article.getThumb_media_id())
						.put("author", article.getAuthor())
						.put("content_source_url",
								article.getContent_source_url())
						.put("content_source_url",
								article.getContent_source_url())
						.put("content", article.getContent())
						.put("digest", article.getDigest())
						.put("show_cover_pic", article.getShow_cover_pic());
				articles.add(item);
			}
		}
	};

	private static ObjectMapper mapper = new ObjectMapper();

	@Override
	public String getName() {
		return this.name();
	}

	@Override
	public String getDisplayName() {
		try {
			return I18N.getText(getClass(), name());
		} catch (Exception e) {
			return name();
		}
	}

	@Override
	public String toString() {
		return getDisplayName();
	}

	abstract void build(ObjectNode object, CorpWechatMessage msg);

	public String toJson(CorpWechatMessage msg) {
		ObjectNode object = mapper.createObjectNode();
		object.put("touser", msg.getTouser()).put("toparty", msg.getToparty())
				.put("totag", msg.getTotag()).put("agentid", msg.getAgentid())
				.put("safe", msg.getSafe()).put("msgtype", name());
		build(object, msg);
		return object.toString();
	}

}
