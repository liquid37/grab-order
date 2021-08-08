package com.huazan.pojo.base;

import lombok.Data;

import java.util.List;

@Data
public class BaseMatchRule<P extends BaseMatchParam, D extends BaseMatchData> {

    public P ruleParam;

    private List<D> matchDataList;
}
