package com.pinyougou.manager.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("login")
@RestController
public class LoginController {
    @RequestMapping("getLoginName")
    public Map<String,Object> getLoginName(){
        //获取用户名
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        Map<String,Object> map = new HashMap<>();
        map.put("loginName",name);
        return map;
    }

}
