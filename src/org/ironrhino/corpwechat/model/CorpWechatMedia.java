package org.ironrhino.corpwechat.model;

import java.io.Serializable;
import java.util.Date;

import org.ironrhino.core.util.JsonUtils;
import org.springframework.beans.BeanUtils;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CorpWechatMedia implements Serializable {

	private static final long serialVersionUID = 8896631350899380567L;

	public static final long lifetime = 3 * 24 * 3600;

	private String media_id;

	private CorpWechatMediaType type;

	private long created_at;

	public CorpWechatMedia(String json) {
		try {
			CorpWechatMedia wm = JsonUtils.fromJson(json, CorpWechatMedia.class);
			BeanUtils.copyProperties(wm, this);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public Date getExpireDate() {
		if (created_at == 0)
			return null;
		return new Date((created_at + lifetime) * 1000);
	}

}
