package org.ironrhino.wechat.autoreply.service;

import java.io.Serializable;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.ironrhino.core.cache.CheckCache;
import org.ironrhino.core.cache.EvictCache;
import org.ironrhino.core.service.BaseManagerImpl;
import org.ironrhino.wechat.autoreply.model.AutoreplyRule;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AutoreplyRuleManagerImpl extends BaseManagerImpl<AutoreplyRule> implements AutoreplyRuleManager {

	private static final String CACHE_KEY = "AllAutoreplyRule";

	@EvictCache(key = CACHE_KEY)
	@Transactional
	public void save(AutoreplyRule obj) {
		super.save(obj);
	}

	@EvictCache(key = CACHE_KEY)
	@Transactional
	public void update(AutoreplyRule obj) {
		super.update(obj);
	}

	@EvictCache(key = CACHE_KEY)
	@Transactional
	public void delete(AutoreplyRule obj) {
		super.delete(obj);
	}

	@EvictCache(key = CACHE_KEY)
	@Transactional
	public List<AutoreplyRule> delete(Serializable... id) {
		return super.delete(id);
	}

	@CheckCache(key = CACHE_KEY)
	@Transactional(readOnly = true)
	public List<AutoreplyRule> findRules() {
		DetachedCriteria dc = detachedCriteria();
		dc.add(Restrictions.eq("enabled", true));
		dc.addOrder(Order.asc("displayOrder"));
		dc.addOrder(Order.asc("createDate"));
		return findListByCriteria(dc);
	}

}
