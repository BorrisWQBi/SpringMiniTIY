package com.borris.proxy;

import com.sun.xml.internal.txw2.IllegalAnnotationException;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class AspectInvoker {

    @Getter
    @Setter
    Map<Class , Object> targetAspectByType;
    @Getter
    @Setter
    List<AspectElement> aspectEleList;
    @Getter
    @Setter
    private List<Method> beforeMethodList;
    @Getter
    @Setter
    private List<Method> aroundMethodList;
    @Getter
    @Setter
    private List<Method> afterMethodList;
    @Getter
    @Setter
    private MethodInvoker aroundMethod;

    public AspectInvoker(List<AspectElement> aspectEleList){
        this.aspectEleList = aspectEleList;
        targetAspectByType = new HashMap<>(aspectEleList.size());
        beforeMethodList = new ArrayList<>();
        afterMethodList = new ArrayList<>();
        aroundMethodList = new ArrayList<>();
        buildAspectProxy();
    }

    private void buildAspectProxy() {
        if (aspectEleList != null && !aspectEleList.isEmpty()){
            aspectEleList.forEach(ae -> {
                targetAspectByType.put(ae.getTargetAspect().getClass(),ae.getTargetAspect());
                beforeMethodList.addAll(ae.getBeforeMethodList());
                afterMethodList.addAll(ae.getAfterMethodList());
                aroundMethodList.addAll(ae.getAroundMethodList());
            });
            buildAroundStacks();
        }
    }

    public void invokeBefore(Method targetMethod) {
        if (beforeMethodList != null && !beforeMethodList.isEmpty()) {
            beforeMethodList.forEach(beforeMethod -> {
                Object[] args = new Object[beforeMethod.getParameterTypes().length];
                args = hasMatchType(beforeMethod.getParameterTypes(), targetMethod,args);
                beforeMethod.setAccessible(true);
                try {
                    beforeMethod.invoke(targetAspect(beforeMethod.getDeclaringClass()), args);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public Object targetAspect(Class c){
        return targetAspectByType.get(c);
    }

    public void invokeAfter(Method targetMethod) {
        if (afterMethodList != null && !afterMethodList.isEmpty()) {
            afterMethodList.forEach(afterMethod -> {
                Object[] args = new Object[afterMethod.getParameterTypes().length];
                args = hasMatchType(afterMethod.getParameterTypes(), targetMethod, args);
                afterMethod.setAccessible(true);
                try {
                    afterMethod.invoke(targetAspect(afterMethod.getDeclaringClass()), args);
                } catch (Exception e) {
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

    private Object[] hasMatchType(Class<?>[] parameterTypes, Method targetMethod, Object[] args) {
        for (int i = 0; i < parameterTypes.length; i++) {
            Class clazz = parameterTypes[i];
            if (clazz.equals(targetMethod.getClass())){
                args[i] = targetMethod;
                break;
            }
        }
        return args;
    }

    public void buildAroundStacks() throws IllegalFormatException {
        if (aroundMethodList != null && !aroundMethodList.isEmpty()) {
            int idx = aroundMethodList.size() - 1;
            //先把最底层的target放进去
            Method tempMethod = null;
            MethodInvoker mi = null;
            while (idx >= 0) {
                tempMethod = aroundMethodList.get(idx);
                //around方法没有参数时非法
                if (tempMethod.getParameterTypes().length == 0)
                    throw new IllegalAnnotationException("can't build around method without parameters");
                //around方法参数部位methodInvoker时非法
                if (!MethodInvoker.class.equals(tempMethod.getParameterTypes()[0]))
                    throw new IllegalAnnotationException("can't build around method without parameters");
                mi = new MethodInvoker(targetAspect(tempMethod.getDeclaringClass()), tempMethod, mi);
                idx--;
            }
            this.aroundMethod = mi;
        }
    }
}
