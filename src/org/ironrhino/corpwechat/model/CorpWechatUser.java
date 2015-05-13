package org.ironrhino.corpwechat.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.ironrhino.core.model.Attribute;

public class CorpWechatUser implements Serializable {

	private static final long serialVersionUID = 4982625019550377789L;

	private String userid;

	private String name;

	private List<Integer> department;

	private String position;

	private String mobile;

	private String email;

	private String weixinid;

	private Integer enable;

	private Integer status;

	private String avatar;

	private Map<String, List<Attribute>> extattr;

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Integer> getDepartment() {
		return department;
	}

	public void setDepartment(List<Integer> department) {
		this.department = department;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getWeixinid() {
		return weixinid;
	}

	public void setWeixinid(String weixinid) {
		this.weixinid = weixinid;
	}

	public Integer getEnable() {
		return enable;
	}

	public void setEnable(Integer enable) {
		this.enable = enable;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public Map<String, List<Attribute>> getExtattr() {
		return extattr;
	}

	public void setExtattr(Map<String, List<Attribute>> extattr) {
		this.extattr = extattr;
	}

}
