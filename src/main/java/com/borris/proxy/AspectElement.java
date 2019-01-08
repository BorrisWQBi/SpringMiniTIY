package com.borris.proxy;

import com.borris.annotation.*;
import lombok.Getter;
import lombok.Setter;

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
            return 1;
        if (this.order > o.order)
            return 1;
        if (this.order == o.order)
            return 0;
        return -1;
    }

}
