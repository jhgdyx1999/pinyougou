package com.pinyougou.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.compositeEntity.Cart;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.service.CartService;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author:Jack
 * @description:
 * @Date:created in 2018/10/25,13:29
 */
@Service
public class CartServiceImpl implements CartService {

    @Resource
    private TbItemMapper itemMapper;
    @Resource
    private RedisTemplate<String,Object> redisTemplate;



    @Override
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) {
        //根据SKU id查询商品
        TbItem item = itemMapper.selectByPrimaryKey(itemId);
        if (item == null || !item.getStatus().equals("1")) {
            //校验商品状态
            throw new RuntimeException("SKU商品异常");
        }
        String sellerId = item.getSellerId();
        //判断购物车列表内是否包含该商家
        Cart cart = selectCartBySellerId(sellerId, cartList);
        if (cart == null) {  //购物车列表不包含该商家
            //创建新的购物车对象
            cart = new Cart();
            cart.setSellerId(sellerId);
            cart.setSellerName(item.getSeller());
            //创建购物车明细列表对象
            List<TbOrderItem> orderItemList = new ArrayList<>();
            //创建购物车明细
            TbOrderItem orderItem = createOrderItem(item, num);

            orderItemList.add(orderItem);
            cart.setOrderItemList(orderItemList);
            cartList.add(cart);
        } else { //购物车列表包含该商家
            //判断购物车商品明细列表中是否包含该商品
            List<TbOrderItem> orderItemList = cart.getOrderItemList();
            TbOrderItem orderItem = selectOrderItemByItemId(orderItemList, itemId);
            if (orderItem == null) {
                //购物车商品明细列表中不包含该商品
                orderItem = createOrderItem(item, num);
                orderItemList.add(orderItem);
                cart.setOrderItemList(orderItemList);
            } else {
                //购物车商品明细列表中包含该商品
                orderItem.setNum(orderItem.getNum() + num);
                orderItem.setTotalFee(new BigDecimal(orderItem.getNum()).multiply(orderItem.getPrice()));
            }
            if (orderItem.getNum() <= 0) {
                //该SKU不在明细列表内
                orderItemList.remove(orderItem);
            }
            if (orderItemList.size() == 0) {
                //购物车列表内没有该商家的商品
                cartList.remove(cart);
            }
        }
        return cartList;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Cart> selectCartListFromRedis(String username) {
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(username);
        if (cartList == null){
            cartList = new ArrayList<>();
        }
        return cartList;
    }

    @Override
    public void saveCartListToRedis(String username, List<Cart> cartList) {
        redisTemplate.boundHashOps("cartList").put(username, cartList);
    }

    /**
     * 合并购物车
     * @param cartList1
     * @param cartList2
     * @return
     */
    @Override
    public List<Cart> mergeCartList(List<Cart> cartList1, List<Cart> cartList2) {
        for (Cart cart : cartList1){
            List<TbOrderItem> orderItemList = cart.getOrderItemList();
            for (TbOrderItem orderItem:orderItemList ){
                addGoodsToCartList(cartList2,orderItem.getItemId(),orderItem.getNum());
            }
        }
        return cartList2;
    }

    /**
     * 查询购物车列表内的某商家的购物车
     *
     * @param sellerId
     * @param cartList
     * @return
     */
    private Cart selectCartBySellerId(String sellerId, List<Cart> cartList) {
        for (Cart cart : cartList) {
            if (cart.getSellerId().equals(sellerId)) {
                //购物车列表内包含该商家
                return cart;
            }
        }
        return null;
    }

    /**
     * 查询商品明细列表中的某商品
     *
     * @param orderItemList
     * @param itemId
     * @return
     */
    private TbOrderItem selectOrderItemByItemId(List<TbOrderItem> orderItemList, Long itemId) {
        for (TbOrderItem orderItem : orderItemList) {
            if (orderItem.getItemId().equals(itemId)) {
                return orderItem;
            }
        }
        return null;
    }

    /**
     * 创建购物车明细对象
     *
     * @param item
     * @param num
     * @return
     */
    private TbOrderItem createOrderItem(TbItem item, Integer num) {
        TbOrderItem orderItem = new TbOrderItem();
        orderItem.setGoodsId(item.getGoodsId());
        orderItem.setItemId(item.getId());
        orderItem.setNum(num);
        orderItem.setPicPath(item.getImage());
        orderItem.setPrice(item.getPrice());
        orderItem.setSellerId(item.getSellerId());
        orderItem.setTitle(item.getTitle());
        orderItem.setTotalFee(new BigDecimal(num).multiply(item.getPrice()));
        return orderItem;
    }
}
