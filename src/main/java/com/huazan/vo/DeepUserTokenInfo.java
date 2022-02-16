package com.huazan.vo;

import lombok.Data;

@Data
public class DeepUserTokenInfo{
    // 登录成功token
    private String token;
    // 公司名
    private String corpName;

    private String userId;
}
