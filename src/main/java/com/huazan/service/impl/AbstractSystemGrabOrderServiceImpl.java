package com.huazan.service.impl;

import com.huazan.constants.SystemConstant;
import com.huazan.pojo.GrabOrderContent;
import com.huazan.pojo.LoginResultInfo;
import com.huazan.pojo.base.BaseMatchRule;
import com.huazan.service.IGrabOrderHandler;
import com.huazan.service.IGrabOrderMatchDataService;
import com.huazan.service.ISystemGrabOrderService;
import com.huazan.utils.CommonPropertiesUtil;
import com.huazan.utils.ThreadPoolUtil;
import com.huazan.vo.GrabOrderInfoVO;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class AbstractSystemGrabOrderServiceImpl<R extends BaseMatchRule> implements ISystemGrabOrderService {

    FirefoxDriver driver;

    @Autowired
    @Qualifier("handlerStart")
    IGrabOrderHandler grabOrderHandler;

    Executor executor = ThreadPoolUtil.createThreadPoolExecutorCallerRunsPolicy(5,10,1);

    LoginResultInfo loginResultInfo;

    abstract  IGrabOrderMatchDataService getGrabOrderMatchDataService();

    public void login() throws Exception {
        System.setProperty(CommonPropertiesUtil.get(SystemConstant.WEB_DRIVER_KEY), CommonPropertiesUtil.get(SystemConstant.WEB_DRIVER_VALUE));
        driver = new FirefoxDriver();
        while (true) {
            try {
                driver.get(loginUrl());
                //解决使用selenium-java被检测导致滑块验证失败
                ((JavascriptExecutor) driver).executeScript("Object.defineProperties(navigator,{ webdriver:{ get: () => false } })");
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
            break;
        }
        doLogin();
    }

    @Override
    public void grabOrder() throws Exception {
        //1、执行登录，获取token信息
        login();
        //2、加载监控规则
        List matchData = getGrabOrderMatchDataService().getMatchData();
        //3、根据监控规则抓单
        doGrabOrder(matchData);

    }

    protected void doGrabOrder(List<R> matchData){
        startGrabOrder(matchData);
    }

    protected void startGrabOrder(List<R> matchDataList){
        int initialDelay = 1;
        for(R rule : matchDataList) {
            ScheduledExecutorService executorService = ThreadPoolUtil.createScheduledExecutorService(1);
            executorService.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    doGrabOrderByRule(rule);
                }
            }, initialDelay, 10, TimeUnit.SECONDS);
            initialDelay++;
        }
    }

    void doGrabOrderByRule(R rule){
        executor.execute(()->{
            List<GrabOrderInfoVO> orderInfoVOS = doQuery(rule);
            GrabOrderContent content = new GrabOrderContent();
            content.setOrderInfoVOS(orderInfoVOS);
            content.setSystemName(getSystemName());
            content.setMatchDataList(rule.getMatchDataList());
            grabOrderHandler.handler(content);
        });
    }

    abstract void doLogin() throws Exception;

    abstract String loginUrl();

    abstract List<GrabOrderInfoVO> doQuery(R rule);

    abstract String getSystemName();

}
