package com.huazan.pojo.samecity;

import com.huazan.pojo.base.BaseMatchParam;
import lombok.Data;

@Data
public class SameCityMatchParam extends BaseMatchParam {

    private Integer maxAmount;

    private Integer minAmount;
}
