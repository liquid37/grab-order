package com.huazan.pojo.samecity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.huazan.pojo.base.BaseMatchData;
import com.huazan.vo.GrabOrderInfoVO;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SameCityMatchData extends BaseMatchData {

    @Excel(name = "利率大于")
    private BigDecimal rate;

    @Excel(name = "期限（天）")
    private String limitDateStr;

    @Override
    public boolean match(GrabOrderInfoVO orderInfo) {
        return false;
    }
}
