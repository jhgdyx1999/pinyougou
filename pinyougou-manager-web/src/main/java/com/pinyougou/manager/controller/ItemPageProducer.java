package com.pinyougou.manager.controller;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.jms.Destination;

/**
 * @author:Jack
 * @description:
 * @Date:created in 2018/10/23,13:28
 */

@Component
public class ItemPageProducer {

    @Resource
    private Destination topicPageDestination;
    @Resource
    private JmsTemplate jmsTemplate;

    public void  send(String id){
        jmsTemplate.send(topicPageDestination, session -> session.createTextMessage(id));
    }
}
