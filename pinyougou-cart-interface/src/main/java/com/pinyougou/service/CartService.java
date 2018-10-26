package com.pinyougou.service;

import com.pinyougou.compositeEntity.Cart;

import java.util.List;

/**
 * @author:Jack
 * @description:
 * @Date:created in 2018/10/25,13:27
 */
public interface CartService {

    public List<Cart> addGoodsToCartList(List<Cart> cartList,Long itemId,Integer num);

    public List<Cart> selectCartListFromRedis(String username);

    public void saveCartListToRedis(String username,List<Cart> cartList);

    public  List<Cart> mergeCartList(List<Cart> cartList1,List<Cart> cartList2);
}
