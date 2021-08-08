package com.huazan.pojo.deep;

import com.huazan.pojo.base.BaseMatchParam;
import lombok.Data;

@Data
public class DeepMatchParam extends BaseMatchParam {

    private Integer maxAmount;

    private Integer minAmount;
}
