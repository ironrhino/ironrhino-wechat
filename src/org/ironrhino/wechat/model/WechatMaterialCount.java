package org.ironrhino.wechat.model;

import java.io.Serializable;

import org.ironrhino.core.util.JsonUtils;
import org.springframework.beans.BeanUtils;

import lombok.NoArgsConstructor;

import lombok.Data;

@Data
@NoArgsConstructor
public class WechatMaterialCount implements Serializable {

	private static final long serialVersionUID = 8896631350899380567L;

	private int voice_count;
	private int video_count;
	private int image_count;
	private int news_count;

	public WechatMaterialCount(String json) {
		try {
			WechatMaterialCount wm = JsonUtils.fromJson(json, WechatMaterialCount.class);
			BeanUtils.copyProperties(wm, this);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

}
