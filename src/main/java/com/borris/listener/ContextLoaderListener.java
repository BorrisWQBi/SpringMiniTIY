package com.borris.listener;

import com.borris.context.ApplicationContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.IOException;
import java.util.List;

public class ContextLoaderListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
//        Properties properties = new Properties();
//        properties.load(this.getClass().getClassLoader().getResourceAsStream("application.properties"));
        ServletContext sc = sce.getServletContext();
        //1.读取web.xml中的初始化参数
        String classLoc = sc.getInitParameter("contextConfigLocation");
        try {
            List<String> paths = ApplicationContext.checkPaths(classLoc);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //2.根据初始化参数中填写的文件路径，读取相关文件的bean配置
        //懒得（划掉）没精力写xml解析器，此处只实现注解方式的编程式配置

        //3.根据配置文件的文件路径，扫描路径下所有class文件，用classloader装载

        //4.用工厂通过单例的方式初始化所有类，并保存进ApplicationContext中

        //5.所有类初始化之后，扫描所有类的成员变量，通过autowired识别并注入


    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
