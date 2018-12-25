package com.borris.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class JavaDynamicProxy implements InvocationHandler {

    Object targetBean;


    List<AspectImpl> aspectList;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result;
        if(aspectList!=null && !aspectList.isEmpty()){
            aspectList.forEach(aspect -> {
                aspect.invokeBefore(method);
            });
            Queue<AspectImpl> aspectQueue = new LinkedList<AspectImpl>(aspectList);
            result = AspectImpl.invokeAround(proxy,method,args,aspectQueue);
            aspectList.forEach(aspect -> {
                aspect.invokeAfter(method);
            });
        }
        return null;
    }


}
