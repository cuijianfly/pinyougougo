package com.pinyougou.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.service.SellerService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * 权限认证拓展：从数据库查询登录用户
 */
public class UserDetailsServiceImpl implements UserDetailsService {
    //注入服务
    @Reference
    private SellerService sellerService;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("认证方法执行了。。。"+username);
        //构造用户的角色权限列表
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_SELLER"));
        //根据用户名到数据库查询商家信息
        TbSeller one = sellerService.findOne(username);
        //如果查找到相关商家信息，并且商家为已审核状态
        if(one != null && "1".equals(one.getStatus())){
            //入参为用户传入的用户名，数据库查询出的密码，
            return new User(username,one.getPassword(),authorities);
        }else{
            return null;
        }
    }
}
