package com.borris.proxy;

import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class AspectInvoker {

    @Getter
    @Setter
    List<AspectElement> aspectEleList;

    public AspectInvoker(List<AspectElement> aspectEleList){
        this.aspectEleList = aspectEleList;
        buildAspectProxy();
    }

    private void buildAspectProxy() {
        if (aspectEleList != null && !aspectEleList.isEmpty()) {
            for (int i = 0; i < aspectEleList.size() - 1; i++) {
                aspectEleList.get(i).setNextInvoker(aspectEleList.get(i + 1));
            }
        }
    }

    public Object invoke(Object targetBean, Method targetMethod, Object... args) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Object result;
        if (aspectEleList != null && !aspectEleList.isEmpty()) {
            AspectElement first = aspectEleList.get(0);
            result = first.invoke(targetBean,targetMethod,args);
        }else{
            result = targetMethod.invoke(targetBean, args);
        }
        return result;
    }
}
