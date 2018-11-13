package com.borris.context;

import com.borris.annotation.Component;
import com.borris.annotation.Controller;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.URISyntaxException;
import java.util.*;

public class ApplicationContext {

    private static ApplicationContext applicationContext;

    @Getter
    @Setter
    private Map<String, Object> beanMap;
    @Getter
    @Setter
    private Map<String, Object> controllerMap;
    @Getter
    @Setter
    private Map<String, Object> serviceMap;
    @Getter
    @Setter
    private Map<String, Object> repositoryMap;
    @Getter
    @Setter
    private Map<String, Object> requestMap;

    private ApplicationContext() {
        beanMap = new HashMap<String, Object>();
        controllerMap = new HashMap<String, Object>();
        serviceMap = new HashMap<String, Object>();
        repositoryMap = new HashMap<String, Object>();
        requestMap = new HashMap<String, Object>();
    }

    public static ApplicationContext getInstance() {
        if (applicationContext == null) {
            synchronized (applicationContext) {
                if (applicationContext == null) {
                    applicationContext = new ApplicationContext();
                }
            }
        }
        return applicationContext;
    }

    public static Object getBean(String beanName) {
        return applicationContext.beanMap.get(beanName);
    }

    /**
     * 将class文件装载入jvm
     * */
    public static void initClasses(List<String> allClasses) {
        ClassLoader cl = applicationContext.getClass().getClassLoader();
        allClasses.forEach(className -> {
            try {
                Class clazz = cl.loadClass(className);
                Object bean = clazz.newInstance();
                applicationContext.putByAnno(clazz,bean);
            } catch (Exception e) {
                System.out.println("error while loading class "+className);
                e.printStackTrace();
                return;
            }
        });
    }

    private void putByAnno(Class clazz, Object bean) {
        Component component = (Component) clazz.getAnnotation(Component.class);
        if(component != null){
            String beanName = component.name();
            applicationContext.beanMap.put(beanName,bean);
        }
        Controller controller = (Controller) clazz.getAnnotation(Controller.class);
        if(controller!=null){
            String value = controller.value();
            String controllerName = StringUtils.isEmpty(value)? clazz.getName():value;
            controllerMap.put(controllerName,bean);
        }
    }
}
