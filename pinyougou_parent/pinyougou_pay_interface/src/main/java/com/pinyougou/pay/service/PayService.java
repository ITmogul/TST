package com.pinyougou.pay.service;

import com.pinyougou.pojo.TbPayLog;

import java.util.Map;

public interface PayService {

    /**
     * 获取支付超链接，生成二维码
     */
    public Map<String,Object> createNative(String out_trade_no,String total_fee) throws Exception;

    /**
     * 查询支付状态
     */
    public Map<String,String> queryPayStatus(String out_trade_no) throws Exception;

    TbPayLog getPayLogFormRedis(String userId);

    void updatePayStatus(String out_trade_no, String transaction_id);
}
