package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pay.service.PayService;
import com.pinyougou.pojo.TbPayLog;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/pay")
public class PayController {

    @Reference
    private PayService payService;

    /**
     * 获取支付超链接，生成二维码
     */
    @RequestMapping("/createNative")
    public Map<String,Object> createNative(){
        try {
            //获取支付用户
            String userId = SecurityContextHolder.getContext().getAuthentication().getName();
            //从缓存中获取支付日志
            TbPayLog payLog = payService.getPayLogFormRedis(userId);
            return payService.createNative(payLog.getOutTradeNo(),payLog.getTotalFee()+"");
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();//返回空集合 {}
        }
    }

    /**
     * 查询支付状态
     */
    @RequestMapping("/queryPayStatus")
    public Result queryPayStatus(String out_trade_no){

        try {
            int count=1;
            //持续查询支付状态
            while (true){
                //每隔三秒调用一次
                Thread.sleep(3000);

                //超过5分钟，没有支付，提示支付超时
                count++;
                if(count>100){
                    return new Result(false,"timeout");
                }

                Map<String, String> resultMap = payService.queryPayStatus(out_trade_no);
                //获取交易状态
                String trade_state = resultMap.get("trade_state");
                if ("SUCCESS".equals(trade_state)) {//支付成功
                    //支付成功后，更新订单、支付日志 支付状态、支付时间等信息
                    String transaction_id = resultMap.get("transaction_id");
                    payService.updatePayStatus(out_trade_no,transaction_id);

                    return new Result(true,"支付成功");
                }

            }


        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"微信支付失败");
        }
    }
}
