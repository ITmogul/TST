package com.pinyougou.user.service;

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

public class UserDetailServiceImpl implements UserDetailsService{

    private SellerService sellerService;

    public void setSellerService(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //做认证和授权操作
        //基于商家id查询商家对象
        TbSeller seller = sellerService.findOne(username);
        if(seller!=null){
            //只有审核通过的商家才能登录
            if ("1".equals(seller.getStatus())) {
                //参数三：用户权限集合
                List<GrantedAuthority> authorities = new ArrayList<>();
                //正常应该从数据库查询
                authorities.add(new SimpleGrantedAuthority("ROLE_SELLER"));
                return new User(username,seller.getPassword(),authorities);
            }else {
                return null;
            }
        }else{
            //当获取不到商家信息是，直接返回null即可
            return null;
        }

    }
}
