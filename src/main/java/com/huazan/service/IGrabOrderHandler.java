package com.huazan.service;

import com.huazan.pojo.GrabOrderContent;

public interface IGrabOrderHandler {

    /**
     * 处理订单
     * @param grabOrderContent
     */
    boolean handler(GrabOrderContent grabOrderContent);
}
