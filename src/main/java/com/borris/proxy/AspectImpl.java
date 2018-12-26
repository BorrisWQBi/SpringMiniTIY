package com.borris.proxy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Queue;

public class AspectImpl implements Comparable<AspectImpl> {
    Object targetAspect;

    private int order;

    private List<Method> beforeMethodList;
    private Method aroundMethod;
    private List<Method> afterMethodList;

    @Override
    public int compareTo(AspectImpl o) {
        if (this.order < o.order)
            return -1;
        if (this.order == o.order)
            return 0;
        return 1;
    }

    public void invokeBefore(Method targetMethod) {
        if (beforeMethodList != null && !beforeMethodList.isEmpty()) {
            beforeMethodList.forEach(beforeMethod -> {
                Object[] args = new Object[]{};
                if (hasMatchType(beforeMethod.getParameterTypes(), targetMethod)) {
                    args = new Object[]{targetMethod};
                }
                beforeMethod.setAccessible(true);
                try {
                    beforeMethod.invoke(targetAspect, args);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public void invokeAfter(Method targetMethod) {
        if (afterMethodList != null && !afterMethodList.isEmpty()) {
            afterMethodList.forEach(afterMethod -> {
                Object[] args = new Object[]{};
                if (hasMatchType(afterMethod.getParameterTypes(), targetMethod)) {
                    args = new Object[]{targetMethod};
                }
                afterMethod.setAccessible(true);
                try {
                    afterMethod.invoke(targetAspect, args);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public Object invokeAround(Object targetObject, Method targetMethod, Object[] targetArgs) throws InvocationTargetException, IllegalAccessException {
        return aroundMethod.invoke(targetAspect,new MethodInvoker(targetObject,targetMethod,targetArgs));
    }

    private boolean hasMatchType(Class<?>[] parameterTypes, Method targetMethod) {
        for (Class clazz : parameterTypes) {
            if (clazz.equals(targetMethod.getClass()))
                return true;
        }
        return false;
    }
}
