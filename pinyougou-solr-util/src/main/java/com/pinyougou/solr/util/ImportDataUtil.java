package com.pinyougou.solr.util;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.SolrDataQuery;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author:Jack
 * @description:
 * @Date:created in 2018/10/18,10:57
 */

@Component
public class ImportDataUtil {

    @Autowired
    private TbItemMapper itemMapper;
    @Autowired
    private SolrTemplate solrTemplate;

    private void importItemData(){
        TbItemExample itemExample = new TbItemExample();
        itemExample.createCriteria().andStatusEqualTo("1");
        List<TbItem> itemList = itemMapper.selectByExample(itemExample);
        for (TbItem item:itemList){
            Map specMap = JSON.parseObject(item.getSpec(), Map.class);
            item.setSpecMap(specMap);
            solrTemplate.saveBean(item);
        }
        solrTemplate.commit();
    }

    private void deleteAllItem(){
        SolrDataQuery solrDataQuery = new SimpleQuery("*:*");
        solrTemplate.delete(solrDataQuery);
        solrTemplate.commit();

    }

    private void deleteById(String id){
        solrTemplate.deleteById(id);
        solrTemplate.commit();
    }

    public static void main(String[] args) {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
        ImportDataUtil importDataUtil = (ImportDataUtil) applicationContext.getBean("importDataUtil");
//        importDataUtil.importItemData();
//        importDataUtil.deleteAllItem();
        importDataUtil.deleteById("1369306");
    }
}
