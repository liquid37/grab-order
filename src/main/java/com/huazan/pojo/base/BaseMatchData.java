package com.huazan.pojo.base;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.huazan.vo.GrabOrderInfoVO;
import lombok.Data;

@Data
public abstract class BaseMatchData {

    /**
     * 承兑行
     */
    @Excel(name = "承兑行")
    private String acceptor;

    public abstract boolean match(GrabOrderInfoVO orderInfo);
}
