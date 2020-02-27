package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.cart.service.CartService;
import entity.Result;
import groupEntity.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import utils.CookieUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;
@RestController
@RequestMapping("/cart")
public class CartController {

    @Reference
    private CartService cartService;

    @Autowired
    private HttpSession session;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    private String getSessionId(){
        //1、尝试基于cookieUtil获取sessionId  ,sessionId基于cookie保存一周
       String sessionId =  CookieUtil.getCookieValue(request,"cookie_cartlist_unlogin");
        if (sessionId==null) {
            //重新获取的sessionId
            sessionId = session.getId();
            //将重新获取的sessionId保存一周
            CookieUtil.setCookie(request,response,"cookie_cartlist_unlogin",sessionId,3600*24*7,"utf-8");
        }
        return sessionId;
    }


    /**
     * 查询购物车列表
     * @return
     */
    @RequestMapping("/findCartList")
    public List<Cart> findCartList(){
        //获取登录人登录名
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        String sessionId = getSessionId();
        //未登录，基于sessionId做为key充redis取值
        List<Cart> cartList_sessionId = cartService.findCartListFromRedis(sessionId);

        if("anonymousUser".equals(username)){//未登录
            System.out.println("select cartlist by sessionId............");
            return cartList_sessionId;
        }else{
            System.out.println("select cartlist by username............");
            //已登录
            List<Cart> cartList_username = cartService.findCartListFromRedis(username);
            //登录前是否添加商品到购物车
            if(cartList_sessionId!=null && cartList_sessionId.size()>0){
                //登录前，添加了商品到购物车
                //合并登录前的购物车列表到登录后的购物车列表中
                cartList_username =cartService.mergeCartList(cartList_sessionId,cartList_username);
                //清除登录前的购物车列表
                cartService.deleteCartList(sessionId);
                //合并后的购物车列表重新存入redis
                cartService.saveCartListToRedisByUserName(username,cartList_username);
            }

            return cartList_username;
        }

    }


    @RequestMapping("/addItemToCartList")
    @CrossOrigin(origins = "http://item.pinyougou.com",allowCredentials = "true")
    public Result addItemToCartList(Long itemId, Integer num){
        try {
            //获取登录人登录名
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            System.out.println(username);

            //获取购物车列表
            List<Cart> cartList =  findCartList();
            //添加商品到购物车列表
            cartList = cartService.addItemToCartList(cartList,itemId,num);
            if("anonymousUser".equals(username)){//未登录
                System.out.println("add cartlist by sessionId ..........");
                //将新购物车列表重新存入redis中
                String sessionId = getSessionId();
                cartService.saveCartListToRedisBySessionId(sessionId,cartList);
            }else{
                System.out.println("add cartlist by username ..........");
                //已登录  基于用户名作为key 将新购物车列表重新存入redis中
                cartService.saveCartListToRedisByUserName(username,cartList);
            }


            return new Result(true,"添加购物车成功");
        }catch (RuntimeException e) {
            e.printStackTrace();
            return new Result(false,e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"添加购物车失败");
        }

    }
}
