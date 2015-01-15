package org.ironrhino.wechat.model;

import java.io.Serializable;
import java.util.Date;

import org.ironrhino.core.util.JsonUtils;
import org.springframework.beans.BeanUtils;

public class WechatMedia implements Serializable {

	private static final long serialVersionUID = 8896631350899380567L;

	public static final long lifetime = 3 * 24 * 3600;

	private String media_id;

	private WechatMediaType type;

	private long created_at;

	public WechatMedia() {

	}

	public WechatMedia(String json) {
		try {
			WechatMedia wm = JsonUtils.fromJson(json, WechatMedia.class);
			BeanUtils.copyProperties(wm, this);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public String getMedia_id() {
		return media_id;
	}

	public void setMedia_id(String media_id) {
		this.media_id = media_id;
	}

	public WechatMediaType getType() {
		return type;
	}

	public void setType(WechatMediaType type) {
		this.type = type;
	}

	public long getCreated_at() {
		return created_at;
	}

	public void setCreated_at(long created_at) {
		this.created_at = created_at;
	}

	public Date getExpireDate() {
		if (created_at == 0)
			return null;
		return new Date((created_at + lifetime) * 1000);
	}

}
