package com.borris.proxy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MethodInvoker {
    private Object targetObject;
    private Method targetMethod;
    private Object[] targetArgs;

    public MethodInvoker(Object targetObject, Method targetMethod, Object[] targetArgs) {
        this.targetObject = targetObject;
        this.targetMethod = targetMethod;
        this.targetArgs = targetArgs;
    }

    public Object preceed() throws InvocationTargetException, IllegalAccessException {
        return targetMethod.invoke(targetObject,targetArgs);
    }
}
