package org.ironrhino.wechat.model;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class WechatMaterialList implements Serializable {

	private static final long serialVersionUID = 2658037597334634022L;

	private int total_count;

	private int item_count;

	private List<WechatMaterial> item;

}
