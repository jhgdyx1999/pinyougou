package com.pinyougou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;

import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 * @author:Jack
 * @description:
 * @Date:created in 2018/10/23,10:34
 */
public class ItemSearchListener implements MessageListener {

    @Resource
    private ItemSearchService itemSearchService;

    @Override
    public void onMessage(Message message) {
        TextMessage textMessage = (TextMessage) message;
        try {
            System.out.println("监听到消息,更新检索服务器");
            itemSearchService.updateItems(JSON.parseArray(textMessage.getText(), TbItem.class));
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }
}
