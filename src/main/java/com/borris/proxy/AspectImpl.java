package com.borris.proxy;

import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class AspectImpl implements Comparable<AspectImpl> {

    @Getter
    @Setter
    private Object targetAspect;

    @Getter
    @Setter
    private int order;

    @Getter
    @Setter
    private List<Method> beforeMethodList;
    @Getter
    @Setter
    private List<Method> aroundMethodList;
    @Getter
    @Setter
    private MethodInvoker aroundMethod;
    @Getter
    @Setter
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

    public Object invokeAround(Object targetBean, Method targetMethod, Object... args) throws InvocationTargetException, IllegalAccessException {
        MethodInvoker targetInvoker = new MethodInvoker(targetBean, targetMethod, args);
        MethodInvoker temp = aroundMethod.getNextInvoker();
        while (temp.hasNextNode()) {
            temp = temp.getNextInvoker();
        }
        temp.setTargetArgs(new MethodInvoker(targetBean, targetMethod, args));
        Object result = aroundMethod.preceed();
        temp.setTargetArgs(new Object[]{null});
        return result;
    }

    private boolean hasMatchType(Class<?>[] parameterTypes, Method targetMethod) {
        for (Class clazz : parameterTypes) {
            if (clazz.equals(targetMethod.getClass()))
                return true;
        }
        return false;
    }

    public void buildAroundStacks() {
        if (aroundMethodList != null && !aroundMethodList.isEmpty()) {
            int idx = aroundMethodList.size() - 1;
            //先把最底层的target放进去
            Method tempMethod = null;
            MethodInvoker mi = null;
            while (idx >= 0) {
                tempMethod = aroundMethodList.get(idx);
                mi = new MethodInvoker(targetAspect, tempMethod, mi);
                idx--;
            }
            this.aroundMethod = mi;
        }
    }
}
