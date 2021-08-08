package com.huazan.qo;

import lombok.Data;

@Data
public class DeepOrderQueryParam {

    /**
     * 状态
     */
    private Integer draftStatus = 7;

    /**
     * 当前页
     */
    private Integer current = 1;

    /**
     * 每页数量
     */
    private Integer size = 2;

    /**
     * 开始时间
     */
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;

    /**
     * 是否历史记录
     */
    private Integer history = 0;

}
