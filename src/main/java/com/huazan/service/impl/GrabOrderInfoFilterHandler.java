package com.huazan.service.impl;

import com.huazan.pojo.GrabOrderContent;
import com.huazan.service.IGrabOrderHandler;
import com.huazan.vo.GrabOrderInfoVO;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service("filterHandler")
@Order(20)
public class GrabOrderInfoFilterHandler implements IGrabOrderHandler {

    static ExpiringMap<String, Byte> map = ExpiringMap.builder()
            .variableExpiration().expirationPolicy(ExpirationPolicy.CREATED).build();

    @Override
    public boolean handler(GrabOrderContent grabOrderContent) {
        List<GrabOrderInfoVO> orderInfoVOS = grabOrderContent.getOrderInfoVOS();
        List matchDataList = grabOrderContent.getMatchDataList();
        List<GrabOrderInfoVO> afterFilterOrders = orderInfoVOS.stream()
                .filter(o -> !map.containsKey(o.getId()))
                .filter(o1 -> o1.matchData(matchDataList))
                .collect(Collectors.toList());
        if(afterFilterOrders.size() == 0){
            return false;
        }
        afterFilterOrders.forEach(o -> map.put(o.getId(),(byte)1,60*60*24,TimeUnit.SECONDS));
        grabOrderContent.setOrderInfoVOS(afterFilterOrders);
        return true;
    }

}
