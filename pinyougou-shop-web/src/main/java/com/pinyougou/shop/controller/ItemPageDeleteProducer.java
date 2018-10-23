package com.pinyougou.shop.controller;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.jms.Destination;

/**
 * @author:Jack
 * @description:
 * @Date:created in 2018/10/23,14:17
 */

@Component
public class ItemPageDeleteProducer {

    @Resource
    private Destination topicPageDeleteDestination;
    @Resource
    private JmsTemplate jmsTemplate;

    public void send(Long[] ids){
        jmsTemplate.send(topicPageDeleteDestination, session -> session.createObjectMessage(ids));
    }
}
