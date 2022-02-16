package com.huazan.vo;

import lombok.Data;

@Data
public class NotifyVO {

    /**
     * 通知内容
     */
    private String content;

    /**
     * 钉群accessToken
     */
    private String accessToken;

    /**
     * 钉群secret
     */
    private String secret;
}
