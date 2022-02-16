package com.huazan.pojo;

import lombok.Data;

/**
 * 登录结果信息
 */
@Data
public class LoginResultInfo {

    /**
     * 用户token
     */
    private String token;

    /**
     * 过期时间
     */
    private Long expiredTime;

    /**
     * 用户id
     */
    private String userId;
}
