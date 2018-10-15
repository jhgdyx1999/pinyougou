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
    public TbGoods findOne(Long id) {
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

}
