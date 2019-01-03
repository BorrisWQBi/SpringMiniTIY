package com.borris.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class JavaDynamicProxy implements InvocationHandler {
    private Object targetBean;
    private AspectImpl aspectObj;

    public JavaDynamicProxy(Object targetBean, AspectImpl aspectObj) {
        this.targetBean = targetBean;
        this.aspectObj = aspectObj;
        if(this.aspectObj!=null){
            this.aspectObj.buildAroundStacks();
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result;
        if(aspectObj!=null) {
            aspectObj.invokeBefore(method);
            result = aspectObj.invokeAround(targetBean, method, args);
            aspectObj.invokeBefore(method);
        }else{
            result = method.invoke(targetBean,args);
        }
        return result;
    }
}
