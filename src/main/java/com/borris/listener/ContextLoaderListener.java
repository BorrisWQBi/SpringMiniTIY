package com.borris.listener;

import com.borris.context.ApplicationContext;
import com.factory.AbstractBeanFactory;
import com.factory.AnnotationBeanFactory;
import org.apache.commons.lang3.StringUtils;

import javax.naming.OperationNotSupportedException;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;

public class ContextLoaderListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext sc = sce.getServletContext();
        //1.读取web.xml中的初始化参数
        String classLoc = sc.getInitParameter("contextConfigLocation");
        AbstractBeanFactory abf = AnnotationBeanFactory.getInstance();
        try {
            abf.initBeanFactoryByAnnotation(classLoc);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }





}
