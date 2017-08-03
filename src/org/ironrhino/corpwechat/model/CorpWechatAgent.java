package org.ironrhino.corpwechat.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
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

}
