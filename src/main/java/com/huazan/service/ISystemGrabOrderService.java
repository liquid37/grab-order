package com.huazan.service;

import com.huazan.pojo.LoginResultInfo;
import com.huazan.pojo.OrderInfo;

import java.util.List;

/**
 * 系统抢单service接口
 */
public interface ISystemGrabOrderService {

    /**
     * 抢单
     * @return
     */
    void grabOrder() throws Exception;

}
