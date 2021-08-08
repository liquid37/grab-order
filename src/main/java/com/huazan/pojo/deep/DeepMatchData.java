package com.huazan.pojo.deep;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.huazan.pojo.base.BaseMatchData;
import com.huazan.vo.GrabOrderInfoVO;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DeepMatchData extends BaseMatchData {

    @Excel(name = "利率大于")
    private BigDecimal rate;

    @Excel(name = "期限（天）")
    private Integer limitDate;

    @Override
    public boolean match(GrabOrderInfoVO orderInfo) {
        if(orderInfo.getAcceptor().contains(getAcceptor())  // 承兑人
                && orderInfo.getLimitDays().intValue()>= limitDate // 期限以上
                && (orderInfo.getRate().compareTo(rate.multiply(new BigDecimal("100"))) >= 0)) // 利率以上
        {
            return true;
        }
        return false;
    }
}
