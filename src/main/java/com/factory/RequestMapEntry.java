package com.factory;

import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

public class RequestMapEntry {
    @Getter
    @Setter
    private String url;
    @Getter
    @Setter
    private Object targetObj;
    @Getter
    @Setter
    private Method method;
    @Getter
    @Setter
    private String[] parameterNames;

    public RequestMapEntry(String url, Object targetObj, Method method, String[] parameterNames) {
        this.url = url;
        this.targetObj = targetObj;
        this.method = method;
        this.parameterNames = parameterNames;
    }

    public Object invokeMethod(Object[] params) throws InvocationTargetException, IllegalAccessException {
        return method.invoke(targetObj, params);
    }
}
