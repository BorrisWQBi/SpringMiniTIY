package com.factory;

import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class RequestMapEntry{
    @Getter
    @Setter
    private String url;
    @Getter
    @Setter
    private Object targetObj;
    @Getter
    @Setter
    private Method method;

    public RequestMapEntry(String url, Object targetObj, Method method){
        this.url = url;
        this.targetObj = targetObj;
        this.method = method;
    }

    public Object invokeMethod(Object[] params) throws InvocationTargetException, IllegalAccessException {
        return method.invoke(targetObj,params);
    }
}
