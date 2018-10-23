package com.itheima.commons.sms;

import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author:Jack
 * @description:
 * @Date:created in 2018/10/23,20:28
 */
@RestController
public class SmsController {

    @Resource
    private JmsMessagingTemplate jmsMessagingTemplate;

    @RequestMapping("/send")
    public void sendMsg(){
        Map<String,String> map = new HashMap<>();
        map.put("phoneNumbers", "15062269794");
        map.put("signName", "signName");
        map.put("templateCode", "templateCode");
        map.put("templateParam", "templateParam");
        jmsMessagingTemplate.convertAndSend("sms-service",map );
    }
}
