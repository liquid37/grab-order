package com.huazan.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huazan.constants.DeepConstant;
import com.huazan.constants.SystemConstant;
import com.huazan.pojo.LoginResultInfo;
import com.huazan.pojo.deep.DeepMatchData;
import com.huazan.pojo.deep.DeepMatchRule;
import com.huazan.pojo.deep.DeepSystemQO;
import com.huazan.service.IGrabOrderMatchDataService;
import com.huazan.utils.DeepSystemPropertiesUtil;
import com.huazan.utils.DeepUserPropertiesUtil;
import com.huazan.utils.WebDriverUtil;
import com.huazan.vo.DeepUserTokenInfo;
import com.huazan.vo.GrabOrderInfoVO;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 深度系统抢单service实现
 */
@Service("deepSystem")
public class DeepSystemGrabOrderServiceImpl extends AbstractSystemGrabOrderServiceImpl<DeepMatchRule> {

    @Autowired
    @Qualifier(value = "deepSystemMatchDataService")
    private IGrabOrderMatchDataService grabOrderMatchDataService;

    @Override
    String loginUrl() {
        return DeepSystemPropertiesUtil.get(DeepConstant.LOGIN_URL);
    }

    @Override
    IGrabOrderMatchDataService getGrabOrderMatchDataService() {
        return grabOrderMatchDataService;
    }

    @Override
    void doLogin() throws Exception {
        //定位账号输入框
        WebElement accountElement = WebDriverUtil.getElement(driver, By.ByXPath.xpath(DeepSystemPropertiesUtil.get(DeepConstant.LOGIN_ACCOUNT_INPUT)));
        accountElement.clear(); // 清空账号
        accountElement.sendKeys(DeepUserPropertiesUtil.get(DeepConstant.ACCOUNT)); // 设置账号
        TimeUnit.MILLISECONDS.sleep(500);
        WebElement passwordElement = WebDriverUtil.getElement(driver, By.ByXPath.xpath(DeepSystemPropertiesUtil.get(DeepConstant.LOGIN_PASSWORD_INPUT)));
        passwordElement.clear(); // 清空密码
        passwordElement.sendKeys(DeepUserPropertiesUtil.get(DeepConstant.PASSWORD)); // 设置密码
        //定位滑块 ,滑块长度为 40 * 34  ,整个滑块区域为360 * 34 ，因此计算出滑动距离为360-40 = 320
        WebElement scaleElement = WebDriverUtil.getElement(driver, By.ByXPath.xpath(DeepSystemPropertiesUtil.get(DeepConstant.SCALE_INPUT)));

        Actions action = new Actions(driver);
        // 滑动滑块
        TimeUnit.SECONDS.sleep(1);
        action.dragAndDropBy(scaleElement, 320, 0).perform();
        TimeUnit.MILLISECONDS.sleep(500);
        //点击登录按钮
        WebElement submitButton = WebDriverUtil.getElement(driver, By.ByXPath.xpath(DeepSystemPropertiesUtil.get(DeepConstant.SUBMIT_BUTTON))); // todo 改成配置
        submitButton.click();
        // 登录成功
        TimeUnit.MILLISECONDS.sleep(2000);
        String loginUserInfo = driver.getLocalStorage().getItem("F_user");
        System.out.println("f_user: " + loginUserInfo);
        DeepUserTokenInfo tokenInfo = JSONObject.parseObject(loginUserInfo, DeepUserTokenInfo.class);
        String token = tokenInfo.getToken();

        loginResultInfo = new LoginResultInfo();
        loginResultInfo.setToken(token);
        loginResultInfo.setExpiredTime(24 * 60 * 60 * 1000L);
    }

    @Override
    protected List<GrabOrderInfoVO> doQuery(DeepMatchRule rule) {
        List<GrabOrderInfoVO> orderListVOList = new ArrayList<>();
        // 查询当日我的订单
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost(DeepSystemPropertiesUtil.get(DeepConstant.GRAB_URL));
        // 由客户端执行(发送)Get请求
        httpPost.addHeader("token", loginResultInfo.getToken());
        httpPost.addHeader("Content-Type", "application/json");
        DeepSystemQO listQueryQO = new DeepSystemQO();
        //listQueryQO.setDAmtStart(deepMatchRule.getRuleParam().getMinAmount()*1000000);
        if(rule.getRuleParam().getMaxAmount()!=null) {
            listQueryQO.setDAmtEnd(rule.getRuleParam().getMaxAmount() * 1000000+1);
        }
        if(rule.getRuleParam().getMinAmount()!=null){
            listQueryQO.setDAmtStart(rule.getRuleParam().getMinAmount() * 1000000-1);
        }
        // todo 测试银行
        Map<String, String> acceptorMap = rule.getMatchDataList().stream().collect(Collectors.toMap(DeepMatchData::getAcceptor, DeepMatchData::getAcceptor));
        Set<String> acceptorList = acceptorMap.keySet();
        String acceptors = StringUtils.join(acceptorList, ",");
        listQueryQO.setKeyWords(acceptors);
        //listQueryQO.setKeyWords("广州银行股份有限公司");
        HttpEntity entity = new StringEntity(JSONObject.toJSONString(listQueryQO), "UTF-8");
        httpPost.setEntity(entity);
        // 响应模型
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpPost);
            // 从响应模型中获取响应实体
            HttpEntity responseEntity = response.getEntity();
            String listResultJson = EntityUtils.toString(responseEntity);
            JSONObject jsonObject = JSONObject.parseObject(listResultJson);
            JSONArray jsonArray = jsonObject.getJSONObject("page").getJSONArray("list");

            if(!jsonArray.isEmpty()){
                Iterator<Object> iterator = jsonArray.stream().iterator();
                while (iterator.hasNext()){
                    JSONObject dataObject =  (JSONObject) iterator.next();
                    GrabOrderInfoVO orderListVO = new GrabOrderInfoVO();
                    orderListVO.setId(dataObject.getString("id"));
                    orderListVO.setAcceptor(dataObject.getString("acceptance"));
                    orderListVO.setLimitDays(dataObject.getInteger("discountDays"));
                    orderListVO.setRate(dataObject.getBigDecimal("annualInterest").divide(new BigDecimal("10000")));
                    orderListVO.setAmount(dataObject.getLong("draftAmt")/1000000+"万");
                    orderListVO.setPublishTime(dataObject.getString("publishDate"));
                    orderListVOList.add(orderListVO);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return orderListVOList;
    }

    @Override
    String getSystemName() {
        return SystemConstant.DEEP_SYSTEM_NAME;
    }
}
