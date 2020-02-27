package com.pinyougou.cart.service;

import groupEntity.Cart;

import java.util.List;

public interface CartService {
    /**
     * 添加商品到购物车列表
     */
    public List<Cart> addItemToCartList(List<Cart> cartList,Long itemId,Integer num);

    void saveCartListToRedisBySessionId(String sessionId, List<Cart> cartList);

    List<Cart> findCartListFromRedis(String sessionId);

    void saveCartListToRedisByUserName(String username, List<Cart> cartList);

    List<Cart> mergeCartList(List<Cart> cartList_sessionId, List<Cart> cartList_username);

    void deleteCartList(String sessionId);
}
