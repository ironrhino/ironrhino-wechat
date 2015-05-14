package org.ironrhino.corpwechat.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class CorpWechatAgent implements Serializable {

	private static final long serialVersionUID = 6643643710043826001L;

	private Integer agentid;
	private String name;
	private String square_logo_url;
	private String round_logo_url;
	private String logo_mediaid;
	private String description;
	private String redirect_domain;
	private Integer close;
	private Integer report_location_flag;
	private Integer isreportuser;
	private Integer isreportenter;
	private Map<String, List<CorpWechatUser>> allow_userinfos;
	private Map<String, List<Integer>> allow_partys;
	private Map<String, List<Integer>> allow_tags;

	public Integer getAgentid() {
		return agentid;
	}

	public void setAgentid(Integer agentid) {
		this.agentid = agentid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSquare_logo_url() {
		return square_logo_url;
	}

	public void setSquare_logo_url(String square_logo_url) {
		this.square_logo_url = square_logo_url;
	}

	public String getRound_logo_url() {
		return round_logo_url;
	}

	public void setRound_logo_url(String round_logo_url) {
		this.round_logo_url = round_logo_url;
	}

	public String getLogo_mediaid() {
		return logo_mediaid;
	}

	public void setLogo_mediaid(String logo_mediaid) {
		this.logo_mediaid = logo_mediaid;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getRedirect_domain() {
		return redirect_domain;
	}

	public void setRedirect_domain(String redirect_domain) {
		this.redirect_domain = redirect_domain;
	}

	public Integer getClose() {
		return close;
	}

	public void setClose(Integer close) {
		this.close = close;
	}

	public Integer getReport_location_flag() {
		return report_location_flag;
	}

	public void setReport_location_flag(Integer report_location_flag) {
		this.report_location_flag = report_location_flag;
	}

	public Integer getIsreportuser() {
		return isreportuser;
	}

	public void setIsreportuser(Integer isreportuser) {
		this.isreportuser = isreportuser;
	}

	public Integer getIsreportenter() {
		return isreportenter;
	}

	public void setIsreportenter(Integer isreportenter) {
		this.isreportenter = isreportenter;
	}

	public Map<String, List<CorpWechatUser>> getAllow_userinfos() {
		return allow_userinfos;
	}

	public void setAllow_userinfos(
			Map<String, List<CorpWechatUser>> allow_userinfos) {
		this.allow_userinfos = allow_userinfos;
	}

	public Map<String, List<Integer>> getAllow_partys() {
		return allow_partys;
	}

	public void setAllow_partys(Map<String, List<Integer>> allow_partys) {
		this.allow_partys = allow_partys;
	}

	public Map<String, List<Integer>> getAllow_tags() {
		return allow_tags;
	}

	public void setAllow_tags(Map<String, List<Integer>> allow_tags) {
		this.allow_tags = allow_tags;
	}

}
