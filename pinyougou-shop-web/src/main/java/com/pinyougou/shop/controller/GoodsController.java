package com.pinyougou.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.compositeEntity.GoodsAndGoodsDescAndItems;
import com.pinyougou.entity.PageResult;
import com.pinyougou.entity.Result;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.sellergoods.service.GoodsService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * controller
 *
 * @author Administrator
 */
@SuppressWarnings("Duplicates")
@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Reference
    private GoodsService goodsService;

    /**
     * 返回全部列表
     *
     * @return
     */
    @RequestMapping("/findAll")
    public List<TbGoods> findAll() {
        return goodsService.findAll();
    }


    /**
     * 返回全部列表
     *
     * @return
     */
    @RequestMapping("/findPage")
    public PageResult findPage(int page, int rows) {
        return goodsService.findPage(page, rows);
    }

    /**
     * 增加
     *
     * @param goodsAndGoodsDescAndItems
     * @return
     */
    @RequestMapping("/add")
    public Result add(@RequestBody GoodsAndGoodsDescAndItems goodsAndGoodsDescAndItems) {
        String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
        goodsAndGoodsDescAndItems.getGoods().setSellerId(sellerId);
        try {
            goodsService.add(goodsAndGoodsDescAndItems);
            return new Result(true, "增加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "增加失败");
        }
    }

    /**
     * 修改
     *
     * @param goodsAndGoodsDescAndItems
     * @return
     */
    @RequestMapping("/update")
    public Result update(@RequestBody GoodsAndGoodsDescAndItems goodsAndGoodsDescAndItems) {
        String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
        GoodsAndGoodsDescAndItems targetGoodsAndGoodsDescAndItems = goodsService.findOne(goodsAndGoodsDescAndItems.getGoods().getId());
        String targetSellerId = targetGoodsAndGoodsDescAndItems.getGoods().getSellerId();
        //如果待修改的商品不属于当前商家,或者当前提交的sellerId与登录的用户不符合,则为"非法操作"
        if (!sellerId.equals(targetSellerId) || !sellerId.equals(goodsAndGoodsDescAndItems.getGoods().getSellerId())){
            return new Result(false, "非法操作");
        }
        try {
            goodsService.update(goodsAndGoodsDescAndItems);
            return new Result(true, "修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "修改失败");
        }
    }

    /**
     * 获取实体
     *
     * @param id
     * @return
     */
    @RequestMapping("/findOne")
    public GoodsAndGoodsDescAndItems findOne(Long id) {
        return goodsService.findOne(id);
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @RequestMapping("/delete")
    public Result delete(Long[] ids) {
        try {
            goodsService.delete(ids);
            return new Result(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除失败");
        }
    }

    /**
     * 查询+分页
     *
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping("/search")
    public PageResult search(@RequestBody TbGoods goods, int page, int rows) {
        String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
        goods.setSellerId(sellerId);
        return goodsService.findPage(goods, page, rows);
    }


    @RequestMapping("/updateIsMarketableStatus")
    public Result updateIsMarketableStatus(Long[] ids,String status){
        try {
            for (Long id:ids ) {
                GoodsAndGoodsDescAndItems goodsAndGoodsDescAndItems = goodsService.findOne(id);
                if (!"1".equals(goodsAndGoodsDescAndItems.getGoods().getAuditStatus())){
                    return new Result(false, "审核未通过的商品无法上下架");
                }
            }
            goodsService.updateIsMarketableStatus(ids, status);
            return new Result(true, "操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "操作失败");
        }


    }

}
