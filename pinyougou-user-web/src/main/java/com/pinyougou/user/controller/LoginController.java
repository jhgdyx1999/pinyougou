package com.pinyougou.user.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author:Jack
 * @description:
 * @Date:created in 2018/10/24,21:26
 */
@RestController
@RequestMapping("/login")
public class LoginController {

    @RequestMapping("/getLoginName")
    public Map getLoginName(){
        Map<String,Object> map = new HashMap<>();
        map.put("loginName", SecurityContextHolder.getContext().getAuthentication().getName());
        return  map;
    }
}
