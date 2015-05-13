package org.ironrhino.wechat.model;

import java.io.Serializable;
import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;
import org.ironrhino.core.util.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapperImpl;

import com.fasterxml.jackson.databind.JsonNode;

public class WechatUser implements Serializable {

	private static Logger logger = LoggerFactory.getLogger(WechatUser.class);

	private static final long serialVersionUID = -2575653400669416221L;

	private String openid;
	private String unionid;
	private Integer groupid;
	private String nickname;
	private String remark;
	private int sex;
	private String city;
	private String country;
	private String province;
	private String language;
	private String headimgurl;
	private int subscribe;
	private long subscribe_time;

	public WechatUser() {

	}

	public WechatUser(String json) {
		try {
			BeanWrapperImpl bwi = new BeanWrapperImpl(this);
			JsonNode node = JsonUtils.fromJson(json, JsonNode.class);
			Iterator<String> it = node.fieldNames();
			while (it.hasNext()) {
				String name = it.next();
				String value = node.get(name).asText();
				if (name.equals("headimgurl") && StringUtils.isNotBlank(value)
						&& !value.endsWith("/"))
					value = value.substring(0, value.lastIndexOf('/') + 1);
				try {
					bwi.setPropertyValue(name, value);
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getUnionid() {
		return unionid;
	}

	public void setUnionid(String unionid) {
		this.unionid = unionid;
	}

	public Integer getGroupid() {
		return groupid;
	}

	public void setGroupid(Integer groupid) {
		this.groupid = groupid;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getHeadimgurl() {
		return headimgurl;
	}

	public void setHeadimgurl(String headimgurl) {
		this.headimgurl = headimgurl;
	}

	public int getSubscribe() {
		return subscribe;
	}

	public void setSubscribe(int subscribe) {
		this.subscribe = subscribe;
	}

	public long getSubscribe_time() {
		return subscribe_time;
	}

	public void setSubscribe_time(long subscribe_time) {
		this.subscribe_time = subscribe_time;
	}

}
