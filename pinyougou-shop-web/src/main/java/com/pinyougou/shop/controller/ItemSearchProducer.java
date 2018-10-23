package com.pinyougou.shop.controller;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.jms.Destination;

/**
 * @author:Jack
 * @description:
 * @Date:created in 2018/10/23,10:23
 */
@Component
public class ItemSearchProducer {

    @Resource
    private JmsTemplate jmsTemplate;
    @Resource
    private Destination queueSolrDestination;

    public void send(String message){
        jmsTemplate.send(queueSolrDestination,session -> session.createTextMessage(message));
    }
}
