package com.pinyougou.seckill.service.impl;

import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.mapper.TbSeckillOrderMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import utils.IdWorker;

import java.util.Date;
import java.util.Map;

@Component
public class CreateOrder implements Runnable {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private TbSeckillOrderMapper seckillOrderMapper;

    @Autowired
    private TbSeckillGoodsMapper seckillGoodsMapper;

    @Override
    public void run() {
        //从redis队列中获取下单任务
        Map<String,Object> orderMap  = (Map<String, Object>) redisTemplate.boundListOps("seckill_order_queue").rightPop();
        String userId = (String) orderMap.get("userId");
        Long seckillGoodsId = (Long) orderMap.get("seckillGoodsId");
        TbSeckillGoods seckillGoods = (TbSeckillGoods) redisTemplate.boundHashOps("seckill_goods").get(seckillGoodsId);
        //2、组装订单数据
        /*`id` bigint(20) NOT NULL COMMENT '主键',
	  `seckill_id` bigint(20) DEFAULT NULL COMMENT '秒杀商品ID',
	  `money` decimal(10,2) DEFAULT NULL COMMENT '支付金额',  //秒杀商品的秒杀价格
	  `user_id` varchar(50) DEFAULT NULL COMMENT '用户',
	  `seller_id` varchar(50) DEFAULT NULL COMMENT '商家', //秒杀商品关联的商家id值
	  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
	  `status` varchar(1) DEFAULT NULL COMMENT '状态', // 1 未支付*/
        TbSeckillOrder seckillOrder = new TbSeckillOrder();
        seckillOrder.setId(idWorker.nextId());
        seckillOrder.setSeckillId(seckillGoodsId);
        seckillOrder.setMoney(seckillGoods.getCostPrice());
        seckillOrder.setUserId(userId);
        seckillOrder.setSellerId(seckillGoods.getSellerId());
        seckillGoods.setCreateTime(new Date());
        seckillOrder.setStatus("1");

        //3、保存订单  实际业务：将生成的订单存入redis，等支付成功后，在同步数据库
        seckillOrderMapper.insert(seckillOrder);
        //4、操作redis，扣减秒杀库存
        seckillGoods.setStockCount(seckillGoods.getStockCount()-1);

        //5、当用户下单完成后，记录该用户购买了当前商品
        redisTemplate.boundSetOps("seckill_goods_"+seckillGoodsId).add(userId);

        //当用户下单完成后，排队人数减一
        redisTemplate.boundValueOps("seckill_paidui_queue_" + seckillGoodsId).increment(-1);

        //5、如果秒杀商品库存为0或者秒杀活动结束（当前时间大于秒杀结束时间），同步秒杀商品库存数据到数据库，清除redis中的秒杀商品
        if (seckillGoods.getStockCount()< 1 || (new Date().getTime() > seckillGoods.getEndTime().getTime())) {
            //同步秒杀商品库存数据到数据库
            seckillGoodsMapper.updateByPrimaryKey(seckillGoods);
            //清除redis中的秒杀商品
            redisTemplate.boundHashOps("seckill_goods").delete(seckillGoodsId);
        }else{
            //扣减秒杀库存
            redisTemplate.boundHashOps("seckill_goods").put(seckillGoodsId,seckillGoods);
        }

    }
}
