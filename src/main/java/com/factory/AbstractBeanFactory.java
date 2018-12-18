package com.factory;

import lombok.Getter;
import lombok.Setter;

import javax.naming.OperationNotSupportedException;
import java.lang.reflect.Method;

public abstract class AbstractBeanFactory {
    protected static AbstractBeanFactory beanFactory = null;

    public abstract Object getBean(String beanName);

    public void initBeanFactoryByAnnotation(String classLocPath) throws OperationNotSupportedException {
        throw new OperationNotSupportedException("this factory not supported ANNOTATION init method");
    }

    public void initBeanFactoryByXml(String xmlPath) throws OperationNotSupportedException {
        throw new OperationNotSupportedException("this factory not supported XML init method");
    }
    public abstract RequestMapEntry getRequestMapEntry(String url);
}

