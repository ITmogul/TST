package com.pinyougou.seckill.task;

import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillGoodsExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class SeckillTask {

    @Autowired
    private TbSeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 同步满足条件的秒杀商品到redis
     *   审核通过
         有库存
         当前时间大于开始时间,并小于秒杀结束时间
     */
    @Scheduled(cron = "0/10 * * * * ?")//开启定时任务
    public void synchronizeSeckillGoodsToRedis(){
        //查询数据库秒杀商品

        TbSeckillGoodsExample example = new TbSeckillGoodsExample();
        example.createCriteria().andStatusEqualTo("1").
                    andStockCountGreaterThan(0).
                    andStartTimeLessThanOrEqualTo(new Date()).andEndTimeGreaterThanOrEqualTo(new Date());
        List<TbSeckillGoods> seckillGoodsList = seckillGoodsMapper.selectByExample(example);
        //基于redisTemplate同步redis中
        for (TbSeckillGoods seckillGoods : seckillGoodsList) {
            //方便基于秒杀商品id获取秒杀商品对象用于详情页展示
            redisTemplate.boundHashOps("seckill_goods").put(seckillGoods.getId(),seckillGoods);

            //基于redis队列完成记录秒杀商品剩余库存量  list   id为1的商品剩余5件  [1,1,1,1,1]
            Integer stockCount = seckillGoods.getStockCount();
            for(int i=0;i<stockCount;i++){
                redisTemplate.boundListOps("seckill_goods_queue_"+seckillGoods.getId()).leftPush(seckillGoods.getId());
            }

        }

        System.out.println("synchronizeSeckillGoodsToRedis finish......");
    }
}
