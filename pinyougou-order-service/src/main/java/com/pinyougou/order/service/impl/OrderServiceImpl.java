package com.pinyougou.order.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.common.util.IdWorker;
import com.pinyougou.compositeEntity.Cart;
import com.pinyougou.entity.PageResult;
import com.pinyougou.mapper.TbOrderItemMapper;
import com.pinyougou.mapper.TbPayLogMapper;
import com.pinyougou.order.service.OrderService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbOrderMapper;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbOrderExample;
import com.pinyougou.pojo.TbOrderExample.Criteria;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojo.TbPayLog;
import org.apache.commons.collections.OrderedMap;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    @Resource
    private TbOrderMapper orderMapper;
    @Resource
    private RedisTemplate<String,Object> redisTemplate;
    @Resource
    private IdWorker idWorker;
    @Resource
    private TbOrderItemMapper orderItemMapper;
    @Resource
    private TbPayLogMapper payLogMapper;

    /**
     * 查询全部
     */
    @Override
    public List<TbOrder> findAll() {
        return orderMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbOrder> page = (Page<TbOrder>) orderMapper.selectByExample(null);
        return new PageResult<>(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Override
    public void add(TbOrder order) {
//        String sellerId = order.getSellerId();
        String userId = order.getUserId();
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(userId);
        List<Long> orderIds = new ArrayList<>();
        long totalFee = 0L;
        for (Cart cart : cartList){
            TbOrder tbOrder = new TbOrder();
            long orderId = idWorker.nextId();
            tbOrder.setOrderId(orderId);//订单id
            tbOrder.setPaymentType(order.getPaymentType());//付款类型
            tbOrder.setStatus("1");//订单状态("1"为未付款)
            tbOrder.setCreateTime(new Date());//订单生成时间
            tbOrder.setUpdateTime(new Date());//订单跟新时间
            tbOrder.setUserId(userId);//用户id
            tbOrder.setReceiverAreaName(order.getReceiverAreaName());//收货人地址
            tbOrder.setReceiverMobile(order.getReceiverMobile());//收货人电话
//            tbOrder.setReceiverZipCode(order.getReceiverZipCode());//收货人邮编
            tbOrder.setReceiver(order.getReceiver());//收货人姓名
            tbOrder.setSourceType(order.getSourceType());//订单来源类型
            tbOrder.setSellerId(cart.getSellerId());//商家id

            List<TbOrderItem> orderItemList = cart.getOrderItemList();

            BigDecimal payment = new BigDecimal(0.00);

            for (TbOrderItem orderItem : orderItemList){
                orderItem.setId(idWorker.nextId());
                orderItem.setOrderId(orderId);
                //循环获取总金额
                payment = payment.add(orderItem.getTotalFee());
                orderItemMapper.insert(orderItem) ;
            }
            tbOrder.setPayment(payment);//实付金额
            totalFee += payment.multiply(new BigDecimal(100)).longValue();
            orderMapper.insert(tbOrder);

            orderIds.add(orderId);
        }
        //清除redis数据
        redisTemplate.boundHashOps("cartList").delete(userId);
        //生成支付日志
        if ("1".equals(order.getPaymentType())){
            TbPayLog payLog = new TbPayLog();
            payLog.setOutTradeNo(idWorker.nextId()+"");
            payLog.setCreateTime(new Date());
            payLog.setTotalFee(totalFee);
            payLog.setUserId(userId);
            payLog.setTradeState("0");
            payLog.setOrderList(orderIds.toString().replace("[", "").replace("]", ""));
            payLog.setPayType("1");

            payLogMapper.insert(payLog);
            redisTemplate.boundHashOps("payLog").put(userId, payLog);
        }
    }

    /**
     * 修改
     */
    @Override
    public void update(TbOrder order) {
        orderMapper.updateByPrimaryKey(order);
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public TbOrder findOne(Long id) {
        return orderMapper.selectByPrimaryKey(id);
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            orderMapper.deleteByPrimaryKey(id);
        }
    }


    @Override
    public PageResult findPage(TbOrder order, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbOrderExample example = new TbOrderExample();
        Criteria criteria = example.createCriteria();

        if (order != null) {
            if (order.getPaymentType() != null && order.getPaymentType().length() > 0) {
                criteria.andPaymentTypeLike("%" + order.getPaymentType() + "%");
            }
            if (order.getPostFee() != null && order.getPostFee().length() > 0) {
                criteria.andPostFeeLike("%" + order.getPostFee() + "%");
            }
            if (order.getStatus() != null && order.getStatus().length() > 0) {
                criteria.andStatusLike("%" + order.getStatus() + "%");
            }
            if (order.getShippingName() != null && order.getShippingName().length() > 0) {
                criteria.andShippingNameLike("%" + order.getShippingName() + "%");
            }
            if (order.getShippingCode() != null && order.getShippingCode().length() > 0) {
                criteria.andShippingCodeLike("%" + order.getShippingCode() + "%");
            }
            if (order.getUserId() != null && order.getUserId().length() > 0) {
                criteria.andUserIdLike("%" + order.getUserId() + "%");
            }
            if (order.getBuyerMessage() != null && order.getBuyerMessage().length() > 0) {
                criteria.andBuyerMessageLike("%" + order.getBuyerMessage() + "%");
            }
            if (order.getBuyerNick() != null && order.getBuyerNick().length() > 0) {
                criteria.andBuyerNickLike("%" + order.getBuyerNick() + "%");
            }
            if (order.getBuyerRate() != null && order.getBuyerRate().length() > 0) {
                criteria.andBuyerRateLike("%" + order.getBuyerRate() + "%");
            }
            if (order.getReceiverAreaName() != null && order.getReceiverAreaName().length() > 0) {
                criteria.andReceiverAreaNameLike("%" + order.getReceiverAreaName() + "%");
            }
            if (order.getReceiverMobile() != null && order.getReceiverMobile().length() > 0) {
                criteria.andReceiverMobileLike("%" + order.getReceiverMobile() + "%");
            }
            if (order.getReceiverZipCode() != null && order.getReceiverZipCode().length() > 0) {
                criteria.andReceiverZipCodeLike("%" + order.getReceiverZipCode() + "%");
            }
            if (order.getReceiver() != null && order.getReceiver().length() > 0) {
                criteria.andReceiverLike("%" + order.getReceiver() + "%");
            }
            if (order.getInvoiceType() != null && order.getInvoiceType().length() > 0) {
                criteria.andInvoiceTypeLike("%" + order.getInvoiceType() + "%");
            }
            if (order.getSourceType() != null && order.getSourceType().length() > 0) {
                criteria.andSourceTypeLike("%" + order.getSourceType() + "%");
            }
            if (order.getSellerId() != null && order.getSellerId().length() > 0) {
                criteria.andSellerIdLike("%" + order.getSellerId() + "%");
            }

        }

        Page<TbOrder> page = (Page<TbOrder>) orderMapper.selectByExample(example);
        return new PageResult<>(page.getTotal(), page.getResult());
    }

    @Override
    public TbPayLog getPayLogFromRedis(String userId) {
        return (TbPayLog) redisTemplate.boundHashOps("payLog").get(userId);
    }

    @Override
    public void updateOrderStatus(String out_trade_no, String transaction_id) {
        TbPayLog payLog = payLogMapper.selectByPrimaryKey(out_trade_no);
        payLog.setTradeState("1");//订单已支付
        payLog.setPayTime(new Date());
        payLog.setTransactionId(transaction_id);
        payLogMapper.updateByPrimaryKey(payLog);

        //更新订单状态
        String[] orderIds = payLog.getOrderList().split(",");
        for (String orderId : orderIds){
            TbOrder order = orderMapper.selectByPrimaryKey(Long.valueOf(orderId));
            order.setUpdateTime(new Date());
            order.setStatus("2");//已付款
            orderMapper.updateByPrimaryKey(order);
        }

        //清除redis内的支付日志
        redisTemplate.boundHashOps("payLog").delete(payLog.getUserId());


    }

}
