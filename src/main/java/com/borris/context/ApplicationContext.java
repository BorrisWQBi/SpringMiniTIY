package com.borris.context;

import com.borris.StringUtils;
import lombok.Getter;
import lombok.Setter;

import java.io.*;
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
     * 根据classLoc读取内容，并将所配置的需要读取的文件scanPackage放入数组
     * 因为懒（划掉）精力有限，所以只支持properties文件读取了
     * 支持classpath相对路径或者绝对路径读取
     * */
    public static List<String> checkPaths(String classLoc) throws IOException {
        String[] paths = classLoc.split(",");
        List<String> result = new ArrayList<String>();
        for (String filePath : paths) {
            InputStream is;
            if (filePath.indexOf("classpath:") == 0) {
                filePath = filePath.substring("classpath:".length());
                is = applicationContext.getClass().getClassLoader().getResourceAsStream(filePath);
            } else {
                is = new BufferedInputStream(new FileInputStream(filePath));
            }
            Properties properties = getProperties(is);
            is.close();
            String scanPackage = properties.getProperty("scanPackage");
            if(StringUtils.isNotEmpty(scanPackage)){
                result.add(scanPackage.trim());
            }
        }
        return result;
    }

    private static Properties getProperties(InputStream is) throws IOException {
        Properties properties = new Properties();
        properties.load(is);
        return properties;
    }
}
