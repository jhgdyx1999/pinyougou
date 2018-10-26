package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.common.util.CookieUtil;
import com.pinyougou.compositeEntity.Cart;
import com.pinyougou.entity.Result;
import com.pinyougou.service.CartService;
import org.opensaml.ws.wssecurity.Username;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author:Jack
 * @description:
 * @Date:created in 2018/10/25,14:36
 */

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;
    @Reference
    private CartService cartService;

    @RequestMapping("/selectCartListFromCookie")
    public List<Cart> selectCartListFromCookie() {
        //anonymousUser
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        //从cookie中获取购物车信息
        String cartListStr = CookieUtil.getCookieValue(request, "cartList", "UTF-8");
        if (StringUtils.isEmpty(cartListStr)) {
            cartListStr = "[ ]";
        }
        List<Cart> cartListFromCookie = JSON.parseArray(cartListStr, Cart.class);
        if ("anonymousUser".equals(username)) {
            //当前用户未登录
            return cartListFromCookie;
        } else {
            //当前用户已登录,从redis中获取购物车信息
            List<Cart> cartListFromRedis = cartService.selectCartListFromRedis(username);
            if (cartListFromCookie.size()>0){
                System.out.println("合并购物车");
                //浏览器cookie内的购物车有商品信息,则执行合并操作
                //合并购物车
                List<Cart> cartList = cartService.mergeCartList(cartListFromRedis, cartListFromCookie);
                cartService.saveCartListToRedis(username, cartList);
                //清除浏览器cookie
                CookieUtil.deleteCookie(request, response, "cartList");
                return cartList;
            }
            return cartListFromRedis;
        }
    }


    @RequestMapping("/addGoodsToCartList")
    public Result addGoodsToCartList(Long itemId, Integer num) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            if ("anonymousUser".equals(username)) {
                //当前用户未登录,向cookie中存储信息
                List<Cart> cartList = selectCartListFromCookie();
                cartList = cartService.addGoodsToCartList(cartList, itemId, num);
                CookieUtil.setCookie(request, response, "cartList", JSON.toJSONString(cartList), 3600 * 24, "UTF-8");
            } else {
                //当前用户已登录,向redis中存储购物车信息
                List<Cart> cartList = cartService.selectCartListFromRedis(username);
                cartList = cartService.addGoodsToCartList(cartList, itemId, num);
                cartService.saveCartListToRedis(username, cartList);
            }
            return new Result(true, "添加至购物车成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "添加至购物车失败");
        }
    }
}
