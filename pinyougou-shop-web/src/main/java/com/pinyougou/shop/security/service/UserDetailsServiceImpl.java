package com.pinyougou.shop.security.service;

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
 * @author:Jack
 * @description:
 * @Date:created in 2018/10/12,20:58
 */
@SuppressWarnings("JavaDoc")
public class UserDetailsServiceImpl implements UserDetailsService {

    private SellerService sellerService;

    public UserDetailsServiceImpl(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_SELLER"));

        TbSeller seller = sellerService.findOne(username);
        if (seller != null && "1".equals(seller.getStatus())){
            return new User(username, seller.getPassword(), grantedAuthorities);
        }
        //使用抛该异常的方式表示认证失败，不采用返回null值的方式
       throw new UsernameNotFoundException("认证失败...");
    }
}
