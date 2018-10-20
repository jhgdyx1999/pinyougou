package com.pinyougou.search.service;

import com.pinyougou.pojo.TbItem;

import java.util.List;
import java.util.Map;

/**
 * @author:Jack
 * @description:
 * @Date:created in 2018/10/18,14:34
 */
public interface ItemSearchService {

    public Map<String,Object> search(Map searchMap);

    public void updateItems(List<TbItem> items);

    public void deleteByGoodsIds(Long[] ids);

}
