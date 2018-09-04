package com.pinyougou.solrutil;

import com.alibaba.fastjson.JSON;
import com.pinyougou.entity.SolrItem;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 实现商品数据的查询(已审核商品)
 */
@Component
public class SolrUtil {
    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private SolrTemplate solrTemplate;
    /**
     * 导入商品数据
     */
    public void importItemData(){
        //从数据库库查询商品
        TbItem where = new TbItem();
        //只查找有效的商品
        where.setStatus("1");
        List<TbItem> items = itemMapper.select(where);
        //solr对象列表
        List<SolrItem> solrItems = new ArrayList<>();
        SolrItem solrItem = null;
        for (TbItem item : items) {
            System.out.println(item.getId() + " " + item.getTitle() + "  " + item.getPrice());
            //使用spring的BeanUtils深克隆对象
            solrItem = new SolrItem();
            BeanUtils.copyProperties(item,solrItem);
            //规格-->动态域
            Map specMap = JSON.parseObject(item.getSpec(), Map.class);
            solrItem.setSpecMap(specMap);
            solrItems.add(solrItem);
        }
        //存到索引库
        solrTemplate.saveBeans(solrItems);
        solrTemplate.commit();
    }

    public static void main(String[] args) {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
        SolrUtil solrUtil = applicationContext.getBean(SolrUtil.class);
        solrUtil.importItemData();

    }

}
