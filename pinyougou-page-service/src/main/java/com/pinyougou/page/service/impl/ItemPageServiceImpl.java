package com.pinyougou.page.service.impl;

import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author:Jack
 * @description:
 * @Date:created in 2018/10/21,11:27
 */
@Service
public class ItemPageServiceImpl implements ItemPageService {

    @Value("${item.page.dir}")
    private String itemPageDir;
    @Resource
    private TbGoodsMapper goodsMapper;
    @Resource
    private TbGoodsDescMapper goodsDescMapper;
    @Resource
    private TbItemCatMapper itemCatMapper;
    @Resource
    private FreeMarkerConfig freeMarkerConfig;
    @Resource
    private TbItemMapper itemMapper;


    @Override
    public boolean generateItemHtml(Long id) {
        Map<String, Object> dataModel = new HashMap<>();
        //获取商品
        TbGoods goods = goodsMapper.selectByPrimaryKey(id);
        //商品描述
        TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(id);
        //三级分类
        String itemCat1Name = itemCatMapper.selectByPrimaryKey(goods.getCategory1Id()).getName();
        String itemCat2Name = itemCatMapper.selectByPrimaryKey(goods.getCategory2Id()).getName();
        String itemCat3Name = itemCatMapper.selectByPrimaryKey(goods.getCategory3Id()).getName();
        //sku
        TbItemExample itemExample = new TbItemExample();
        itemExample.createCriteria().andGoodsIdEqualTo(id);
        itemExample.setOrderByClause("is_default DESC");
        List<TbItem> itemList = itemMapper.selectByExample(itemExample);

        Configuration configuration = freeMarkerConfig.getConfiguration();
        try {
            Template template = configuration.getTemplate("item.ftl");
            //数据模型
            dataModel.put("goods", goods);
            dataModel.put("goodsDesc", goodsDesc);
            dataModel.put("itemCat1Name", itemCat1Name);
            dataModel.put("itemCat2Name", itemCat2Name);
            dataModel.put("itemCat3Name", itemCat3Name);
            dataModel.put("itemList", itemList);
            //输出页面
            Writer writer = new FileWriter(new File(itemPageDir + id + ".html"));
            template.process(dataModel, writer);
            writer.close();
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    @Override
    public void deleteItemHtml(Long[] ids) {
        for (Long id : ids) {
            FileUtils.deleteQuietly(new File(itemPageDir + id + ".html"));
        }
    }
}
