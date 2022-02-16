package com.huazan.vo;

import com.huazan.pojo.base.BaseMatchData;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Data
public class GrabOrderInfoVO {

    /**
     * id
     */
    private String id;

    /**
     * 承兑人
     */
    private String acceptor;

    /**
     * 利率
     */
    private BigDecimal rate;

    /**
     * 期限
     */
    private Integer limitDays;

    /**
     * 发布时间
     */
    private String publishTime;

    /**
     * 金额
     */
    private String amount;

    /**
     * 通知群
     */
    private String notifyGroup;

    public boolean matchData(List matchDataList){

        for(Object data : matchDataList){
            if(data instanceof BaseMatchData){
                BaseMatchData matchData = (BaseMatchData)data;
                if(matchData.match(this)){
                    this.setNotifyGroup(matchData.getNotifyGroup());
                    return true;
                }
            }
        }
        return false;
    }

}
