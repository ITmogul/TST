package com.pinyougou.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.pinyougou.mapper.TbOrderMapper;
import com.pinyougou.mapper.TbPayLogMapper;
import com.pinyougou.pay.service.PayService;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbPayLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import utils.HttpClient;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
@Service
@Transactional
public class PayServiceImpl implements PayService {

    @Value("${appid}")
    private String appid;//公众号id
    @Value("${partner}")
    private String partner;//商户号id
    @Value("${partnerkey}")
    private String partnerkey;//商户秘钥
    @Value("${notifyurl}")
    private String notifyurl;//回调地址


    @Override
    public Map<String, Object> createNative(String out_trade_no, String total_fee) throws Exception {
        // 1、组装微信统一下单API所需要的必填参数
        Map<String,String> paramMap = new HashMap<>();
        paramMap.put("appid",appid);
        paramMap.put("mch_id",partner);
        paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
        paramMap.put("body","品优购");
        paramMap.put("out_trade_no",out_trade_no);
        paramMap.put("total_fee",total_fee);
        paramMap.put("spbill_create_ip","127.0.0.1");
        paramMap.put("notify_url",notifyurl);
        paramMap.put("trade_type","NATIVE");
        paramMap.put("product_id","1");
        //将参数转xml
        String paramXml = WXPayUtil.generateSignedXml(paramMap, partnerkey);
        System.out.println(paramXml);
        //2、基于httpclient工具类，调用微信支付平台，完成支付操作
        HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
        httpClient.setHttps(true);
        httpClient.setXmlParam(paramXml);
        httpClient.post();
        //3、处理响应结果
        String resultXml = httpClient.getContent();
        Map<String, String> resultMap = WXPayUtil.xmlToMap(resultXml);

        Map<String, Object> map = new HashMap<>();
        map.put("code_url",resultMap.get("code_url"));
        map.put("out_trade_no",out_trade_no);
        map.put("total_fee",total_fee);
        return map;
    }

    @Override
    public Map<String, String> queryPayStatus(String out_trade_no) throws Exception {
        // 1、组装微信查询支付状态所需要的必填参数
        Map<String,String> paramMap = new HashMap<>();
        paramMap.put("appid",appid);
        paramMap.put("mch_id",partner);
        paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
        paramMap.put("out_trade_no",out_trade_no);
        //将参数转xml
        String paramXml = WXPayUtil.generateSignedXml(paramMap, partnerkey);
        //2、基于httpclient工具类，调用微信支付平台，完成支付状态操作操作
        HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
        httpClient.setHttps(true);
        httpClient.setXmlParam(paramXml);
        httpClient.post();
        //3、处理响应结果
        String resultXml = httpClient.getContent();
        System.out.println("支付结果："+resultXml);
        Map<String, String> resultMap = WXPayUtil.xmlToMap(resultXml);

        return resultMap;
    }

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public TbPayLog getPayLogFormRedis(String userId) {
        return (TbPayLog) redisTemplate.boundHashOps("payLog").get(userId);
    }

    @Autowired
    private TbPayLogMapper payLogMapper;

    @Autowired
    private TbOrderMapper orderMapper;

    @Override
    public void updatePayStatus(String out_trade_no, String transaction_id) {
        //支付成功后，更新订单、支付日志 支付状态、支付时间等信息
        TbPayLog payLog = payLogMapper.selectByPrimaryKey(out_trade_no);
        payLog.setTradeState("2");//已支付
        payLog.setPayTime(new Date());
        payLog.setTransactionId(transaction_id);
        payLogMapper.updateByPrimaryKey(payLog);

        //更新订单状态
        String orderList = payLog.getOrderList();
        String[] orderIds = orderList.split(",");
        for (String orderId : orderIds) {
            TbOrder tbOrder = orderMapper.selectByPrimaryKey(Long.parseLong(orderId));
            tbOrder.setStatus("2");//已支付
            tbOrder.setPaymentTime(new Date());
            orderMapper.updateByPrimaryKey(tbOrder);
        }
        //清空缓存中的支付日志
        redisTemplate.boundHashOps("payLog").delete(payLog.getUserId());
    }
}
