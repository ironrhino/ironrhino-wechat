package org.ironrhino.wechat.model;

import java.io.Serializable;
import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;
import org.ironrhino.core.util.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.NotWritablePropertyException;

import com.fasterxml.jackson.databind.JsonNode;

public class WechatVideo implements Serializable {

	private static Logger logger = LoggerFactory.getLogger(WechatVideo.class);

	private static final long serialVersionUID = -2575653400669416221L;

	private String title;
	private String description;
	private String down_url;

	public WechatVideo() {

	}

	public WechatVideo(String json) {
		try {
			BeanWrapperImpl bwi = new BeanWrapperImpl(this);
			JsonNode node = JsonUtils.fromJson(json, JsonNode.class);
			Iterator<String> it = node.fieldNames();
			while (it.hasNext()) {
				String name = it.next();
				String value = node.get(name).asText();
				if (name.equals("headimgurl") && StringUtils.isNotBlank(value) && !value.endsWith("/"))
					value = value.substring(0, value.lastIndexOf('/') + 1);
				try {
					bwi.setPropertyValue(name, value);
				} catch (NotWritablePropertyException e) {
					// ignore
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDown_url() {
		return down_url;
	}

	public void setDown_url(String down_url) {
		this.down_url = down_url;
	}

}
