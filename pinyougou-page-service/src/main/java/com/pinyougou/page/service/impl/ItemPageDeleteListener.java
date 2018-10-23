package com.pinyougou.page.service.impl;

import com.pinyougou.page.service.ItemPageService;

import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

/**
 * @author:Jack
 * @description:
 * @Date:created in 2018/10/23,14:24
 */

public class ItemPageDeleteListener implements MessageListener {

    @Resource
    private ItemPageService itemPageService;

    @Override
    public void onMessage(Message message) {
        ObjectMessage objectMessage = (ObjectMessage) message;
        try {
            Long[] ids = (Long[]) objectMessage.getObject();
            System.out.println("接收消息:删除页面");
            itemPageService.deleteItemHtml(ids);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
