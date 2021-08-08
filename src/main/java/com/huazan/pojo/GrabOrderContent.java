package com.huazan.pojo;

import com.huazan.pojo.base.BaseMatchData;
import com.huazan.vo.GrabOrderInfoVO;
import lombok.Data;

import java.util.List;

@Data
public class GrabOrderContent<D extends BaseMatchData> {

    private List<GrabOrderInfoVO> orderInfoVOS;

    private List<D> matchDataList;

    private String systemName;

}
