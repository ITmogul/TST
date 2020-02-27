package com.pinyougou.seckill.service;

import com.pinyougou.pojo.TbSeckillGoods;

import java.util.List;

public interface SeckillService {
    /**
     * 查询秒杀商品列表
     */
    public List<TbSeckillGoods> findSeckillGoodsList();

    /**
     * 基于秒杀商品id查询秒杀商品
     */
    public TbSeckillGoods findOne(Long seckillGoodsId);

    /**
     * 秒杀下单功能
     */
    public void saveOrder(Long seckillGoodsId,String userId);
}
