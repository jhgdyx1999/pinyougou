package com.pinyougou.shop.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author:Jack
 * @description:
 * @Date:created in 2018/10/12,14:09
 */

@RestController
@RequestMapping("/login")
public class LoginController {

    @RequestMapping("/name")
    public Map loginName(){
        System.out.println(111);
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        Map<String,Object> map  = new HashMap<>();
        map.put("loginName", name);
        return map;
    }
}
