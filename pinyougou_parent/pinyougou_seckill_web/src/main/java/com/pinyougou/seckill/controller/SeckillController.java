package com.pinyougou.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.seckill.service.SeckillService;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/seckill")
public class SeckillController {

    @Reference
    private SeckillService seckillService;

    /**
     * 查询秒杀商品列表
     */
    @RequestMapping("/findSeckillGoodsList")
    public List<TbSeckillGoods> findSeckillGoodsList(){
        return seckillService.findSeckillGoodsList();
    }

    /**
     * 基于秒杀商品id查询秒杀商品
     */
    @RequestMapping("/findOne")
    public TbSeckillGoods findOne(Long seckillGoodsId){
        return seckillService.findOne(seckillGoodsId);
    }

    /**
     * 秒杀下单功能
     */
    @RequestMapping("/saveOrder")
    public Result saveOrder(Long seckillGoodsId){
        try {
            //获取下单用户
            String userId = SecurityContextHolder.getContext().getAuthentication().getName();

            if("anonymousUser".equals(userId)){
                //用户未登录，先去登录
                return new Result(false,"抢购前，请先登录");
            }
            seckillService.saveOrder(seckillGoodsId,userId);
            return new Result(true,"下单成功");
        }catch (RuntimeException e) {
            e.printStackTrace();
            return new Result(false,e.getMessage());
        }catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"服务器异常，下单抢购失败");
        }
    }
}
