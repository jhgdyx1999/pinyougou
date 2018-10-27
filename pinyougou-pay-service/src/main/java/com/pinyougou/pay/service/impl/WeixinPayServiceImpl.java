package com.pinyougou.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.pinyougou.common.util.HttpClient;
import com.pinyougou.pay.service.WeixinPayService;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

/**
 * @author:Jack
 * @description:
 * @Date:created in 2018/10/27,10:47
 */
@Service
public class WeixinPayServiceImpl implements WeixinPayService {

    @Value("${appid}")
    private String appid;
    @Value("${partner}")
    private String mch_id;
    @Value("${notifyurl}")
    private String notifyurl;
    @Value("${partnerkey}")
    private String partnerkey;

    /**
     * 生成支付二维码
     *
     * @param out_trade_no
     * @param total_fee
     * @return
     */
    @Override
    //https://api.mch.weixin.qq.com/pay/unifiedorder
    public Map<String, String> generateNative(String out_trade_no, String total_fee) {
        //封装参数
        Map<String, String> map = new HashMap<>();
        map.put("appid", appid);//公众账号ID
        map.put("mch_id", mch_id);//商户号
        map.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串
        map.put("body", "pinyougou");//商品描述
        map.put("out_trade_no", out_trade_no);//商户订单号
        map.put("total_fee", total_fee);//标价金额
        map.put("spbill_create_ip", "127.0.0.1");//终端ip
        map.put("notify_url", notifyurl);//通知地址
        map.put("trade_type", "NATIVE");//交易类型
        try {
            //发送请求
            String signedXml = WXPayUtil.generateSignedXml(map, partnerkey);
            System.out.println(signedXml);
            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            client.setHttps(true);
            client.setXmlParam(signedXml);
            client.post();
            String xmlResponse = client.getContent();
            //获取返回结果
            Map<String, String> mapResponse = WXPayUtil.xmlToMap(xmlResponse);
            //封装返回结果
            Map<String, String> mapToFore = new HashMap<>();
            //            mapToFore.put("code_url", mapResponse.get("code_url"));
            mapToFore.put("code_url", "weixin://wxpay/bizpayurl?pr=U4mxZss.");
            mapToFore.put("out_trade_no", out_trade_no);
            mapToFore.put("total_fee", total_fee);

            return mapToFore;

        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    @Override
    public Map<String, String> queryPayStatus(String out_trade_no) {
        Map<String, String> map = new HashMap<>();
        map.put("appid", appid);//公众账号ID
        map.put("mch_id", mch_id);//商户号
        map.put("out_trade_no", out_trade_no);//商户订单号
        map.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串

        try {
            String signedXml = WXPayUtil.generateSignedXml(map, partnerkey);
            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
            client.setHttps(true);
            client.setXmlParam(signedXml);
            client.post();
            String xmlResponse = client.getContent();

            //获取返回结果
            return WXPayUtil.xmlToMap(xmlResponse);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
