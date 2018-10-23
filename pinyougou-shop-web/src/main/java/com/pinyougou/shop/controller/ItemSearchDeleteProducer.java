package com.pinyougou.shop.controller;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.jms.Destination;

/**
 * @author:Jack
 * @description:
 * @Date:created in 2018/10/23,11:13
 */
@Component
public class ItemSearchDeleteProducer {

    @Resource
    private JmsTemplate jmsTemplate;
    @Resource
    private Destination queueSolrDeleteDestination;

    public void send(Long[] ids){
        jmsTemplate.send(queueSolrDeleteDestination, session -> session.createObjectMessage(ids));
    }
}
