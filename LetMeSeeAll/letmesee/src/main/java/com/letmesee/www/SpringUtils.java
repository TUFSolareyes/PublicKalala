package com.letmesee.www;

import com.letmesee.www.util.LogUtil;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;


@Component
public class SpringUtils implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringUtils.applicationContext = applicationContext;
    }

    public static Object getBean(String name) {
        try{
            Object bean = applicationContext.getBean(name);
            return bean;
        }catch (NoSuchBeanDefinitionException e){
            LogUtil.logMessage("没有这个对象",LogUtil.WARN);
        }
        return null;
    }
}


