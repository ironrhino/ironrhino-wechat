package org.ironrhino.wechat.model;

import java.io.UnsupportedEncodingException;

import org.apache.commons.lang3.StringUtils;
import org.ironrhino.core.model.Displayable;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public enum WechatMessageType implements Displayable {

	text {
		@Override
		protected void buildObjectNode(ObjectNode object, WechatMessage msg) {
			String content = msg.getContent();
			try {
				if (content.getBytes("UTF-8").length > WechatResponse.CONTENT_MAX_BYTES)
					content = content.substring(0, WechatResponse.CONTENT_MAX_BYTES / 3);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			object.with(name()).put("content", content);
		}
	},
	image {
		@Override
		protected void buildObjectNode(ObjectNode object, WechatMessage msg) {
			object.with(name()).put("media_id", msg.getMedia_id());
		}
	},
	voice {
		@Override
		protected void buildObjectNode(ObjectNode object, WechatMessage msg) {
			object.with(name()).put("media_id", msg.getMedia_id());
		}
	},
	video {
		@Override
		protected void buildObjectNode(ObjectNode object, WechatMessage msg) {
			ObjectNode video = object.with(name()).put("media_id", msg.getMedia_id());
			if (StringUtils.isNotBlank(msg.getTitle()))
				video.put("title", msg.getTitle());
			if (StringUtils.isNotBlank(msg.getDescription()))
				video.put("description", msg.getDescription());
		}
	},
	music {
		@Override
		protected void buildObjectNode(ObjectNode object, WechatMessage msg) {
			object.with(name()).put("title", msg.getTitle()).put("description", msg.getDescription())
					.put("musicurl", msg.getMusicurl()).put("hqmusicurl", msg.getHqmusicurl())
					.put("thumb_media_id", msg.getThumb_media_id());
		}
	},
	news {
		@Override
		protected void buildObjectNode(ObjectNode object, WechatMessage msg) {
			if (msg.getArticles().size() > 10)
				throw new IllegalArgumentException("article size can not large than 10");
			ArrayNode articles = object.with(name()).putArray("articles");
			for (WechatArticle article : msg.getArticles()) {
				ObjectNode item = mapper.createObjectNode().put("title", article.getTitle())
						.put("description", article.getDescription()).put("url", article.getUrl())
						.put("picurl", article.getPicurl());
				articles.add(item);
			}
		}
	},
	mpnews {
		@Override
		protected void buildObjectNode(ObjectNode object, WechatMessage msg) {
			object.with(name()).put("media_id", msg.getMedia_id());
		}
	},
	wxcard {
		@Override
		protected void buildObjectNode(ObjectNode object, WechatMessage msg) {
			object.with(name()).put("card_id", msg.getCard_id());
		}
	};

	private static ObjectMapper mapper = new ObjectMapper();

	public String getName() {
		return name();
	}

	public String getDisplayName() {
		return Displayable.super.getDisplayName();
	}

	@Override
	public String toString() {
		return getDisplayName();
	}

	protected abstract void buildObjectNode(ObjectNode object, WechatMessage msg);

	public String toJson(WechatMessage msg) {
		ObjectNode object = mapper.createObjectNode();
		object.put("touser", msg.getTouser()).put("msgtype", name());
		buildObjectNode(object, msg);
		return object.toString();
	}

}
