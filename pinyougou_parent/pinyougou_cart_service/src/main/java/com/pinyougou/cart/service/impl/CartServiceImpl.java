package com.pinyougou.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import groupEntity.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
public class CartServiceImpl implements CartService {

    @Autowired
    private TbItemMapper itemMapper;

    /**
     * 判断商家对应的购物车是否在购物车列表中    根据商品id查询关联的商家id
         如果该商家对应的购物车不存在
             构建购物车对象  new Cart();
             关联商家信息
             构建购物车中的商品列表  new ArrayList();
             构建购物车商品对象，再添加到购物车商品列表中

             将构建的购物车商品列表添加到购物车对象中

             将创建购物车对象添加到购物车列表中

         如果该商家对应的购物车存在
             判断添加的商品是否存在购物车商品列表中
             如果不存在
             构建购物车商品对象，再添加到购物车商品列表中
             如果存在
             购物车中的商品数量、和小计金额需要重新统计
     * @param cartList
     * @param itemId
     * @param num
     * @return
     */
    @Override
    public List<Cart> addItemToCartList(List<Cart> cartList, Long itemId, Integer num) {
        // 根据商品id查询关联的商家id
        TbItem item = itemMapper.selectByPrimaryKey(itemId);
        if(item==null){
            throw new RuntimeException("很抱歉，商品已下架");
        }
        if(!item.getStatus().equals("1")){
            throw new RuntimeException("很抱歉，商品无效");
        }
        String sellerId = item.getSellerId();
        //判断商家对应的购物车是否在购物车列表中，如果存在，返回该购物车对象
        Cart cart = searchCartBySellerId(cartList,sellerId);
        if (cart==null) {//            如果该商家对应的购物车不存在
//            构建购物车对象  new Cart();
            cart = new Cart();
//            关联商家信息
            cart.setSellerId(sellerId);
            cart.setSellerName(item.getSeller());
//            构建购物车中的商品列表  new ArrayList();
            List<TbOrderItem> orderItemList = new ArrayList<>();
//            构建购物车商品对象，再添加到购物车商品列表中
            TbOrderItem orderItem =  createOrderItem(item,num);
            orderItemList.add(orderItem);
//                    将构建的购物车商品列表添加到购物车对象中
            cart.setOrderItemList(orderItemList);
//            将创建购物车对象添加到购物车列表中
            cartList.add(cart);
        }else{//            如果该商家对应的购物车存在
            List<TbOrderItem> orderItemList = cart.getOrderItemList();
//                    判断添加的商品是否存在购物车商品列表中
           TbOrderItem orderItem =  searchOrderItemByItemId(orderItemList,itemId);
            if (orderItem==null) {
//            如果不存在
//            构建购物车商品对象，再添加到购物车商品列表中
                orderItem =  createOrderItem(item,num);
                orderItemList.add(orderItem);
            }else{
//                    如果存在
//            购物车中的商品数量、和小计金额需要重新统计
                orderItem.setNum(orderItem.getNum()+num);
                orderItem.setTotalFee(new BigDecimal(orderItem.getPrice().doubleValue()*orderItem.getNum()));

                //商品数量减至0
                if (orderItem.getNum()<=0) {
                    orderItemList.remove(orderItem);
                }
                //购物车中商品全部删除，从购物车列表中删除购物车对象
                if (orderItemList.size()==0) {
                    cartList.remove(cart);
                }

            }
        }

        return cartList;
    }



    private TbOrderItem searchOrderItemByItemId(List<TbOrderItem> orderItemList, Long itemId) {
        for (TbOrderItem orderItem : orderItemList) {
            if (orderItem.getItemId().longValue()==itemId.longValue()) {
                return orderItem;
            }
        }
        return null;
    }

    /**
     `item_id` bigint(20) NOT NULL COMMENT '商品id',
     `goods_id` bigint(20) DEFAULT NULL COMMENT 'SPU_ID',
     `order_id` bigint(20) NOT NULL COMMENT '订单id',
     `title` varchar(200) COLLATE utf8_bin DEFAULT NULL COMMENT '商品标题',
     `price` decimal(20,2) DEFAULT NULL COMMENT '商品单价',
     `num` int(10) DEFAULT NULL COMMENT '商品购买数量',
     `total_fee` decimal(20,2) DEFAULT NULL COMMENT '商品总金额',
     `pic_path` varchar(200) COLLATE utf8_bin DEFAULT NULL COMMENT '商品图片地址',
     `seller_id` varchar(100) COLLATE utf8_bin DEFAULT NULL,
     * @param item
     * @param num
     * @return
     */
    private TbOrderItem createOrderItem(TbItem item, Integer num) {
        if(num<1){
            throw new RuntimeException("非法操作");
        }

        TbOrderItem orderItem = new TbOrderItem();
        orderItem.setItemId(item.getId());
        orderItem.setGoodsId(item.getGoodsId());
        orderItem.setTitle(item.getTitle());
        orderItem.setPrice(item.getPrice());
        orderItem.setNum(num);
        orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue()*num));
        orderItem.setPicPath(item.getImage());
        orderItem.setSellerId(item.getSellerId());
        return orderItem;
    }

    private Cart searchCartBySellerId(List<Cart> cartList, String sellerId) {
        for (Cart cart : cartList) {
            if (cart.getSellerId().equals(sellerId)) {
                return cart;
            }
        }
        return null;
    }

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void saveCartListToRedisBySessionId(String sessionId, List<Cart> cartList) {
        redisTemplate.boundValueOps(sessionId).set(cartList,7L, TimeUnit.DAYS);
    }

    @Override
    public List<Cart> findCartListFromRedis(String sessionId) {
        List<Cart> cartList = (List<Cart>) redisTemplate.boundValueOps(sessionId).get();
        if(cartList==null){
            cartList = new ArrayList<>();
        }
        return cartList;
    }

    @Override
    public void saveCartListToRedisByUserName(String username, List<Cart> cartList) {
        redisTemplate.boundValueOps(username).set(cartList);
    }

    @Override
    public List<Cart> mergeCartList(List<Cart> cartList_sessionId, List<Cart> cartList_username) {
        //登录前的购物车列表
        for (Cart cart : cartList_sessionId) {
            //登录前的购物车商品列表
            List<TbOrderItem> orderItemList = cart.getOrderItemList();
            for (TbOrderItem orderItem : orderItemList) {
                Long itemId = orderItem.getItemId();
                Integer num = orderItem.getNum();
                cartList_username = addItemToCartList(cartList_username,itemId,num);
            }
        }
        return cartList_username;
    }

    @Override
    public void deleteCartList(String sessionId) {
        redisTemplate.delete(sessionId);
    }
}
