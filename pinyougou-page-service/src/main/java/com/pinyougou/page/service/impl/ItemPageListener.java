package com.pinyougou.page.service.impl;

import com.pinyougou.page.service.ItemPageService;

import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 * @author:Jack
 * @description:
 * @Date:created in 2018/10/23,13:35
 */
public class ItemPageListener implements MessageListener {

    @Resource
    private ItemPageService itemPageService;

    @Override
    public void onMessage(Message message) {
        TextMessage textMessage = (TextMessage) message;
        try {
            itemPageService.generateItemHtml( Long.parseLong(textMessage.getText()) );
            System.out.println("监听到消息,执行生成页面操作");
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
