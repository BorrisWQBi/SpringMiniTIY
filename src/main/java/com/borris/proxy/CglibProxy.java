package com.borris.proxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class CglibProxy implements MethodInterceptor {
    private Object targetBean;
    private AspectImpl aspectObj;

    public static Object getInstance(CglibProxy clb, Object targetObject, AspectImpl aspectObj) {
        clb.targetBean = targetObject;
        Enhancer eh = new Enhancer();
        eh.setSuperclass(clb.targetBean.getClass());
        eh.setCallback(clb);
        Object proxyObject = eh.create();

        clb.aspectObj = aspectObj;
        if (clb.aspectObj != null) {
            clb.aspectObj.buildAroundStacks();
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
