package com.borris.context;

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
}
