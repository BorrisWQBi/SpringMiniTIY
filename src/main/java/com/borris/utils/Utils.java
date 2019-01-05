package com.borris.utils;

import com.borris.annotation.Component;
import org.apache.commons.lang3.StringUtils;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.MethodNode;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Utils {

    public static MethodNode getMethodNode(List<MethodNode> methodNodes, Method method) {
        String typeDesc = Type.getMethodDescriptor(method);
        for (MethodNode methodNode : methodNodes) {
            if (StringUtils.equals(methodNode.desc, typeDesc) && StringUtils.equals(methodNode.name, method.getName()))
                return methodNode;
        }
        return null;
    }

    public static boolean checkHasAnnotation(AnnotatedElement annoClazz, Class<? extends Annotation> annoType) {
        if(annoClazz.isAnnotationPresent(annoType))
            return true;
        Annotation[] annotations = annoClazz.getAnnotations();
        Annotation annotation = Arrays.stream(annotations)
                .filter(anno -> anno.getClass().isAnnotationPresent(annoType))
                .findFirst().orElse(null);
        if(annotation!=null)
            return true;
        boolean isParentComponent = false;
        for(Annotation anno : annotations){

            isParentComponent = checkHasAnnotation(anno.annotationType(),annoType);
            if(isParentComponent){
                return isParentComponent;
            }
        }
        return false;
    }

    public static boolean checkFieldHasAnno(Field f,Class<? extends Annotation> annoType){
        List annoList = Arrays.stream(f.getAnnotations())
                .filter(anno->anno.annotationType().equals(annoType))
                .collect(Collectors.toList());
        return !annoList.isEmpty();
    }
}
