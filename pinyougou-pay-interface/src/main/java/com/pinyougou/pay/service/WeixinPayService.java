package com.pinyougou.pay.service;

import java.util.Map;

/**
 * @author:Jack
 * @description:
 * @Date:created in 2018/10/27,10:45
 */
public interface WeixinPayService {

    public Map<String,String> generateNative(String out_trade_no,String total_fee);

    public Map<String,String> queryPayStatus(String out_trade_no);

    public Map<String,String> closePay(String out_trade_no);

}
