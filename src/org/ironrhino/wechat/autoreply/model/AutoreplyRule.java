package org.ironrhino.wechat.autoreply.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Version;

import org.apache.commons.lang3.StringUtils;
import org.ironrhino.core.metadata.Authorize;
import org.ironrhino.core.metadata.AutoConfig;
import org.ironrhino.core.metadata.Hidden;
import org.ironrhino.core.metadata.Richtable;
import org.ironrhino.core.metadata.UiConfig;
import org.ironrhino.core.model.BaseRecordableEntity;
import org.ironrhino.core.model.Enableable;
import org.ironrhino.core.model.Ordered;
import org.ironrhino.core.security.role.UserRole;
import org.ironrhino.core.struts.ValidationException;

@AutoConfig
@Authorize(ifAllGranted = UserRole.ROLE_ADMINISTRATOR)
@Table(name = "autoreply_rule")
@Entity
@Richtable(alias = "自动回复规则")
public class AutoreplyRule extends BaseRecordableEntity implements Enableable, Ordered<AutoreplyRule> {

	private static final long serialVersionUID = 7685688986308057811L;

	@Column(nullable = false)
	@UiConfig(alias = "匹配模式", width = "150px", cssClass = "conjunct", dynamicAttributes = "{\"data-replacement\":\"control-group-autoreplyRule-keyword\"}")
	@Enumerated
	private MatchMode matchMode;

	@UiConfig(alias = "关键词", width = "200px", hiddenInInput = @Hidden(expression = "entity.matchMode??&&entity.matchMode.name()=='ANY'"), hiddenInView = @Hidden(expression = "entity.matchMode??&&entity.matchMode.name()=='ANY'"))
	private String keyword;

	@Column(nullable = false)
	@UiConfig(alias = "回复内容", type = "textarea")
	private String content;

	@UiConfig(alias = "备注", type = "textarea", width = "200px")
	private String description;

	@UiConfig(width = "80px")
	private boolean enabled;

	@UiConfig(width = "80px")
	private int displayOrder;

	@Version
	private int version = -1;

	public MatchMode getMatchMode() {
		return matchMode;
	}

	public void setMatchMode(MatchMode matchMode) {
		this.matchMode = matchMode;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public int getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(int displayOrder) {
		this.displayOrder = displayOrder;
	}

	@PrePersist
	@PreUpdate
	private void validate() {
		if (matchMode == MatchMode.ANY)
			this.keyword = null;
		else if (StringUtils.isBlank(this.keyword)) {
			ValidationException ve = new ValidationException();
			ve.addFieldError("keyword", "不能为空");
			throw ve;
		}
	}

}
