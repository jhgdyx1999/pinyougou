package com.pinyougou.sellergoods.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.pinyougou.compositeEntity.GoodsAndGoodsDescAndItems;
import com.pinyougou.entity.PageResult;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.pojo.TbGoodsExample.Criteria;
import com.pinyougou.sellergoods.service.GoodsService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
@Transactional
public class GoodsServiceImpl implements GoodsService {

    @Resource
    private TbGoodsMapper goodsMapper;
    @Resource
    private TbGoodsDescMapper goodsDescMapper;
    @Resource
    private TbSellerMapper sellerMapper;
    @Resource
    private TbItemCatMapper itemCatMapper;
    @Resource
    private TbBrandMapper brandMapper;
    @Resource
    private TbItemMapper itemMapper;

    /**
     * 查询全部
     */
    @Override
    public List<TbGoods> findAll() {
        return goodsMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(null);
        return new PageResult<>(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Override
    public void add(GoodsAndGoodsDescAndItems goodsAndGoodsDescAndItems) {
        TbGoods goods = goodsAndGoodsDescAndItems.getGoods();goods.setAuditStatus("0");
        goodsMapper.insert(goods);

        TbGoodsDesc goodsDesc = goodsAndGoodsDescAndItems.getGoodsDesc();
        goodsDesc.setGoodsId(goods.getId());
        goodsDescMapper.insert(goodsDesc);
        //判定是否启用规格
        if ("1".equals(goods.getIsEnableSpec())){
            List<TbItem> itemList = goodsAndGoodsDescAndItems.getItemList();
            for (TbItem item:itemList){
                //生商品名
                Map specMap = (Map) JSON.parse(item.getSpec());
                StringBuilder titleBuilder = new StringBuilder(goods.getGoodsName());
                for (Object key:specMap.keySet()){
                    titleBuilder.append(" ");
                    titleBuilder.append(specMap.get(key));
                }
                String title = titleBuilder.toString();
                item.setTitle(title);
                setCommonProperties(item,goods,goodsDesc);
                itemMapper.insert(item);
            }
        }else {
            TbItem item = new TbItem();
            item.setTitle(goods.getGoodsName());
            item.setPrice(goods.getPrice());
            item.setNum(9999);
            item.setStatus("1");
            item.setIsDefault("1");
            item.setSpec("{}");
            setCommonProperties(item,goods,goodsDesc);
            itemMapper.insert(item);
        }
    }


    public void setCommonProperties(TbItem item,TbGoods goods,TbGoodsDesc goodsDesc){
        //查询商家
        String sellerId = goods.getSellerId();
        TbSeller seller = sellerMapper.selectByPrimaryKey(sellerId);
        //商家信息
        item.setSellerId(sellerId);//商家id
        item.setSeller(seller.getNickName());//商家店铺名
        //商品图片
        List<Map> ItemImagesList = JSON.parseArray(goodsDesc.getItemImages(), Map.class);
        if (ItemImagesList.size() != 0){
            item.setImage(ItemImagesList.get(0).get("url").toString());
        }
        //分类信息
        item.setCategoryid(goods.getCategory3Id());
        item.setCategory(itemCatMapper.selectByPrimaryKey(goods.getCategory3Id()).getName());
        //创建时间和跟新时间
        item.setCreateTime(new Date());
        item.setUpdateTime(new Date());
        //商品id
        item.setGoodsId(goods.getId());
        //品牌
        item.setBrand(brandMapper.selectByPrimaryKey(goods.getBrandId()).getName());
    }

    /**
     * 修改
     */
    @Override
    public void update(GoodsAndGoodsDescAndItems goods) {

    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public TbGoods findOne(Long id) {
        return goodsMapper.selectByPrimaryKey(id);
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            goodsMapper.deleteByPrimaryKey(id);
        }
    }


    @Override
    public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbGoodsExample example = new TbGoodsExample();
        Criteria criteria = example.createCriteria();

        if (goods != null) {
            if (goods.getSellerId() != null && goods.getSellerId().length() > 0) {
                criteria.andSellerIdEqualTo( goods.getSellerId() );
            }
            if (goods.getGoodsName() != null && goods.getGoodsName().length() > 0) {
                criteria.andGoodsNameLike("%" + goods.getGoodsName() + "%");
            }
            if (goods.getAuditStatus() != null && goods.getAuditStatus().length() > 0) {
                criteria.andAuditStatusLike("%" + goods.getAuditStatus() + "%");
            }
            if (goods.getIsMarketable() != null && goods.getIsMarketable().length() > 0) {
                criteria.andIsMarketableLike("%" + goods.getIsMarketable() + "%");
            }
            if (goods.getCaption() != null && goods.getCaption().length() > 0) {
                criteria.andCaptionLike("%" + goods.getCaption() + "%");
            }
            if (goods.getSmallPic() != null && goods.getSmallPic().length() > 0) {
                criteria.andSmallPicLike("%" + goods.getSmallPic() + "%");
            }
            if (goods.getIsEnableSpec() != null && goods.getIsEnableSpec().length() > 0) {
                criteria.andIsEnableSpecLike("%" + goods.getIsEnableSpec() + "%");
            }
            if (goods.getIsDelete() != null && goods.getIsDelete().length() > 0) {
                criteria.andIsDeleteLike("%" + goods.getIsDelete() + "%");
            }

        }

        Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(example);
        return new PageResult<>(page.getTotal(), page.getResult());
    }

}
