package com.pinyougou.seckill.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.seckill.service.SeckillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class SeckillServiceImpl implements SeckillService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<TbSeckillGoods> findSeckillGoodsList() {
        return redisTemplate.boundHashOps("seckill_goods").values();
    }

    @Override
    public TbSeckillGoods findOne(Long seckillGoodsId) {
        return (TbSeckillGoods) redisTemplate.boundHashOps("seckill_goods").get(seckillGoodsId);
    }

    @Autowired
    private ThreadPoolTaskExecutor executor;

    @Autowired
    private CreateOrder createOrder;

    @Override
    public void saveOrder(Long seckillGoodsId, String userId) {
        //判断当前用户是否购买个这个秒杀商品
        Boolean member = redisTemplate.boundSetOps("seckill_goods_" + seckillGoodsId).isMember(userId);
        if (member) {
            throw new RuntimeException("同一用户针对同一商品不能重复下单");
        }

        //进入下单方法后，排队人数加一操作
        Long increment = redisTemplate.boundValueOps("seckill_paidui_queue_" + seckillGoodsId).increment(1);


        //基于redis队列完成记录秒杀商品剩余库存量   []
        Long seckillId = (Long) redisTemplate.boundListOps("seckill_goods_queue_" + seckillGoodsId).rightPop();
        if (seckillId==null) {
            throw new RuntimeException("很遗憾，商品售罄");
        }

        //1、seckillGoodsId获取秒杀商品信息  10    100请求
        TbSeckillGoods seckillGoods = (TbSeckillGoods) redisTemplate.boundHashOps("seckill_goods").get(seckillGoodsId);

        //当排队人数大于剩余库存数+20时，如果还有多余排队人数，剩余的人员提醒排队人数过多  5  25 50
        if(increment>(seckillGoods.getStockCount()+20)){
            throw new RuntimeException("排队人数过多，你没有希望抢到商品了");
        }


        Map<String,Object> orderMap = new HashMap<>();
        orderMap.put("userId",userId);
        orderMap.put("seckillGoodsId",seckillGoodsId);

        //将秒杀下单的操作，作为线程队列的任务存放  map :key:value
        redisTemplate.boundListOps("seckill_order_queue").leftPush(orderMap);

        //基于线程池执行对象，调用保存订单的线程对象，完成订单保存
        executor.execute(createOrder);

    }
}
