package org.ironrhino.corpwechat.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.ironrhino.core.model.Attribute;

import lombok.Data;

@Data
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

}
