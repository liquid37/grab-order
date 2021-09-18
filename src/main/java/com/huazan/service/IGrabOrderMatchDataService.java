package com.huazan.service;

import com.huazan.pojo.base.BaseMatchRule;

import java.io.IOException;
import java.util.List;

public interface IGrabOrderMatchDataService<T extends BaseMatchRule> {

    List<T> getMatchData() throws IOException;
}
