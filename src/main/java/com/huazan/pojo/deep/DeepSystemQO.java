package com.huazan.pojo.deep;

import lombok.Data;

@Data
public class DeepSystemQO {

    private Integer size = 100;

    private Integer current = 1;

    private Integer todayFlag = 1;

    private Integer channel = 1;

    private String dStatus = "1";

    private String platform = "PC";

    private String keyWords;

    private Integer dAmtEnd;

    private Integer dAmtStart;
}
