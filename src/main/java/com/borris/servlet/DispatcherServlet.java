package com.borris.servlet;

import com.factory.AbstractBeanFactory;
import com.factory.RequestMapEntry;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class DispatcherServlet extends HttpServlet {

    private AbstractBeanFactory beanFactory;

    @Override
    public void init(ServletConfig config) {
        beanFactory = (AbstractBeanFactory) config.getServletContext().getAttribute("BeanFactory");
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)  {
        String url = req.getPathInfo();
        RequestMapEntry rme = beanFactory.getRequestMapEntry(url);
        if(rme !=null){
            Method method = rme.getMethod();
            String[] paraNames = rme.getParameterNames();
            Object[] objs = new Object[paraNames.length];
            for (int i = 0; i < paraNames.length; i++) {
                objs[i] = req.getParameter(paraNames[i]);
            }
            try {
                Object result = rme.invokeMethod(objs);
                resp.getWriter().write(result.toString());
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
