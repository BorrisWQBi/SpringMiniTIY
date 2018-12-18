package com.borris.context;

import com.factory.AbstractBeanFactory;

public class ApplicationContext{
    private static AbstractBeanFactory beanFactory;

    public static void setBeanFactory(AbstractBeanFactory beanFactory){
        ApplicationContext.beanFactory = beanFactory;
    }

    public AbstractBeanFactory getBeanFactory(){
        return beanFactory;
    }

    public Object getBeanByName(String beanName) {
        return beanFactory.getBean(beanName);
    }

}
