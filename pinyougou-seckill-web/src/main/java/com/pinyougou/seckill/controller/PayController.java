package com.pinyougou.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.entity.Result;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.service.SeckillOrderService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @author:Jack
 * @description:
 * @Date:created in 2018/10/27,11:20
 */
@RestController
@RequestMapping("/pay")
public class PayController {

    @Reference(timeout = 5000)
    private WeixinPayService weixinPayService;
    @Reference(timeout = 5000)
    private SeckillOrderService seckillOrderService;

    @RequestMapping("/generateNative")
    public Map<String, String> generateNative() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        TbSeckillOrder seckillOrder = seckillOrderService.selectOrderFromRedis(username);
        if (seckillOrder != null) {
            return weixinPayService.generateNative(seckillOrder.getId() + "", seckillOrder.getMoney().multiply(new BigDecimal(100)).longValue() + "");
        }
        return new HashMap<>();
    }

    @RequestMapping("/queryPayStatus")
    public Result queryPayStatus(String out_trade_no) {
        Result result;
        int count = 0;
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        while (true) {
            Map<String, String> payStatus = weixinPayService.queryPayStatus(out_trade_no);
            payStatus = new HashMap<>();
            payStatus.put("trade_state", "SUCCESS");
            payStatus.put("transaction_id", "123456789");
            if (payStatus == null) {
                result = new Result(false, "支付异常");
                break;
            }
            if ("SUCCESS".equals(payStatus.get("trade_state"))) {
                result = new Result(true, "支付成功");
                seckillOrderService.saveOrderFromRedisToDb(username, Long.valueOf(out_trade_no), payStatus.get("transaction_id"));
                break;
            }
            try {
                Thread.sleep(2000);
                count++;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (count > 300) {
                Map<String, String> payResult = weixinPayService.closePay(out_trade_no);
                if ("FAIL".equals(payResult.get("return_code")) && "ORDERPAID".equals(payResult.get("err_code"))){
                    result = new Result(true, "支付成功");
                    seckillOrderService.saveOrderFromRedisToDb(username, Long.valueOf(out_trade_no), payStatus.get("transaction_id"));
                }else{
                    seckillOrderService.deleteTimeoutOrderFromRedis(username);
                    result = new Result(false, "二维码超时");
                }
                break;
            }
        }
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }
}
