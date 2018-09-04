package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.entity.SolrItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.ScoredPage;

import java.util.HashMap;
import java.util.Map;

/**
 * 搜索服务
 * 设置请求超时时间
 */
@Service(timeout = 5000)
public class ItemSearchServiceImpl implements ItemSearchService {
    @Autowired
    private SolrTemplate solrTemplate;
    /**
     * 搜索服务
     * @param searchMap
     * @return
     */
    public Map search(Map searchMap) {
        Query query = new SimpleQuery("*:*");
        //组装查询条件
        //item_keywords:复制域名称
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        ScoredPage<SolrItem> page = solrTemplate.queryForPage(query, SolrItem.class);
        Map map = new HashMap();
        map.put("rows",page.getContent());
        return map;
    }
}
