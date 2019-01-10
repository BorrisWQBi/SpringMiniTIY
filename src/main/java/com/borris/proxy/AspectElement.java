package com.borris.proxy;

import com.borris.annotation.*;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AspectElement implements Comparable<AspectElement> {

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
    private List<Method> afterMethodList;
    @Getter
    @Setter
    private MethodInvoker aroundMethod;

    @Getter
    @Setter
    private AspectElement nextInvoker;

    public AspectElement(Class aspectClass) throws IllegalAccessException, InstantiationException {
        if (!aspectClass.isAnnotationPresent(Aspect.class)) {
            throw new UnsupportedOperationException("class " + aspectClass.toString() + " is not an aspect class");
        }
        Order orderAnno = (Order) aspectClass.getAnnotation(Order.class);
        if(orderAnno != null){
            order = orderAnno.value();
        }else{
            order=-1;
        }
        Method[] allMethod = aspectClass.getDeclaredMethods();
        beforeMethodList = Arrays.stream(allMethod).filter(method -> method.isAnnotationPresent(Before.class)).collect(Collectors.toList());
        aroundMethodList = Arrays.stream(allMethod).filter(method -> method.isAnnotationPresent(Around.class)).collect(Collectors.toList());
        afterMethodList = Arrays.stream(allMethod).filter(method -> method.isAnnotationPresent(After.class)).collect(Collectors.toList());

        this.targetAspect = aspectClass.newInstance();
    }

    @Override
    public int compareTo(AspectElement o) {
        //如果order小于0，则优先级最低
        if (this.order < 0)
            return -1;
        if (this.order > o.order)
            return -1;
        if (this.order == o.order)
            return 0;
        return 1;
    }

    public void invokeBefore(Method targetMethod) {
        if (beforeMethodList != null && !beforeMethodList.isEmpty()) {
            beforeMethodList.forEach(beforeMethod -> {
                Object[] args = new Object[beforeMethod.getParameterTypes().length];
                int idx = hasMatchType(beforeMethod.getParameterTypes(), targetMethod);
                if (idx >= 0)
                    args[idx] = targetMethod;
                beforeMethod.setAccessible(true);
                try {
                    beforeMethod.invoke(targetAspect, args);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public void invokeAfter(Method targetMethod) {
        if (afterMethodList != null && !afterMethodList.isEmpty()) {
            afterMethodList.forEach(afterMethod -> {
                Object[] args = new Object[afterMethod.getParameterTypes().length];
                int idx = hasMatchType(afterMethod.getParameterTypes(), targetMethod);
                if (idx >= 0)
                    args[idx] = targetMethod;
                afterMethod.setAccessible(true);
                try {
                    afterMethod.invoke(targetAspect, args);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public Object invoke(Object targetBean, Method targetMethod, Object... args) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        MethodInvoker temp = aroundMethod;
        while (temp.hasNextNode()) {
            temp = temp.getNextInvoker();
        }
        invokeBefore(targetMethod);
        Object result=null;
        if(nextInvoker==null){
            temp.setTargetArgs(new MethodInvoker(targetBean, targetMethod, args));
            result = aroundMethod.preceed();
        }else{
            Method m = nextInvoker.getClass().getDeclaredMethod("invoke",Object.class,Method.class,args.getClass());
            temp.setTargetArgs(new MethodInvoker(nextInvoker, m, new Object[]{targetBean, targetMethod, args}));
            result = aroundMethod.preceed();
        }
        temp.setTargetArgs(new Object[]{null});
        invokeAfter(targetMethod);
        return result;
    }

    private int hasMatchType(Class<?>[] parameterTypes, Method targetMethod) {
        for (int i = 0; i < parameterTypes.length; i++) {
            Class clazz = parameterTypes[i];
            if (clazz.equals(targetMethod.getClass()))
                return i;
        }
        return -1;
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
