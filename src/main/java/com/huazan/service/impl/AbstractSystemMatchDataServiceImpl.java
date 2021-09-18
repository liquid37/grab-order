package com.huazan.service.impl;

import com.huazan.pojo.base.BaseMatchRule;
import com.huazan.service.IGrabOrderMatchDataService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractSystemMatchDataServiceImpl<R extends BaseMatchRule> implements IGrabOrderMatchDataService {

    protected List<R> matchDatas = new ArrayList<>();

    abstract void loadMatchData() throws IOException;

    @Override
    public List<R> getMatchData() throws IOException {
        loadMatchData();
        return matchDatas;
    }

}
