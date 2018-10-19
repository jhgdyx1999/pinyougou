package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import javax.annotation.Resource;
import java.util.*;

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
    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> search(Map searchMap) {
        Map<String, Object> resultMap = new HashMap<>();
        //查询商品列表
        Map<String, Object> itemListMap = searchItemList(searchMap);
        //查询商品分类列表
        Map<String, Object> categoryListMap = searchCategoryList(searchMap);
        List<String> categoryList = (List<String>) categoryListMap.get("categoryList");
        if (categoryList.size()>0){
            String categoryName = categoryList.get(0);
            if (searchMap.get("category") != null){
                categoryName = searchMap.get("category")+"";
            }
            //查询品牌列表
            Map<String, Object> brandListMap = searchBrandList(categoryName);
            //查询规格列表
            Map<String, Object> specListMap = searchSpecList(categoryName);
            resultMap.putAll(brandListMap);
            resultMap.putAll(specListMap);
        }
        resultMap.putAll(itemListMap);
        resultMap.putAll(categoryListMap);

        return resultMap;
    }
    /**
     * 查询品牌列表
     */
    @SuppressWarnings("unchecked")
    private Map<String,Object> searchBrandList(String categoryName){
        Map<String,Object> brandListMap =  new HashMap<>();
        Long typeId =  new Long(redisTemplate.boundHashOps("typeIdList").get(categoryName).toString());
        List<Map> brandMap = (List<Map>) redisTemplate.boundHashOps("brandList").get(typeId);
        brandListMap.put("brandList", brandMap);
        return brandListMap;
    }
    /**
     * 查询规格列表
     */
    @SuppressWarnings("unchecked")
    private Map<String,Object> searchSpecList(String categoryName){
        Map<String,Object> specListMap =  new HashMap<>();
        Long typeId =  new Long(redisTemplate.boundHashOps("typeIdList").get(categoryName).toString());
        List<Map> specMap = (List<Map>) redisTemplate.boundHashOps("specList").get(typeId);
        specListMap.put("specList", specMap);
        return specListMap;
    }

    /**
     * 查询商品列表
     * @param searchMap
     * @return
     */
    @SuppressWarnings("unchecked")
    private Map<String,Object> searchItemList(Map searchMap){
        Map<String, Object> itemListMap = new HashMap<>();
        //创建高亮查询对象
        HighlightQuery query = new SimpleHighlightQuery();
        //自定义高亮格式
        HighlightOptions highlightOptions = new HighlightOptions()
                .addField("item_title").setSimplePrefix("<span style='color:red'>").setSimplePostfix("</span>");
        query.setHighlightOptions(highlightOptions);
        /**
         *  设置查询条件
         */
        //根据关键字查询
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        //根据商品分类查询
        if (searchMap.get("category") != null){
            FilterQuery filterQuery = new SimpleFilterQuery(new Criteria("item_category").is(searchMap.get("category")));
            query.addFilterQuery(filterQuery);
        }
        //根据品牌查询
        if (searchMap.get("brand") != null){
            FilterQuery filterQuery = new SimpleFilterQuery(new Criteria("item_brand").is(searchMap.get("brand")));
            query.addFilterQuery(filterQuery);
        }
        //根据规格查询
        if (searchMap.get("spec") != null){
            Map<String,String> specMap = (Map<String,String>) searchMap.get("spec");
            for (Map.Entry<String,String> entry :specMap.entrySet()){
                FilterQuery filterQuery = new SimpleFilterQuery(new Criteria("item_spec_"+entry.getKey()).is(entry.getValue()));
                query.addFilterQuery(filterQuery);
            }
        }

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
        System.out.println(categoryList);
        categoryListMap.put("categoryList", categoryList);

        return categoryListMap;
    }


}
