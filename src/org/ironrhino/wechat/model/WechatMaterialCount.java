package org.ironrhino.wechat.model;

import java.io.Serializable;

import org.ironrhino.core.util.JsonUtils;
import org.springframework.beans.BeanUtils;

public class WechatMaterialCount implements Serializable {

	private static final long serialVersionUID = 8896631350899380567L;

	private int voice_count;
	private int video_count;
	private int image_count;
	private int news_count;

	public WechatMaterialCount() {

	}

	public WechatMaterialCount(String json) {
		try {
			WechatMaterialCount wm = JsonUtils.fromJson(json, WechatMaterialCount.class);
			BeanUtils.copyProperties(wm, this);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public int getVoice_count() {
		return voice_count;
	}

	public void setVoice_count(int voice_count) {
		this.voice_count = voice_count;
	}

	public int getVideo_count() {
		return video_count;
	}

	public void setVideo_count(int video_count) {
		this.video_count = video_count;
	}

	public int getImage_count() {
		return image_count;
	}

	public void setImage_count(int image_count) {
		this.image_count = image_count;
	}

	public int getNews_count() {
		return news_count;
	}

	public void setNews_count(int news_count) {
		this.news_count = news_count;
	}

}
