package com.huazan.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huazan.constants.SameCityConstant;
import com.huazan.constants.SystemConstant;
import com.huazan.pojo.LoginResultInfo;
import com.huazan.pojo.samecity.SameCityMatchData;
import com.huazan.pojo.samecity.SameCityMatchRule;
import com.huazan.pojo.samecity.SameCityQO;
import com.huazan.service.IGrabOrderMatchDataService;
import com.huazan.utils.SameCitySystemPropertiesUtil;
import com.huazan.utils.SameCityUserPropertiesUtil;
import com.huazan.utils.WebDriverUtil;
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
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 同城系统抢单service实现
 */
@Service("sameSystem")
public class SameCitySystemGrabOrderServiceImpl extends AbstractSystemGrabOrderServiceImpl<SameCityMatchRule> {

    @Autowired
    @Qualifier("sameCitySystemMatchDataService")
    IGrabOrderMatchDataService sameCitySystemMatchDataService;

    @Override
    IGrabOrderMatchDataService getGrabOrderMatchDataService() {
        return sameCitySystemMatchDataService;
    }

    @Override
    void doLogin() throws Exception {
        //定位账号输入框
        WebElement accountElement = WebDriverUtil.getElement(driver, By.ByXPath.xpath(SameCitySystemPropertiesUtil.get(SameCityConstant.LOGIN_ACCOUNT_INPUT))); // todo 改成配置
        accountElement.clear(); // 清空账号
        accountElement.sendKeys(SameCityUserPropertiesUtil.get(SameCityConstant.ACCOUNT)); // 设置账号
        TimeUnit.MILLISECONDS.sleep(500);
        WebElement passwordElement = WebDriverUtil.getElement(driver, By.ByXPath.xpath(SameCitySystemPropertiesUtil.get(SameCityConstant.LOGIN_PASSWORD_INPUT))); // todo 改成配置
        passwordElement.clear(); // 清空密码
        passwordElement.sendKeys(SameCityUserPropertiesUtil.get(SameCityConstant.PASSWORD)); // 设置密码

        //定位滑块 ,滑块长度为 40 * 34  ,整个滑块区域为360 * 34 ，因此计算出滑动距离为360-40 = 320
        WebElement scaleElement = WebDriverUtil.getElement(driver, By.ByXPath.xpath(SameCitySystemPropertiesUtil.get(SameCityConstant.SCALE_INPUT))); // todo 改成配置

        Actions action = new Actions(driver);
        // 滑动滑块
        TimeUnit.SECONDS.sleep(1);
        action.dragAndDropBy(scaleElement,320,0).perform();
        TimeUnit.MILLISECONDS.sleep(500);

        //点击登录按钮
        WebElement submitButton = WebDriverUtil.getElement(driver, By.ByXPath.xpath(SameCitySystemPropertiesUtil.get(SameCityConstant.SUBMIT_BUTTON))); // todo 改成配置
        submitButton.click();
        TimeUnit.MILLISECONDS.sleep(2000);
        Cookie token = driver.manage().getCookieNamed("access_token");
        System.out.println("token=" + token);
        loginResultInfo = new LoginResultInfo();
        loginResultInfo.setToken("Bearer "+token.getValue());
    }

    @Override
    String loginUrl() {
        return SameCitySystemPropertiesUtil.get(SameCityConstant.LOGIN_URL);
    }

    protected List<GrabOrderInfoVO> doQuery(SameCityMatchRule rule){
        Map<String, String> acceptorMap = rule.getMatchDataList().stream().collect(Collectors.toMap(SameCityMatchData::getAcceptor, SameCityMatchData::getAcceptor));
        Set<String> acceptorList = acceptorMap.keySet();
        String acceptors = StringUtils.join(acceptorList, ",");
        List<GrabOrderInfoVO> orderListVOList = new ArrayList<>();
        // 查询当日我的订单
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost(SameCitySystemPropertiesUtil.get(SameCityConstant.GRAB_URL));
        // 由客户端执行(发送)Get请求
        httpPost.addHeader("Authorization", loginResultInfo.getToken());
        httpPost.addHeader("Content-Type", "application/json");
        SameCityQO listQueryQO = new SameCityQO();
        //listQueryQO.setDAmtStart(deepMatchRule.getRuleParam().getMinAmount()*1000000);
        if(rule.getRuleParam().getMaxAmount()!=null) {
            listQueryQO.setPriceEp(rule.getRuleParam().getMaxAmount()+"");
        }
        if(rule.getRuleParam().getMinAmount()!=null){
            listQueryQO.setPriceSp(rule.getRuleParam().getMinAmount()+"");
        }
        listQueryQO.setBankName(acceptors);
        HttpEntity entity = new StringEntity(JSONObject.toJSONString(listQueryQO), "UTF-8");
        //System.out.println("请求参数："+ JSONObject.toJSONString(listQueryQO));
        httpPost.setEntity(entity);
        // 响应模型
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpPost);
            // 从响应模型中获取响应实体
            HttpEntity responseEntity = response.getEntity();
            String listResultJson = EntityUtils.toString(responseEntity);
            JSONObject jsonObject = JSONObject.parseObject(listResultJson);
            JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("list");

            if(!jsonArray.isEmpty()){
                Iterator<Object> iterator = jsonArray.stream().iterator();
                while (iterator.hasNext()){
                    JSONObject dataObject =  (JSONObject) iterator.next();
                    GrabOrderInfoVO orderListVO = new GrabOrderInfoVO();
                    orderListVO.setId(dataObject.getString("ticketId"));
                    orderListVO.setAcceptor(dataObject.getString("bankName"));
                    orderListVO.setLimitDays(dataObject.getInteger("rateDay"));
                    orderListVO.setRate(dataObject.getBigDecimal("yearQuote"));
                    orderListVO.setAmount(dataObject.getLong("ticketPrice")+"万");
                    orderListVO.setPublishTime(dataObject.getString("publishTime"));
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
        return SystemConstant.SAME_CITY_SYSTEM_NAME;
    }
}
