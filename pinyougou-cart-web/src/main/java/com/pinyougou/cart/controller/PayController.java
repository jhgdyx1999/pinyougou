package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.common.util.IdWorker;
import com.pinyougou.entity.Result;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.pojo.TbPayLog;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    private OrderService orderService;

    @RequestMapping("/generateNative")
    public Map<String, String> generateNative() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        TbPayLog payLog = orderService.getPayLogFromRedis(username);
        if (payLog != null) {
            return weixinPayService.generateNative(payLog.getOutTradeNo() + "", payLog.getTotalFee() + "");
        }
        return new HashMap<>();
    }

    @RequestMapping("/queryPayStatus")
    public Result queryPayStatus(String out_trade_no) {
        Result result;
        int count = 0;

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
                orderService.updateOrderStatus(out_trade_no, payStatus.get("transaction_id"));
                break;
            }
            try {
                Thread.sleep(2000);
                count++;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (count > 300) {
                result = new Result(true, "二维码超时");
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
