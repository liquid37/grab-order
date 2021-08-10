package com.huazan.pojo.samecity;

import com.sun.scenario.effect.impl.prism.PrImage;
import lombok.Data;

/**
 * 同城系统订单查询QO
 */
@Data
public class SameCityQO {

    private String version = "3.5";

    private String source = "HTML";

    private String channel = "01";

    private Integer pageNum = 1;

    private Integer pageSize = 50;

    private Integer orderStatus = 1;

    private Integer orderStatusDataFewDays = 1;

    private boolean isCollected = false;

    private boolean isSpj = false;

    private boolean isCurUserPublish = false;

    private boolean fastTrade = false;

    private boolean hideDistrictLimit = true;

    /**
     * 最小金额
     */
    private String priceSp;

    /**
     * 最大金额
     */
    private String priceEp;

    private String bankName;
}
