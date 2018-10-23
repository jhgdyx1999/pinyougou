package com.pinyougou.search.service.impl;

import com.pinyougou.search.service.ItemSearchService;

import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

/**
 * @author:Jack
 * @description:
 * @Date:created in 2018/10/23,11:18
 */
public class ItemSearchDeleteListener implements MessageListener {

    @Resource
    private ItemSearchService itemSearchService;

    @Override
    public void onMessage(Message message) {
        ObjectMessage objectMessage = (ObjectMessage) message;
        try {
            System.out.println("监听到消息:执行删除...");
            itemSearchService.deleteByGoodsIds((Long[]) objectMessage.getObject());
        } catch (JMSException e) {
            e.printStackTrace();
        }


    }
}
