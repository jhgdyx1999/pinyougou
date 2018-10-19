package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.data.domain.Page;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author:Jack
 * @description:
 * @Date:created in 2018/10/18,14:36
 */
@SuppressWarnings("CollectionAddAllCanBeReplacedWithConstructor")
@Service(timeout = 5000)
public class ItemSearchServiceImpl implements ItemSearchService {

    @Resource
    private SolrTemplate solrTemplate;

    @Override
    public Map<String, Object> search(Map searchMap) {
        Map<String, Object> resultMap = new HashMap<>();
        //查询商品列表
        Map<String, Object> itemListMap = searchItemList(searchMap);
        //查询商品分类列表
        Map<String, Object> categoryListMap = searchCategoryList(searchMap);


        resultMap.putAll(itemListMap);
        resultMap.putAll(categoryListMap);
        return resultMap;
    }

    /**
     * 查询商品列表
     * @param searchMap
     * @return
     */
    private Map<String,Object> searchItemList(Map searchMap){
        Map<String, Object> itemListMap = new HashMap<>();
        //创建高亮查询对象
        HighlightQuery query = new SimpleHighlightQuery();
        //自定义高亮格式
        HighlightOptions highlightOptions = new HighlightOptions()
                .addField("item_title").setSimplePrefix("<span style='color:red'>").setSimplePostfix("</span>");
        query.setHighlightOptions(highlightOptions);
        //设置查询条件
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        //获取高亮页面对象
        HighlightPage<TbItem> highlightPage = solrTemplate.queryForHighlightPage(query, TbItem.class);

        List<HighlightEntry<TbItem>> highlightEntries = highlightPage.getHighlighted();

        for (HighlightEntry<TbItem> highlightEntry : highlightEntries){
            List<HighlightEntry.Highlight> highlights = highlightEntry.getHighlights();
            if (highlights.size()>0){
                for (HighlightEntry.Highlight highlight : highlights){
                    List<String> snipplets = highlight.getSnipplets();
                    if (snipplets.size()>0){
                        for (String highLightedTitle : snipplets){
                            highlightEntry.getEntity().setTitle(highLightedTitle);
                        }
                    }
                }
            }
        }
        itemListMap.put("rows", highlightPage.getContent());

        return itemListMap;
    }

    private Map<String,Object> searchCategoryList(Map searchMap){
        Map<String, Object> categoryListMap = new HashMap<>();
        List<String> categoryList = new ArrayList<>();

        Query query = new SimpleQuery("*:*");

        GroupOptions groupOptions = new GroupOptions().addGroupByField("item_category");
        query.setGroupOptions(groupOptions);
        //设置查询条件
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        //获取分组查询对象
        GroupPage<TbItem> itemGroupPage = solrTemplate.queryForGroupPage(query, TbItem.class);//itemGroupPage.getContent();此方法无效
        //获取分组结果对象
        GroupResult<TbItem> groupResult = itemGroupPage.getGroupResult("item_category");
        Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();

        List<GroupEntry<TbItem>> content = groupEntries.getContent();

        for (GroupEntry<TbItem> groupEntry : content){
            //获取每一个分类信息
            categoryList.add(groupEntry.getGroupValue());
        }
        categoryListMap.put("categoryList", categoryList);

        return categoryListMap;
    }


}
