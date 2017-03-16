package org.ironrhino.wechat.model;

import java.io.UnsupportedEncodingException;

import org.apache.commons.lang3.StringUtils;
import org.ironrhino.core.model.Displayable;

public enum WechatResponseType implements Displayable {

	text {
		@Override
		public String toXml(WechatResponse msg) {
			StringBuilder sb = new StringBuilder();
			sb.append("<xml><ToUserName><![CDATA[");
			sb.append(msg.getToUserName());
			sb.append("]]></ToUserName><FromUserName><![CDATA[");
			sb.append(msg.getFromUserName());
			sb.append("]]></FromUserName><CreateTime>");
			sb.append(msg.getCreateTime());
			sb.append("</CreateTime><MsgType>").append(name()).append("</MsgType><Content><![CDATA[");
			String content = msg.getContent();
			try {
				if (content.getBytes("UTF-8").length > WechatResponse.CONTENT_MAX_BYTES)
					content = content.substring(0, WechatResponse.CONTENT_MAX_BYTES / 3);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			sb.append(msg.getContent());
			sb.append("]]></Content></xml>");
			return sb.toString();
		}
	},
	image {
		@Override
		public String toXml(WechatResponse msg) {
			StringBuilder sb = new StringBuilder();
			sb.append("<xml><ToUserName><![CDATA[");
			sb.append(msg.getToUserName());
			sb.append("]]></ToUserName><FromUserName><![CDATA[");
			sb.append(msg.getFromUserName());
			sb.append("]]></FromUserName><CreateTime>");
			sb.append(msg.getCreateTime());
			sb.append("</CreateTime><MsgType>").append(name()).append("</MsgType><Image><MediaId>");
			sb.append(msg.getMediaId());
			sb.append("</MediaId></Image></xml>");
			return sb.toString();
		}
	},
	voice {
		@Override
		public String toXml(WechatResponse msg) {
			StringBuilder sb = new StringBuilder();
			sb.append("<xml><ToUserName><![CDATA[");
			sb.append(msg.getToUserName());
			sb.append("]]></ToUserName><FromUserName><![CDATA[");
			sb.append(msg.getFromUserName());
			sb.append("]]></FromUserName><CreateTime>");
			sb.append(msg.getCreateTime());
			sb.append("</CreateTime><MsgType>").append(name()).append("</MsgType><Voice><MediaId>");
			sb.append(msg.getMediaId());
			sb.append("</MediaId></Voice></xml>");
			return sb.toString();
		}
	},
	video {
		@Override
		public String toXml(WechatResponse msg) {
			StringBuilder sb = new StringBuilder();
			sb.append("<xml><ToUserName><![CDATA[");
			sb.append(msg.getToUserName());
			sb.append("]]></ToUserName><FromUserName><![CDATA[");
			sb.append(msg.getFromUserName());
			sb.append("]]></FromUserName><CreateTime>");
			sb.append(msg.getCreateTime());
			sb.append("</CreateTime><MsgType>").append(name()).append("</MsgType><Video><MediaId>");
			sb.append(msg.getMediaId());
			sb.append("</MediaId>").append("<Title><![CDATA[");
			sb.append(msg.getTitle());
			sb.append("]]></Title>").append("<Description><![CDATA[");
			sb.append(msg.getDescription());
			sb.append("]]></Description>").append("</Video></xml>");
			return sb.toString();
		}
	},
	music {
		@Override
		public String toXml(WechatResponse msg) {
			StringBuilder sb = new StringBuilder();
			sb.append("<xml><ToUserName><![CDATA[");
			sb.append(msg.getToUserName());
			sb.append("]]></ToUserName><FromUserName><![CDATA[");
			sb.append(msg.getFromUserName());
			sb.append("]]></FromUserName><CreateTime>");
			sb.append(msg.getCreateTime());
			sb.append("</CreateTime><MsgType>").append(name()).append("</MsgType><Music>").append("<Title><![CDATA[");
			sb.append(msg.getTitle());
			sb.append("]]></Title>").append("<Description><![CDATA[");
			sb.append(msg.getDescription());
			sb.append("]]></Description>").append("<MusicUrl><![CDATA[");
			sb.append(msg.getMusicUrl());
			sb.append("]]></MusicUrl>").append("<HQMusicUrl><![CDATA[");
			sb.append(msg.getHQMusicUrl());
			sb.append("]]></HQMusicUrl>").append("<ThumbMediaId><![CDATA[");
			sb.append(msg.getThumbMediaId());
			sb.append("]]></ThumbMediaId>").append("</Music></xml>");
			return sb.toString();
		}
	},
	news {
		@Override
		public String toXml(WechatResponse msg) {
			if (msg.getArticles().size() > 10)
				throw new IllegalArgumentException("article size can not large than 10");
			StringBuilder sb = new StringBuilder();
			sb.append("<xml><ToUserName><![CDATA[");
			sb.append(msg.getToUserName());
			sb.append("]]></ToUserName><FromUserName><![CDATA[");
			sb.append(msg.getFromUserName());
			sb.append("]]></FromUserName><CreateTime>");
			sb.append(msg.getCreateTime());
			sb.append("</CreateTime><MsgType>").append(name()).append("</MsgType>").append("<ArticleCount>")
					.append(msg.getArticles().size()).append("</ArticleCount>").append("<Articles>");
			for (WechatArticle article : msg.getArticles()) {
				sb.append("<item><Title><![CDATA[");
				sb.append(article.getTitle());
				sb.append("]]></Title>").append("<Description><![CDATA[");
				sb.append(article.getDescription());
				sb.append("]]></Description>").append("<PicUrl><![CDATA[");
				sb.append(article.getPicurl());
				sb.append("]]></PicUrl>").append("<Url><![CDATA[");
				sb.append(article.getUrl());
				sb.append("]]></Url></item>");
			}
			sb.append("</Articles></xml>");
			return sb.toString();
		}
	},
	transfer_customer_service {
		@Override
		public String toXml(WechatResponse msg) {
			StringBuilder sb = new StringBuilder();
			sb.append("<xml><ToUserName><![CDATA[");
			sb.append(msg.getToUserName());
			sb.append("]]></ToUserName><FromUserName><![CDATA[");
			sb.append(msg.getFromUserName());
			sb.append("]]></FromUserName><CreateTime>");
			sb.append(msg.getCreateTime());
			sb.append("</CreateTime><MsgType>").append(name()).append("</MsgType>");
			if (StringUtils.isNotBlank(msg.getKfAccount()))
				sb.append("<TransInfo><KfAccount><![CDATA[").append(msg.getToUserName())
						.append("]]></KfAccount></TransInfo>");
			sb.append("</xml>");
			return sb.toString();
		}
	};

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

	public String toXml(WechatResponse msg) {
		return "";
	}

}
