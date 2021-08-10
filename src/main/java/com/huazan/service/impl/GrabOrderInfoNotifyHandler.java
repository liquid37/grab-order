package com.huazan.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiGettokenRequest;
import com.dingtalk.api.request.OapiMessageCorpconversationAsyncsendV2Request;
import com.dingtalk.api.request.OapiUserGetByMobileRequest;
import com.dingtalk.api.response.OapiGettokenResponse;
import com.dingtalk.api.response.OapiMessageCorpconversationAsyncsendV2Response;
import com.dingtalk.api.response.OapiUserGetByMobileResponse;
import com.google.common.collect.Maps;
import com.huazan.constants.DingTalkConstant;
import com.huazan.constants.SystemConstant;
import com.huazan.pojo.GrabOrderContent;
import com.huazan.service.IGrabOrderHandler;
import com.huazan.utils.CommonPropertiesUtil;
import com.huazan.utils.DingTalkPropertiesUtil;
import com.huazan.vo.GrabOrderInfoVO;
import com.taobao.api.ApiException;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
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
import java.util.concurrent.TimeUnit;

@Service("notifyHandler")
public class GrabOrderInfoNotifyHandler implements IGrabOrderHandler {

    static ExpiringMap<String, String> map = ExpiringMap.builder()
            .variableExpiration().expirationPolicy(ExpirationPolicy.CREATED).build();

    private static final String TOKEN_KEY = "DING_TALK_TOKEN";

    private static final String NOTIFY_PHONE_KEY = "NOTIFY_PHONE";

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
            // 群通知
            //notifyGroup(notifyContent);
            // 个人通知
            try {
                notifyUser(notifyContent);
            } catch (ApiException e) {
                e.printStackTrace();
            }
        });
        return true;
    }


    private void notifyUser(String content) throws ApiException {
        String token = map.get(TOKEN_KEY);
        DingTalkClient client;
        if(StringUtils.isEmpty(token)){
            client = new DefaultDingTalkClient(DingTalkPropertiesUtil.get(DingTalkConstant.GET_TOKEN_URL));
            OapiGettokenRequest request = new OapiGettokenRequest();
            request.setAppkey(DingTalkPropertiesUtil.get(DingTalkConstant.APP_KEY));
            request.setAppsecret(DingTalkPropertiesUtil.get(DingTalkConstant.APP_SECRET));
            request.setHttpMethod("GET");
            OapiGettokenResponse response = client.execute(request);
            String tokenStr = response.getBody();
            JSONObject tokenJson = JSONObject.parseObject(tokenStr);
            token = tokenJson.getString("access_token");
            map.put(TOKEN_KEY,token,5400, TimeUnit.SECONDS);
        }

        String phoneStr = CommonPropertiesUtil.get(SystemConstant.NOTIFY_PHONE);
        String[] phoneArray = phoneStr.split(",");
        List<String> userIds = new ArrayList<>();
        for(String phone : phoneArray) {
            String userId = map.get(NOTIFY_PHONE_KEY + "_" + phone);
            if(StringUtils.isEmpty(userId)) {
                client = new DefaultDingTalkClient(DingTalkPropertiesUtil.get(DingTalkConstant.GET_BY_MOBILE));
                OapiUserGetByMobileRequest req = new OapiUserGetByMobileRequest();
                req.setMobile(phoneStr);
                req.setHttpMethod("GET");
                OapiUserGetByMobileResponse rsp = client.execute(req, token);
                JSONObject jsonObject = JSONObject.parseObject(rsp.getBody());
                userId = jsonObject.getString("userid");
                map.put(NOTIFY_PHONE_KEY + "_" + phone,userId,5400, TimeUnit.SECONDS);
            }
            userIds.add(userId);
        }
        String userIdList = StringUtils.join(userIds, ",");

        client = new DefaultDingTalkClient(DingTalkPropertiesUtil.get(DingTalkConstant.SYNC_SEND));
        OapiMessageCorpconversationAsyncsendV2Request v2Request = new OapiMessageCorpconversationAsyncsendV2Request();
        v2Request.setAgentId(Long.valueOf(DingTalkPropertiesUtil.get(DingTalkConstant.AGENT_ID)));
        v2Request.setUseridList(userIdList);
        v2Request.setToAllUser(false);

        OapiMessageCorpconversationAsyncsendV2Request.Msg msg = new OapiMessageCorpconversationAsyncsendV2Request.Msg();
        msg.setMsgtype("text");
        msg.setText(new OapiMessageCorpconversationAsyncsendV2Request.Text());
        msg.getText().setContent(content);
        v2Request.setMsg(msg);

        OapiMessageCorpconversationAsyncsendV2Response response = client.execute(v2Request, token);
        System.out.println(response.getBody());
    }


    private void notifyGroup(String content){
        // 创建Httpclient对象
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        CloseableHttpResponse response = null;
        String resultString = null;
        try {
            Long timestamp = System.currentTimeMillis();
            String sign = sign(timestamp);
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(DingTalkPropertiesUtil.get(DingTalkConstant.DING_DING_WEBHOOK)+"&timestamp="+timestamp+"&sign="+sign);
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
            //System.out.println("钉钉机器人人返回结果："+resultString);
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
        String secret = DingTalkPropertiesUtil.get(DingTalkConstant.DING_DING_SECRET);

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

    public static void main(String[] args) throws ApiException, InterruptedException {
        GrabOrderInfoNotifyHandler handler = new GrabOrderInfoNotifyHandler();
        handler.notifyUser("测试钉钉推送消息2");

    }
}
