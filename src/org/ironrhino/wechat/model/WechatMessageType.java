package org.ironrhino.wechat.model;

import java.io.UnsupportedEncodingException;

import org.apache.commons.lang3.StringUtils;
import org.ironrhino.core.model.Displayable;
import org.ironrhino.core.struts.I18N;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public enum WechatMessageType implements Displayable {

	text {
		@Override
		protected ObjectNode buildObjectNode(WechatMessage msg) {
			String content = msg.getContent();
			try {
				if (content.getBytes("UTF-8").length > WechatResponse.CONTENT_MAX_BYTES)
					content = content.substring(0,
							WechatResponse.CONTENT_MAX_BYTES / 3);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			ObjectNode object = mapper.createObjectNode();
			object.put("touser", msg.getTouser()).put("msgtype", name())
					.with("text").put("content", content);
			return object;
		}
	},
	image {
		@Override
		protected ObjectNode buildObjectNode(WechatMessage msg) {
			ObjectNode object = mapper.createObjectNode();
			object.put("touser", msg.getTouser()).put("msgtype", name())
					.with("image").put("media_id", msg.getMedia_id());
			return object;
		}
	},
	voice {
		@Override
		protected ObjectNode buildObjectNode(WechatMessage msg) {
			ObjectNode object = mapper.createObjectNode();
			object.put("touser", msg.getTouser()).put("msgtype", name())
					.with("voice").put("media_id", msg.getMedia_id());
			return object;
		}
	},
	video {
		@Override
		protected ObjectNode buildObjectNode(WechatMessage msg) {
			ObjectNode object = mapper.createObjectNode();
			ObjectNode video = object.put("touser", msg.getTouser())
					.put("msgtype", name()).with("video")
					.put("media_id", msg.getMedia_id());
			if (StringUtils.isNotBlank(msg.getTitle()))
				video.put("title", msg.getTitle());
			if (StringUtils.isNotBlank(msg.getDescription()))
				video.put("description", msg.getDescription());
			return object;
		}
	},
	music {
		@Override
		protected ObjectNode buildObjectNode(WechatMessage msg) {
			ObjectNode object = mapper.createObjectNode();
			object.put("touser", msg.getTouser()).put("msgtype", name())
					.with("video").put("title", msg.getTitle())
					.put("description", msg.getDescription())
					.put("musicurl", msg.getMusicurl())
					.put("hqmusicurl", msg.getHqmusicurl())
					.put("thumb_media_id", msg.getThumb_media_id());
			return object;
		}
	},
	news {
		@Override
		protected ObjectNode buildObjectNode(WechatMessage msg) {
			if (msg.getArticles().size() > 10)
				throw new IllegalArgumentException(
						"article size can not large than 10");
			ObjectNode object = mapper.createObjectNode();
			ArrayNode articles = object.put("touser", msg.getTouser())
					.put("msgtype", name()).with("news").putArray("articles");
			for (WechatArticle article : msg.getArticles()) {
				ObjectNode item = mapper.createObjectNode()
						.put("title", article.getTitle())
						.put("description", article.getDescription())
						.put("url", article.getUrl())
						.put("picurl", article.getPicurl());
				articles.add(item);
			}
			return object;
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

	protected abstract ObjectNode buildObjectNode(WechatMessage msg);

	public String toJson(WechatMessage msg) {
		return buildObjectNode(msg).toString();
	}

}
