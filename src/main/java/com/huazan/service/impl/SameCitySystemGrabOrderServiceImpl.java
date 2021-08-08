package com.huazan.service.impl;

import com.huazan.constants.SameCityConstant;
import com.huazan.pojo.LoginResultInfo;
import com.huazan.pojo.samecity.SameCityMatchRule;
import com.huazan.service.IGrabOrderMatchDataService;
import com.huazan.utils.SameCitySystemPropertiesUtil;
import com.huazan.utils.SameCityUserPropertiesUtil;
import com.huazan.utils.WebDriverUtil;
import com.huazan.vo.GrabOrderInfoVO;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

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

        Cookie token = driver.manage().getCookieNamed("access_token");
        System.out.println("token=" + token);
        LoginResultInfo loginResultInfo = new LoginResultInfo();
        loginResultInfo.setToken("Bearer "+token.getValue());
    }

    @Override
    String loginUrl() {
        return SameCitySystemPropertiesUtil.get(SameCityConstant.LOGIN_URL);
    }

    protected List<GrabOrderInfoVO> doQuery(SameCityMatchRule rule){
        return null;
    }
}
