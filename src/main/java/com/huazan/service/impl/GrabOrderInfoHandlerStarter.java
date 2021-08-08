package com.huazan.service.impl;

import com.huazan.pojo.GrabOrderContent;
import com.huazan.service.IGrabOrderHandler;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@Service("handlerStart")
public class GrabOrderInfoHandlerStarter implements IGrabOrderHandler, InitializingBean, ApplicationContextAware {

    private Collection<IGrabOrderHandler> handlers = new ArrayList<>();

    private ApplicationContext applicationContext;

    @Override
    public boolean handler(GrabOrderContent grabOrderContent) {
        for(IGrabOrderHandler handler : handlers){
            boolean result = handler.handler(grabOrderContent);
            if(!result){
                break;
            }
        }
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        IGrabOrderHandler filterHandler = applicationContext.getBean("filterHandler",IGrabOrderHandler.class);
        handlers.add(filterHandler);
        IGrabOrderHandler notifyHandler = applicationContext.getBean("notifyHandler",IGrabOrderHandler.class);
        handlers.add(notifyHandler);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
