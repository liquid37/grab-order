package com.huazan.pojo;

import lombok.Data;

/**
 * 订单信息
 */
@Data
public class OrderInfo {

    /**
     * id值
     */
    private String id;

    /**
     * 票据号
     */
    private String orderNo;

    /**
     * 承兑人
     */
    private String acceptor;

    /**
     * 金额
     */
    private String amount;



}
