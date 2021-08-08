package com.huazan.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiGettokenRequest;
import com.dingtalk.api.request.OapiUserGetByMobileRequest;
import com.dingtalk.api.response.OapiGettokenResponse;
import com.dingtalk.api.response.OapiUserGetByMobileResponse;
import com.google.common.collect.Maps;
import com.huazan.constants.SystemConstant;
import com.huazan.pojo.GrabOrderContent;
import com.huazan.service.IGrabOrderHandler;
import com.huazan.utils.CommonPropertiesUtil;
import com.huazan.vo.GrabOrderInfoVO;
import com.taobao.api.ApiException;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service("notifyHandler")
public class GrabOrderInfoNotifyHandler implements IGrabOrderHandler {

    private static final String NEWLINE = "\n";
    private String notify_template =
            "对接系统：{5}" + NEWLINE+
            "发布时间：{0} " + NEWLINE+
            "承兑行：{1} " + NEWLINE+
            "金额（万）：{2} " + NEWLINE+
            "期限（天）：{3} " + NEWLINE+
            "利率：{4} %" + NEWLINE
            ;

    @Override
    public boolean handler(GrabOrderContent grabOrderContent) {
        List<GrabOrderInfoVO> orderInfoVOS = grabOrderContent.getOrderInfoVOS();
        orderInfoVOS.parallelStream().forEach( o -> {
            String notifyContent = MessageFormat.format(notify_template,o.getPublishTime(),
                    o.getAcceptor(),
                    o.getAmount(),
                    o.getLimitDays(),
                    o.getRate(),
                    grabOrderContent.getSystemName());
            System.out.println("通知内容："+notifyContent);
            notify(notifyContent);
        });
        return true;
    }
/*

    private void notify(String content) throws ApiException {
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/gettoken");
        OapiGettokenRequest request = new OapiGettokenRequest();
        request.setAppkey("dinguekwidtxzjmscjsj");
        request.setAppsecret("kawlNpnLPF1KIN5APkGf5vKZkjGsUnJXIkE_-JWD_nn6rJkw6s2EuJCutZ57SPf7");
        request.setHttpMethod("GET");
        OapiGettokenResponse response = client.execute(request);
        String tokenStr = response.getBody();
        JSONObject tokenJson = JSONObject.parseObject(tokenStr);

        DingTalkClient client2 = new DefaultDingTalkClient("https://oapi.dingtalk.com/user/get_by_mobile");
        OapiUserGetByMobileRequest req = new OapiUserGetByMobileRequest();
        req.setMobile("13808868866");
        req.setHttpMethod("GET");
        OapiUserGetByMobileResponse rsp = client2.execute(req, tokenJson.getString("access_token"));
        System.out.println(rsp.getBody());
    }
*/

    private void notify(String content){
        // 创建Httpclient对象
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        CloseableHttpResponse response = null;
        String resultString = null;
        try {
            Long timestamp = System.currentTimeMillis();
            String sign = sign(timestamp);
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(CommonPropertiesUtil.get(SystemConstant.DING_DING_WEBHOOK)+"&timestamp="+timestamp+"&sign="+sign);
            httpPost.addHeader("Content-Type", "application/json");

            if (content != null) {

                //消息内容
                Map<String, String> contentMap = Maps.newHashMap();
                contentMap.put("content", content);
                //通知人
                Map<String, Object> atMap = Maps.newHashMap();
                //1.是否通知所有人
                atMap.put("isAtAll", false);
                //2.通知具体人的手机号码列表
                atMap.put("atMobiles", getAtPhone());

                Map<String, Object> reqMap = Maps.newHashMap();
                reqMap.put("msgtype", "text");
                reqMap.put("text", contentMap);
                reqMap.put("at", atMap);
                httpPost.setEntity(new StringEntity(JSONObject.toJSONString(reqMap), "utf-8"));
            }
            // 执行http请求
            response = httpClient.execute(httpPost);
            resultString = EntityUtils.toString(response.getEntity(), "utf-8");
            System.out.println("钉钉机器人人返回结果："+resultString);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String sign(Long timestamp) throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException {
        String secret = CommonPropertiesUtil.get(SystemConstant.DING_DING_SECRET);

        String stringToSign = timestamp + "\n" + secret;
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256"));
        byte[] signData = mac.doFinal(stringToSign.getBytes("UTF-8"));
        String sign = URLEncoder.encode(new String(Base64.encodeBase64(signData)),"UTF-8");
        return sign;
    }

    private List<String> getAtPhone(){
        String notifyPhones = CommonPropertiesUtil.get(SystemConstant.NOTIFY_PHONE);
        String[] phoneArray = notifyPhones.split(",");
        List<String> phones = new ArrayList<>();
        Arrays.stream(phoneArray).forEach( p->{
            phones.add(p);
        });
        return phones;
    }

    public static void main(String[] args) throws ApiException {
        GrabOrderInfoNotifyHandler handler = new GrabOrderInfoNotifyHandler();
        handler.notify("aaaa");
    }
}
