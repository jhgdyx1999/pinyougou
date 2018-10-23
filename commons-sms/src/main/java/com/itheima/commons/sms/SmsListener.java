package com.itheima.commons.sms;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author:Jack
 * @description:
 * @Date:created in 2018/10/23,20:21
 */
@Component
public class SmsListener {

    @Resource
    private SmsUtil smsUtil;

    @JmsListener(destination = "sms-service")
    public void send(Map<String, String> map) {
        try {
            System.out.println("发送验证短信...");
            System.out.println(map);
//            SendSmsResponse sendSmsResponse = smsUtil.sendSms(map.get("phoneNumbers"), map.get("signName"), map.get("templateCode"), map.get("templateParam"));
//            System.out.println(sendSmsResponse.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
