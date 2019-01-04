package com.borris.proxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class CglibProxy implements MethodInterceptor {
    private Object targetBean;
    private AspectImpl aspectObj;

    public Object getInstance(Object targetObject, AspectImpl aspectObj) {
        this.targetBean = targetObject;
        Enhancer eh = new Enhancer();
        eh.setSuperclass(this.targetBean.getClass());
        eh.setCallback(this);
        Object proxyObject = eh.create();

        this.aspectObj = aspectObj;
        if (this.aspectObj != null) {
            this.aspectObj.buildAroundStacks();
        }

        return proxyObject;
    }


    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        Object result;
        if (aspectObj != null) {
            aspectObj.invokeBefore(method);
            result = proxy.invokeSuper(obj,args);
//            result = aspectObj.invokeAround(targetBean, method, args);
            aspectObj.invokeBefore(method);
        } else {
            result = method.invoke(targetBean, args);
        }
        return result;
    }
}
